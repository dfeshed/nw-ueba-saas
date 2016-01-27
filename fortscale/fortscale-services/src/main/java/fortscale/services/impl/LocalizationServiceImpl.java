package fortscale.services.impl;

import fortscale.services.ApplicationConfigurationService;
import fortscale.services.LocalizationService;
import fortscale.utils.spring.SpringPropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by shays on 09/12/2015.
 */
@Component
public class LocalizationServiceImpl implements LocalizationService, InitializingBean {

    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    private static final String FORTSCALE_MESSAGES_PREFIX = "fortscale.message";
    private static final String FORTSCALE_MESSAGES_SEPERATOR = ".";
    private static final String LOCALIZATION_CONFIG_KEY = "system.locale.settings";

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

    @Override
    public Map<String, String> getAllLocalizationStrings(Locale locale) {
        Map<String, String> messages = normalizeResultKey(SpringPropertiesUtil.
                getPropertyMapByPrefix(FORTSCALE_MESSAGES_PREFIX));
        return messages;
    }

    @Override
    public Map<String, String> getAllLocalizationStringsByPrefix(String prefix, Locale locale) {
        String fullPrefix = normalizeKey(prefix);
        Map<String, String> messages = normalizeResultKey(SpringPropertiesUtil.getPropertyMapByPrefix(fullPrefix));
        return messages;

    }

    @Override
    public String getLocalizationStringByKey(String key, Locale locale) {
        return SpringPropertiesUtil.getProperty(key);
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
        return locale;
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
            String normalizedKey = StringUtils.removeStart(message.getKey(), FORTSCALE_MESSAGES_PREFIX +
                    FORTSCALE_MESSAGES_SEPERATOR);
            results.put(normalizedKey,message.getValue());

        }
        return  results;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, String> localizationConfig = new HashMap();
        localizationConfig.put(LOCALIZATION_CONFIG_KEY, DEFAULT_LOCALE.getLanguage().toLowerCase());
        applicationConfigurationService.updateConfigItems(localizationConfig);
        Map<String, String> localizationStrings = getAllLocalizationStrings(DEFAULT_LOCALE);
        Map<String, String> messagesForConfiguration = new HashMap();
        for (Map.Entry<String, String> message: localizationStrings.entrySet()) {
            String key = message.getKey().replaceAll(FORTSCALE_MESSAGES_PREFIX, "");
            key = "messages." + DEFAULT_LOCALE.getLanguage().toLowerCase() + "." + key;
            messagesForConfiguration.put(key, message.getValue());
        }
        applicationConfigurationService.insertConfigItems(messagesForConfiguration);
    }

}