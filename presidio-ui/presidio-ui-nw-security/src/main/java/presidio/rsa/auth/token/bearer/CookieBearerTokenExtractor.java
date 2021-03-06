package presidio.rsa.auth.token.bearer;


import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CookieBearerTokenExtractor inspects request for access_token
 *
 *
 * Partial Copied from Lunch project- need to decide how to reuse code without being depended on all launch project
 *
 */
@Component
public class CookieBearerTokenExtractor extends BearerTokenExtractor {

    public static final String ACCESS_TOKEN = "access_token";
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Extract token from the header or query param of cookie
     * @param request
     * @return TokenBearerWrapper (origin or token  + token as string)
     * TODO: https://bedfordjira.na.rsa.net/browse/ASOC-55722
     */

    public TokenBearerWrapper retrieveToken(HttpServletRequest request) {
        TokenBearerOrigin origin = TokenBearerOrigin.HEADER;
        String tokenValue = extractHeaderToken(request);

        if (tokenValue == null){
            tokenValue = extractTokenFromQueryParam(request);
             origin = TokenBearerOrigin.QUERY_PARAM;
        }
        if (tokenValue == null && request.getCookies() !=null) {
            tokenValue = getTokenFromCookie(request, tokenValue);
            origin = TokenBearerOrigin.COOKIE;
        }

        logger.debug("Token: {}     retrieved from origin: {}",tokenValue,origin);
        return new TokenBearerWrapper(tokenValue,origin);
    }

    @Override
    protected String extractHeaderToken(HttpServletRequest request) {

//        return super.extractHeaderToken(request);
        return request.getHeader("access_token");
    }

    /**
     * Try to extract the token from the cookie
     * @param request
     * @param tokenValue
     * @return Token as string or null
     */
    private String getTokenFromCookie(HttpServletRequest request, String tokenValue) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(OAuth2AccessToken.ACCESS_TOKEN)) {
                tokenValue = cookie.getValue();
                break;
            }
        }
        return tokenValue;
    }

    /**
     * Try to extract the the token from the query param
     * @param request
     * @return token as string or null
     */
    private String extractTokenFromQueryParam(HttpServletRequest request) {
       String queryToken =  request.getParameter(ACCESS_TOKEN);
       if (StringUtils.isEmpty(queryToken)){
           return null;
       } else {
           request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, "Bearer");
           return queryToken;
       }
    }




    /**
     * This method remove the token from the query params (if exists) and set it to on the cookie
     * @param request
     * @param response
     */
    public void updateToken(HttpServletRequest request,HttpServletResponse response, TokenBearerWrapper tokenBearerWrapper) {

        if (TokenBearerOrigin.COOKIE.equals(tokenBearerWrapper.getOrigin())){
            //Do nothing
            return;
        }

        //If the token come from header or from query param
        if (tokenBearerWrapper.getToken()!=null){

            response.addCookie(new Cookie(ACCESS_TOKEN,tokenBearerWrapper.getToken()));
            logger.debug("Token set on cookie");
        }


        //If the token come from query param - redirect
        if(TokenBearerOrigin.QUERY_PARAM.equals(tokenBearerWrapper.getOrigin())) {
            String requestUrl = request.getRequestURI();

            requestUrl += removeTokenFromQueryString(request.getQueryString(), tokenBearerWrapper.getToken());

            try {
                response.sendRedirect(requestUrl);
                logger.debug("URL redirected to remove token. New URL: {}",requestUrl);

            } catch (IOException e) {
                e.printStackTrace();
            }




        }
    }

    /**
     * Return the query string without the access token if exists
     * @param queryString
     * @param token
     * @return
     */
    private String removeTokenFromQueryString(String queryString, String token) {

        if (queryString == null){
            return "";
        }
        queryString = queryString.replaceAll(ACCESS_TOKEN+"="+token,"");
        queryString = queryString.replaceAll("&&","&");
        return queryString;

    }
}

