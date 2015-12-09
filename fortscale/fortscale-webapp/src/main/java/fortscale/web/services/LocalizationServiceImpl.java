package fortscale.web.services;

import fortscale.utils.spring.SpringPropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

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
        return SpringPropertiesUtil.getPropertyMapByPrefix(FORTSCALE_MESSAGES_PREFIX);
    }

    @Override
    public Map<String, String> getAllLocalizationStringsByPrefix(String prefix, Locale locale) {

        locale = getLocaleOrDefaultLocale(locale);
        String fullPrefix = normalizeKey(prefix);
        return SpringPropertiesUtil.getPropertyMapByPrefix(fullPrefix);

    }

    @Override
    public Map<String, String> getAllLocalizationStringBykEY(String prefix, Locale locale) {
        locale = getLocaleOrDefaultLocale(locale);
        String fullPrefix = normalizeKey(prefix);
        return SpringPropertiesUtil.getPropertyMapByPrefix(fullPrefix);

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

    public Locale getDefaultLocale(){
        return DEFAULT_LOCALE;
    }

}
