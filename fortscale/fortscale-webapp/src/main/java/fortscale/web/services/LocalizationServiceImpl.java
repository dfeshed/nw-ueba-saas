package fortscale.web.services;

import fortscale.utils.spring.SpringPropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by shays on 09/12/2015.
 */
@Component
public class LocalizationServiceImpl implements  LocalizationService{

    private static final Locale DEFAULT_LOCALE = Locale.US;
    private final String FORTSCALE_MESSAGES_PREFIX = "fortscale.message";
    private final String FORTSCALE_MESSAGES_SEPERATOR = ".";


    @Override
    public Map<String, String> getAllLocalizationStrings(Locale locale) {
        locale = getLocaleOrDefaultLocale(locale);
        Map<String, String> messages = normalizeResultKey(SpringPropertiesUtil.getPropertyMapByPrefix(FORTSCALE_MESSAGES_PREFIX));
        return messages;
    }

    @Override
    public Map<String, String> getAllLocalizationStringsByPrefix(String prefix, Locale locale) {

        locale = getLocaleOrDefaultLocale(locale);
        String fullPrefix = normalizeKey(prefix);
        Map<String, String> messages = normalizeResultKey(SpringPropertiesUtil.getPropertyMapByPrefix(fullPrefix));
        return messages;

    }

    @Override
    public String getAllLocalizationStringByKey(String key, Locale locale) {
        locale = getLocaleOrDefaultLocale(locale);
        String fullPrefix = normalizeKey(key);

        String message = SpringPropertiesUtil.getProperty(key);
        return message;

    }

    public Locale getDefaultLocale(){
        return DEFAULT_LOCALE;
    }


    /**
     * Check if the key contain the prefix or not.
     * If not - adding the prefix.
     *
     * @param key
     * @return - the key with the messages prefix in the begining
     */
    private String normalizeKey(String key){
        String fullKey;
        if (StringUtils.startsWith(key, FORTSCALE_MESSAGES_PREFIX)){
            fullKey = key;
        } else {
            fullKey = FORTSCALE_MESSAGES_PREFIX+FORTSCALE_MESSAGES_SEPERATOR+key;
        }
        return fullKey;
    }

    private Locale getLocaleOrDefaultLocale(Locale locale){
        if (locale == null){
            return DEFAULT_LOCALE;
        }
        return  locale;
    }

    /**
     * remove the common prefix from all messages, FORTSCALE_MESSAGES_PREFIX
     * so the UI will not have to be familiar with it
     * @param messages
     * @return
     */
    private Map<String, String> normalizeResultKey(Map<String, String> messages){
        Map<String, String> results = new HashMap<>();
        for (Map.Entry<String, String> message :  messages.entrySet()){
            String normalizedKey = StringUtils.removeStart(message.getKey(), FORTSCALE_MESSAGES_PREFIX+FORTSCALE_MESSAGES_SEPERATOR);
            results.put(normalizedKey,message.getValue());

        }
        return  results;
    }



}
