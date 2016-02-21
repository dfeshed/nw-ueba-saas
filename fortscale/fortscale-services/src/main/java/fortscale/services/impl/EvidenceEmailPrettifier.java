package fortscale.services.impl;

import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.EmailEvidenceDecorator;
import fortscale.domain.core.Evidence;
import fortscale.services.EvidencePrettifierService;
import fortscale.services.LocalizationService;
import fortscale.utils.prettifiers.BytesPrettifier;
import fortscale.utils.prettifiers.NumbersPrettifier;
import fortscale.utils.time.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class EvidenceEmailPrettifier implements EvidencePrettifierService<EmailEvidenceDecorator> {

    @Autowired
    private DataEntitiesConfig dataEntitiesConfig;

    @Autowired
    public LocalizationService localizationService;

    private static Logger logger = LoggerFactory.getLogger(EvidenceEmailPrettifier.class);

    private static final String DATA_BUCKET_ANOMALY_TYPE_FIELD_NAME = "data_bucket";
    private static final String EVENT_TIME_ANOMALY_TYPE_FIELD_NAME = "event_time";




    /**
     * Return a decorated indicator name
     *
     * @param evidence The name will be extracted from the evidence
     * @return Decorated indicator (evidence) name
     */
    private String decorateName(Evidence evidence) {
       return localizationService.getIndicatorName(evidence);
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
            logger.error("dataEntitiesConfig.getLogicalEntity: Could not get logical entity for entityId " + entityId);
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
                    case DATA_BUCKET_ANOMALY_TYPE_FIELD_NAME:
                        anomalyValue = BytesPrettifier.ratePrettify(anomalyValue);
                        break;
                    case EVENT_TIME_ANOMALY_TYPE_FIELD_NAME:
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

    private String decorateStartDate (Evidence evidence) {
        Date date;

        try {
            date = new Date(evidence.getStartDate());
        } catch (RuntimeException e) {
            return "Unknown date";
        }

        return TimeUtils.getUtcFormat(date, "yyyy/MM/dd HH:mm");
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

        // Set pretty start date
        decoratedEvidence.setPrettyStartDate(decorateStartDate(evidence));

        return decoratedEvidence;
    }
}
