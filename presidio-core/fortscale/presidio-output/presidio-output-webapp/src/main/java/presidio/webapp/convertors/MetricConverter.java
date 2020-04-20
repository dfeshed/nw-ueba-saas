package presidio.webapp.convertors;

import presidio.monitoring.records.MetricDocument;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.webapp.model.Metric;

public class MetricConverter implements RestPersistencyConvertor<MetricDocument,Metric> {


    public static final String JAVA_LANG_NUMBER = "java.lang.Number";

    @Override
    public MetricDocument convertFromRestToPersistent(Metric restObject) {
        return null;
    }

    @Override
    public Metric convertFromPersistentToRest(MetricDocument persistantObject) {

        return convertFromPersistentToRest(persistantObject,MetricEnums.MetricValues.DEFAULT_METRIC_VALUE);
    }


    public Metric convertFromPersistentToRest(MetricDocument persistantObject, MetricEnums.MetricValues valueType) {

        Number value = persistantObject.getValue().get(valueType);
        Metric metric = new Metric(
                persistantObject.getName(),
                value,
                JAVA_LANG_NUMBER,
                persistantObject.getTimestamp().toInstant(),
                persistantObject.getLogicTime().toInstant()
        );
        return metric;
    }
}
