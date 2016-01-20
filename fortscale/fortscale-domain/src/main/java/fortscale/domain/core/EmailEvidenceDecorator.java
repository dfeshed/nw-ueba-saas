package fortscale.domain.core;

import fortscale.utils.prettifiers.BytesPrettifier;
import fortscale.utils.prettifiers.NumbersPrettifier;
import fortscale.utils.time.TimeUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by avivs on 20/01/16.
 */
public class EmailEvidenceDecorator extends Evidence{

    private String prettyName = "";
    private String dataSource = "";
    private String prettifiedAnomalyValue = "";


    /**
     * Return a decorated indicator name
     * @param name The original name
     * @param timeFrame Should be 'Daily' or 'Hourly'
     * @return Decorated indicator (evidence) name
     */
    private static String decorateName(String name, EvidenceTimeframe timeFrame) {
        if (timeFrame != null) {
            name += " (" + timeFrame.toString() + ")";
        }
        return name;
    }

    /**
     * Returns a decorated data entity. Basically takes the first data entity.
     * @param dataEntitiesIds A list of data sources
     * @return The first data source or empty string
     */
    private static String decorateDataEntityIds(List<String> dataEntitiesIds) {
        String dataSource = "";
        if (dataEntitiesIds != null && dataEntitiesIds.size() > 0) {
            dataSource = dataEntitiesIds.get(0);
        }
        return dataSource;
    }

    /**
     * Pretifies anomaly value based on the recieved indicator
     * @param evidence The indicator to dissect, and from which the anomalyValue is received.
     * @return Prettified anomaly value
     */
    private static String decorateAnomalyValue (Evidence evidence) {

        String anomalyValue = evidence.getAnomalyValue();

        switch(evidence.getEvidenceType()) {
            case AnomalyAggregatedEvent:
                anomalyValue = NumbersPrettifier.truncateDecimalsOnNatural(anomalyValue);
                break;
            case AnomalySingleEvent:
                switch(evidence.getAnomalyType()) {
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


    public EmailEvidenceDecorator() {}

    /**
     *
     * @param evidence The evidence to be decorated
     */
    public EmailEvidenceDecorator(Evidence evidence) {
        super(evidence);

        // Set pretty name
        this.prettyName = decorateName(this.getName(), this.getTimeframe());

        // Set data source
        this.dataSource = decorateDataEntityIds(this.getDataEntitiesIds());

        // Set the anomaly value
        this.prettifiedAnomalyValue = decorateAnomalyValue(this);
    }


}
