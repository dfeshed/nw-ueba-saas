package fortscale.services;

import fortscale.domain.core.Evidence;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

/**
 * Created by shays on 09/12/2015.
 */

public interface  LocalizationService {

    /**
     *
     * @param locale - the languge that we like to present
     * @return
     */
    Map<String, String> getAllLocalizationStrings(Locale locale);

    /**
     * @param prefix - show all strings which start with thr prefix
     * @param locale - the languge that we like to present
     * @return
     */
    Map<String, String> getAllLocalizationStringsByPrefix(String prefix, Locale locale);


    /**
     * @param key - the specific key
     * @param locale - the language that we like to present
     * @return
     */
    String getLocalizationStringByKey(String key, Locale locale);

    /**
     * Get the default system locale
     * @return
     */
    Locale getDefaultLocale();

    /**
     * Get evidence name
     * @param evidence
     * @return
     */
    String getIndicatorName(Evidence evidence);
}
