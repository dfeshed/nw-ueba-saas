package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.Evidence;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.LocalizationService;
import fortscale.services.cache.CacheHandler;
import fortscale.utils.configurations.ConfigrationServerClientUtils;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by shays on 09/12/2015.
 */
@Component
public class LocalizationServiceImpl implements LocalizationService, InitializingBean {

    private Locale DEFAULT_LOCALE;

    private static final String FORTSCALE_MESSAGES_PREFIX = "messages";
    private static final String FORTSCALE_MESSAGES_SEPERATOR = ".";
    private static final String LOCALIZATION_CONFIG_KEY = "system.locale.settings";
    private Logger logger = Logger.getLogger(this.getClass());


    private CacheHandler<Locale, Map<String,String>> messagesCache;


    @Value("${languages.default}")
    private String defaultLocaleString;

    @Value("#{'${languages.supported}'.split(',')}")
    private List<String> languages;

    ConfigrationServerClientUtils configrationServerClientUtils;

    public LocalizationServiceImpl(CacheHandler<Locale, Map<String, String>> messagesCache,
                                   ConfigrationServerClientUtils configrationServerClientUtils) {
        this.messagesCache = messagesCache;
        this.configrationServerClientUtils = configrationServerClientUtils;
    }

    @Override
    public Map<String, String> getAllLocalizationStrings(Locale locale) {

        return getUpdatedLanguageMap(locale);
    }



    @Override
    public String getLocalizationStringByKey(String key, Locale locale) {

        Map<String, String> localizationMap = getUpdatedLanguageMap(locale);

        return localizationMap==null?null:localizationMap.get(key);
    }

    private Map<String, String> getUpdatedLanguageMap(Locale locale) {
        Map<String,String> localizationMap = messagesCache.get(locale);


        if (localizationMap == null) {
            localizationMap = loadLang(locale.getLanguage());
            if (localizationMap != null) {

                messagesCache.put(locale, localizationMap);
            }
        }
        return localizationMap;
    }

    public Locale getDefaultLocale(){
        return DEFAULT_LOCALE;
    }




    private String getLocaleOrDefaultLocaleLanguage(Locale locale){
        if (locale == null){
            return DEFAULT_LOCALE.getLanguage();
        }
        return locale.getLanguage();
    }



    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, String> localizationConfig = new HashMap();
        DEFAULT_LOCALE = getLocaleFromString(defaultLocaleString);

        localizationConfig.put(LOCALIZATION_CONFIG_KEY, DEFAULT_LOCALE.getLanguage());
        languages.forEach(langId -> this.loadLang(langId));


    }

    @Override
    public Map<String,Map<String, String>> getMessagesToAllLanguages(){
        Map<String, Map<String, String>> all = new HashMap<>();
        languages.forEach(langId->{
            Locale locale = getLocaleFromString(langId);

            Map<String, String> localeSettings = this.messagesCache.get(locale);
            //If not in cache - reload
            if (CollectionUtils.isEmpty(localeSettings)){
                loadLang(langId);
                localeSettings = this.messagesCache.get(locale);
            }
            //If still not in cache - report error and continue
            if (CollectionUtils.isEmpty(localeSettings)) {
                logger.error("Cannot load language {}",langId);
            }
            all.put(langId,localeSettings);
        });

        return all;
    }


    private Locale getLocaleFromString(String localeText){
        String langOnly=null;
        String region = null;
        if (localeText.contains("_")){
            langOnly = localeText.split("_")[0];
            region = localeText.split("_")[1];

        }else {
            langOnly = localeText;

        }
        return new Locale.Builder().setLanguage(langOnly).setRegion(region).build();


    }

    private Map<String, String> loadLang(String langId){


        try {
            Properties p = configrationServerClientUtils.readConfigurationAsProperties("ui-messages",langId);

            Map<String, String> langMap =
                    p.entrySet().stream()
                            .collect(Collectors.toMap(
                                    e -> e.getKey().toString(),
                                    e -> e.getValue().toString()
                            ));

            this.messagesCache.put(getLocaleFromString(langId),langMap);

            return langMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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


    public String getAlertName(Alert alert){
        String messageKey = "alert."+alert.getName() +".name";

        String name = getLocalizationStringByKey(messageKey,getDefaultLocale());
        //If display name still no configured, display the raw name
        if (StringUtils.isBlank(name)){
            alert.getName();
        }
        return (String)name;

    }




}