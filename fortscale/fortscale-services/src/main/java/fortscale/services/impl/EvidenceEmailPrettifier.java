package fortscale.services.impl;

import fortscale.domain.core.EmailEvidenceDecorator;
import fortscale.domain.core.Evidence;
import fortscale.services.EvidencePrettifierService;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.utils.prettifiers.BytesPrettifier;
import fortscale.utils.prettifiers.NumbersPrettifier;
import fortscale.utils.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by avivs on 21/01/16.
 */

@Service("evidenceEmailPrettifier")
public class EvidenceEmailPrettifier implements EvidencePrettifierService {

    @Autowired
    private DataEntitiesConfig dataEntitiesConfig;

    /**
     * Return a decorated indicator name
     *
     * @param evidence The name will be extracted from the evidence
     * @return Decorated indicator (evidence) name
     */
    private String decorateName(Evidence evidence) {
        return "";
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
                            date = Long.parseLong(anomalyValue);
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
