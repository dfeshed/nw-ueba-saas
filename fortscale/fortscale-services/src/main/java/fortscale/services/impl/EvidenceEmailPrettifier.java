package fortscale.services.impl;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.EmailEvidenceDecorator;
import fortscale.domain.core.Evidence;
import fortscale.services.EvidencePrettifierService;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.utils.prettifiers.BytesPrettifier;
import fortscale.utils.prettifiers.NumbersPrettifier;
import fortscale.utils.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by avivs on 21/01/16.
 */

@Service("evidenceEmailPrettifier")
public class EvidenceEmailPrettifier implements EvidencePrettifierService {

    @Autowired
    private DataEntitiesConfig dataEntitiesConfig;

    @Autowired
    private ApplicationConfigurationServiceImpl applicationConfigurationService;

    /**
     * Return a decorated indicator name
     *
     * @param evidence The name will be extracted from the evidence
     * @return Decorated indicator (evidence) name
     */
    private String decorateName(Evidence evidence) {

        // Get locale
        Locale locale = new Locale(Locale.ENGLISH.getLanguage());
        ApplicationConfiguration localeConfig = applicationConfigurationService
                .getApplicationConfigurationByKey("system.locale.settings");
        // If no locale is set, default on US, otherwise set the locale
        if (localeConfig != null) {
            locale = new Locale(localeConfig.getValue());
        }

        // Create key name
        String evidenceName = evidence.getAnomalyTypeFieldName();
        String msgKey = "messages." + locale.toString().toLowerCase() + ".evidence." + evidenceName;

        // Get evidence name
        ApplicationConfiguration evidenceNameMessage = applicationConfigurationService
                .getApplicationConfigurationByKey(msgKey);
        if (evidenceNameMessage != null) {
            evidenceName = evidenceNameMessage.getValue();
        }

        // Add Time Frame if exists
        if (evidence.getTimeframe() != null) {
            evidenceName += " (" + evidence.getTimeframe() + ")";
        }

        return evidenceName;
    }

    /**
     * Returns a decorated data entity. Basically takes the first data entity.
     *
     * @param dataEntitiesIds A list of data sources
     * @return The first data source or empty string
     */
    private String decorateDataEntityIds(List<String> dataEntitiesIds) {
        String entityId = "";
        if (dataEntitiesIds != null && dataEntitiesIds.size() > 0) {
            entityId = dataEntitiesIds.get(0);
        }
        String dataSource = "Unknown";
        try {
            dataSource = dataEntitiesConfig.getLogicalEntity(entityId).getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    /**
     * Pretifies anomaly value based on the recieved indicator
     *
     * @param evidence The indicator to dissect, and from which the anomalyValue is received.
     * @return Prettified anomaly value
     */
    private String decorateAnomalyValue(Evidence evidence) {

        String anomalyValue = evidence.getAnomalyValue();

        switch (evidence.getEvidenceType()) {
            case AnomalyAggregatedEvent:
                anomalyValue = NumbersPrettifier.truncateDecimalsOnNatural(anomalyValue);
                break;
            case AnomalySingleEvent:
                switch (evidence.getAnomalyTypeFieldName()) {
                    case "data_bucket":
                        anomalyValue = BytesPrettifier.ratePrettify(anomalyValue);
                        break;
                    case "event_time":
                        long date;
                        try {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s");
                            df.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date d = df.parse(anomalyValue);
                            date = d.getTime();
                        } catch (ParseException e) {
                            break;
                        } catch (NumberFormatException e) {
                            break;
                        }
                        anomalyValue = TimeUtils.getUtcFormat(new Date(date), "yyyy/MM/dd HH:mm");
                        break;
                }
                break;
        }

        return anomalyValue;
    }


    @Override
    public EmailEvidenceDecorator prettify(Evidence evidence) {
        EmailEvidenceDecorator decoratedEvidence = new EmailEvidenceDecorator(evidence);

        // Set pretty name
        decoratedEvidence.setName(decorateName(evidence));

        // Set data source
        decoratedEvidence.setDataSource(decorateDataEntityIds(evidence.getDataEntitiesIds()));

        // Set the anomaly value
        decoratedEvidence.setPrettifiedAnomalyValue(decorateAnomalyValue(evidence));

        return decoratedEvidence;
    }
}
