package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.Evidence;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.LocalizationService;
import fortscale.services.cache.CacheHandler;
import fortscale.utils.configurations.ConfigrationServerClientUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    private static final String FORTSCALE_MESSAGES_TEMPLATE = FORTSCALE_MESSAGES_PREFIX+FORTSCALE_MESSAGES_SEPERATOR+"%s";

    private static final String UI_MESSAGE_PREFIX = "ui.message.";

    //Cache for messages. Refresh every hour,
    //Map locale name <String> to Map of <message_key,message_text>
    //I.E. <"IL",<"bla.ba", "text for key bla bla">
    @Autowired
    @Qualifier("messagesChache")
    private CacheHandler<Locale, Map<String,String>> messagesCache;

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;


    @Value("${languages.default}")
    private String defaultLocaleString;

    @Value("#{'${languages.supported}'.split(',')}")
    private List<String> languages;

    ConfigrationServerClientUtils configrationServerClientUtils = new ConfigrationServerClientUtils();
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
        DEFAULT_LOCALE = Locale.forLanguageTag(defaultLocaleString);

        localizationConfig.put(LOCALIZATION_CONFIG_KEY, DEFAULT_LOCALE.getLanguage());
        languages.forEach(langId -> this.loadLang(langId));


    }

    private Map<String, String> loadLang(String langId){


        try {
            Properties p = configrationServerClientUtils.readConfigurationAsProperties("ui-messages",langId);

            Map<String, String> langMap =
                    p.entrySet().stream()
                            .collect(Collectors.toMap(
                                    e -> e.getKey().toString().replaceFirst(UI_MESSAGE_PREFIX,""),
                                    e -> e.getValue().toString()
                            ));

            this.messagesCache.put(Locale.forLanguageTag(langId),langMap);

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