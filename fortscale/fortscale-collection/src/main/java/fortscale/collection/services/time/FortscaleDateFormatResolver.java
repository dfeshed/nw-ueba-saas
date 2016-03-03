package fortscale.collection.services.time;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ApplicationConfigurationService;
import org.apache.pig.impl.util.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;

/**
 * @author gils
 * 03/03/2016
 */
public class FortscaleDateFormatResolver implements InitializingBean{

    private static final String DATE_FORMATS_KEY = "date.formats";

    private static final String DATE_FORMAT_DELIMITER = "###";
    private static final String EMPTY_STR = "";

    @Autowired
    ApplicationConfigurationService applicationConfigurationService;

    @Override
    public void afterPropertiesSet() throws Exception {
        LinkedList<String> availableInputFormats = FortscaleDateTimeFormats.getAvailableInputFormats();

        ApplicationConfiguration dateFormatsAppConfig = applicationConfigurationService.getApplicationConfigurationByKey(DATE_FORMATS_KEY);

        if (dateFormatsAppConfig == null || dateFormatsAppConfig.getValue() == null || EMPTY_STR.equals(dateFormatsAppConfig.getValue())) {
            applicationConfigurationService.insertConfigItem(DATE_FORMATS_KEY, StringUtils.join(availableInputFormats, DATE_FORMAT_DELIMITER));
        }
    }
}
