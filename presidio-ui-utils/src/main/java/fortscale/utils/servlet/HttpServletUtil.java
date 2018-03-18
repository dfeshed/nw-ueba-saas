package fortscale.utils.servlet;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import ch.qos.logback.access.pattern.AccessConverter;

public class HttpServletUtil {
	private HttpServletUtil (){}
	
	public static final String getFullURL(HttpServletRequest request, String path){
		String contextPath = request.getContextPath();
		String serverName = request.getServerName();
		int port = request.getServerPort();
		String scheme = request.getScheme();

		if(!contextPath.startsWith("/")){
			contextPath = "/" + contextPath;
		}

		if(path.startsWith("/") && contextPath.endsWith("/")){
			path = path.substring(1);
		}
		
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(scheme).
			append("://").
			append(serverName);
		if(port != 80 && port != 443 && port > 0){
			strBuilder.append(':').
			append(port);
		}
		strBuilder.append(contextPath).append(path);
		return strBuilder.toString();
	}
	
	public static Map<String,String> getQueryParams(String uri){
		Map<String,String> params = new HashMap<String, String>();
		try {
			URL url = new URL(uri);
			String queryString = url.getQuery();
			if(!StringUtils.isEmpty(queryString)){
				for (String pair : queryString.split("&")) {
			        int eq = pair.indexOf("=");
			        if (eq < 0) {
			            // key with no value
			            params.put(URLDecoder.decode(pair, "UTF-8"), "");
			        } else {
			            // key=value
			            String key = URLDecoder.decode(pair.substring(0, eq), "UTF-8");
			            String value = URLDecoder.decode(pair.substring(eq + 1), "UTF-8");
			            params.put(key, value);
			        }
			    }
			}
		} catch (MalformedURLException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return params;
	}
	
	public static final String getAccessEventRequestURL(HttpServletRequest request){
		String requestURL = null;
        StringBuffer buf = new StringBuffer();
        buf.append(request.getMethod());
        buf.append(AccessConverter.SPACE_CHAR);
        buf.append(request.getRequestURI());
        final String qStr = request.getQueryString();
        if (qStr != null) {
          buf.append(AccessConverter.QUESTION_CHAR);
          buf.append(qStr);
        }
        buf.append(AccessConverter.SPACE_CHAR);
        buf.append(request.getProtocol());
        requestURL = buf.toString();
        return requestURL;
	}
}
