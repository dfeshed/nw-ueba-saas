package fortscale.web.rest;


import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;
import fortscale.services.LocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;

/**
 * Created by shays on 09/12/2015.
 */
@Controller
@RequestMapping("/api/messages")
public class ApiLocalization {

    @Autowired
    private LocalizationService localizationService;


    /**
     * the api to return all localization messages. GET: /api/messages
     * * Get all localizations according to selected language
     * @param httpRequest
     * @param httpResponse
     * @return
     */
    @RequestMapping(value="/{locale}", method = RequestMethod.GET)
    @LogException
    public @ResponseBody
    DataBean<Map<String, String>> getMessages(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
                                              @PathVariable("locale") Locale locale){

        Map<String, String> messages = localizationService.getAllLocalizationStrings(locale);
        DataBean<Map<String, String>> results = new DataBean<>();
        results.setData(messages);
        return results;
    }


    /**
     * Get all localization messages according to default language
     * @param httpRequest
     * @param httpResponse
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @LogException
    public @ResponseBody
    DataBean<Map<String, String>> getMessages(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
        return getMessages(httpRequest, httpResponse, localizationService.getDefaultLocale());
    }

}
