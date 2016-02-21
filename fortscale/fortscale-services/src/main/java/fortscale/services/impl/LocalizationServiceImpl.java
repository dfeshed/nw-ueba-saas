package fortscale.services.impl;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.Evidence;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.LocalizationService;
import fortscale.services.cache.CacheHandler;
import fortscale.utils.spring.SpringPropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private static final String FORTSCALE_MESSAGES_PROPERTIES_FILE_PREFIX = "fortscale.message";
    private static final String FORTSCALE_MESSAGES_PREFIX = "messages";
    private static final String FORTSCALE_MESSAGES_SEPERATOR = ".";
    private static final String LOCALIZATION_CONFIG_KEY = "system.locale.settings";
    private static final String FORTSCALE_MESSAGES_TEMPLATE = FORTSCALE_MESSAGES_PREFIX+FORTSCALE_MESSAGES_SEPERATOR+"%s";

    //Cache for messages. Refresh every hour,
    //If display names will be changed in mongo, the change will be affective after one hour or less
    @Autowired
    @Qualifier("messagesChache")
    private CacheHandler<String, String> messagesCache;

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

    @Override
    public Map<String, String> getAllLocalizationStrings(Locale locale) {

        String namespace = String.format(FORTSCALE_MESSAGES_TEMPLATE,getLocaleOrDefaultLocale(locale));
        Map<String, String> messages  = applicationConfigurationService.getApplicationConfigurationByNamespace(namespace);
        updateCache(messages);
        return messages;
    }



    @Override
    public Map<String, String> getAllLocalizationStringsByPrefix(String prefix, Locale locale) {

        prefix = normalizeKey(prefix,locale);
        String namespace = normalizeKey(prefix,locale);
        Map<String, String> messages  = applicationConfigurationService.getApplicationConfigurationByNamespace(namespace);
        updateCache(messages);
        return messages;

    }

    @Override
    public String getLocalizationStringByKey(String key, Locale locale) {
        key = normalizeKey(key,locale);
        String value = messagesCache.get(key);

        if (value==null) {
            ApplicationConfiguration conf = applicationConfigurationService.getApplicationConfigurationByKey(key);

            if (conf != null) {
                value = conf.getValue();
                messagesCache.put(key, value);
            }
        }

        return value;
    }

    public Locale getDefaultLocale(){
        return DEFAULT_LOCALE;
    }


    /**
     * Check if the key contain the prefix or not, or partial containing the key
     * If not - adding the prefix.
     *
     * @param key
     * @return - the key with the messages prefix in the begining
     */
    private String normalizeKey(String key,Locale locale){

        String namespace = String.format(FORTSCALE_MESSAGES_TEMPLATE,getLocaleOrDefaultLocale(locale));

        //Key in convention fortscale.<locale>.*
        if (StringUtils.startsWith(key, namespace)){
            return key;
        }
        //Key in convention fortscale.<other parts of the key>
        if (StringUtils.startsWith(key, FORTSCALE_MESSAGES_PREFIX+FORTSCALE_MESSAGES_SEPERATOR)){
            //Remove 'fortscale.'
            key = key.replace(FORTSCALE_MESSAGES_PREFIX+FORTSCALE_MESSAGES_SEPERATOR,"");
        }

        String fullKey = String.format(FORTSCALE_MESSAGES_TEMPLATE, getLocaleOrDefaultLocale(locale));
        fullKey += "."+key;

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
        localizationConfig.put(LOCALIZATION_CONFIG_KEY, DEFAULT_LOCALE.getLanguage());
        applicationConfigurationService.updateConfigItems(localizationConfig);
        //Map<String, String> localizationStrings = getAllLocalizationStrings(DEFAULT_LOCALE);
        Map<String, String> localizationStrings = normalizeResultKey(SpringPropertiesUtil.
                getPropertyMapByPrefix(FORTSCALE_MESSAGES_PROPERTIES_FILE_PREFIX));
        Map<String, String> messagesForConfiguration = new HashMap();
        for (Map.Entry<String, String> message: localizationStrings.entrySet()) {
            String key = message.getKey().replaceAll(FORTSCALE_MESSAGES_PROPERTIES_FILE_PREFIX+".", "");
            key = "messages." + DEFAULT_LOCALE.getLanguage().toLowerCase() + "." + key;
            messagesForConfiguration.put(key, message.getValue());
        }
        applicationConfigurationService.insertConfigItems(messagesForConfiguration);
    }

    public String getIndicatorName(Evidence evidence) {

        //Assumption - only one data source
        String dataSource = evidence.getDataEntitiesIds().get(0);


        //messageKey - prefix and anomaly type only
        String messageKey = "evidence."+evidence.getAnomalyTypeFieldName();

        //messageKeyByDataSource - prefix and anomaly type + datasource name
        String messageKeyByDataSource = "evidence."+dataSource+"."+evidence.getAnomalyTypeFieldName();

        //try get the display name according to anomaly and data source
        String name = getLocalizationStringByKey(messageKeyByDataSource,getDefaultLocale());
        if (name ==null) {
            //If display name didn't found, get the display name according to anomaly only
            name = getLocalizationStringByKey(messageKey, getDefaultLocale());
        }

        //If display name still no configured, display the anomaly raw type
        if (StringUtils.isBlank(name)){
            name=evidence.getAnomalyTypeFieldName();
        }
        return (String)name;
    }

    //Set all the messages in the cache at once
    private void updateCache(Map<String, String> messages) {
        for (Map.Entry<String,String> message : messages.entrySet()){
            messagesCache.put(message.getKey(), message.getValue());;
        }
    }
}