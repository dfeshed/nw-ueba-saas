package fortscale.domain.rest;

import fortscale.domain.core.DataSourceAnomalyTypePair;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.*;
import java.util.List;

/**
 * Created by shays on 11/05/2016.
 */
public class DataSourceAnomalyTypePairListWrapperPropertyEditor extends PropertyEditorSupport {

    private static final String ANOMALY_TYPES_MAJOR_DELIMITER = "@@@";
    private static final String ANOMALY_TYPES_MINOR_DELIMITER = "@@";

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        AlertRestFilter.DataSourceAnomalyTypePairListWrapper anomalys = new AlertRestFilter.DataSourceAnomalyTypePairListWrapper();
        Set<DataSourceAnomalyTypePair> anomalysList = digestIndicatorTypes(text);

        anomalys.setAnomalyList(digestIndicatorTypes(text));
        setValue(anomalys);
    }

    /**
     * Takes indicatorTypes as revieved from the front end, and parses it into  List<DataSourceAnomalyTypePair>
     * @param indicatorTypes string received from the front end. A csv of parseble values,
     *                          representing data source id to list of anomaly type field names
     * @return a List object with parsed values
     */
    public Set<DataSourceAnomalyTypePair> digestIndicatorTypes(String indicatorTypes) {
        if (indicatorTypes == null || indicatorTypes.length() ==  0){
            return  null;
        }
        Set<DataSourceAnomalyTypePair> anomalyTypesList = new HashSet<>();

        Arrays.asList(indicatorTypes.split(",")).forEach(indicatorTypeString -> {

            String[] breakdown = indicatorTypeString.split(ANOMALY_TYPES_MAJOR_DELIMITER);

            String dataSourceId = breakdown[0];
            java.util.List<String> anomalyTypes = new ArrayList<>();

            if(breakdown.length > 1) { //User select data source + indicator
                Arrays.asList(breakdown[1].split(ANOMALY_TYPES_MINOR_DELIMITER)).forEach(anomalyType -> {
                    anomalyTypesList.add(new DataSourceAnomalyTypePair(dataSourceId, anomalyType));
                });
            } else { // User select only data source, and need all the indicator for the data source
                anomalyTypesList.add(new DataSourceAnomalyTypePair(dataSourceId, null));
            }

        });
        return anomalyTypesList;
    }
}
