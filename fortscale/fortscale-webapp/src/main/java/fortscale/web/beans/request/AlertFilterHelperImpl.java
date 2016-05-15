package fortscale.web.beans.request;

import fortscale.domain.core.Alert;
import fortscale.domain.core.DataSourceAnomalyTypePair;
import org.springframework.data.domain.Sort;

import java.util.*;

/**
 * Created by shays on 04/05/2016.
 * Helper for AlertFilter.
 * Helps in common task like parsing of sort attributes, page request attribute and much more
 */

public class AlertFilterHelperImpl extends RequestFilterHelperImpl<AlertRestFilter,Alert> {

//    private static final String ANOMALY_TYPES_MAJOR_DELIMITER = "@@@";
//    private static final String ANOMALY_TYPES_MINOR_DELIMITER = "@@";

    //Constants
    protected static final int DEFAULT_PAGE_SIZE = 20;

    private static final String TIME_STAMP_START = "startDate";

    //Todo: remove when service and dao fill ge the filter itself
    public String getAlertStartRangeAsString(AlertRestFilter alertRestFilter) {
        //return alertRestFilter.getAlertStartRange().get(0).getTime() + "," + alertRestFilter.getAlertStartRange().get(1).getTime();
        return alertRestFilter.getAlertStartRange().get(0) + "," + alertRestFilter.getAlertStartRange().get(1);
    }

    protected Sort getSort(AlertRestFilter filterDTO){
        Sort sortByTSDesc;

        if (filterDTO.getSortField() != null) {
            Sort.Direction sortDir = Sort.Direction.DESC;
            if (filterDTO.getSortDirection() != null){
                sortDir = Sort.Direction.valueOf(filterDTO.getSortDirection());
            }
            sortByTSDesc = new Sort(new Sort.Order(sortDir, filterDTO.getSortField()));


            // If there the api get sortField, which different from TIME_STAMP_START, add
            // TIME_STAMP_START as secondary sort
            if (!TIME_STAMP_START.equals(filterDTO.getSortField())) {
                Sort secondarySort = new Sort(new Sort.Order(Sort.Direction.DESC, TIME_STAMP_START));
                sortByTSDesc = sortByTSDesc.and(secondarySort);
            }
        } else {
            sortByTSDesc = new Sort(new Sort.Order(Sort.Direction.DESC, TIME_STAMP_START));
        }

        return  sortByTSDesc;
    }



    public boolean isFilterEmpty(AlertRestFilter filterDTO){
        return filterDTO.getSeverity() == null && filterDTO.getStatus() == null  && filterDTO.getFeedback() == null
                &&  filterDTO.getAlertStartRange() == null &&
                filterDTO.getEntityName() == null && filterDTO.getEntityTags() == null && filterDTO.getEntityId() == null
                && filterDTO.getIndicatorTypes()== null;
    }

    public Date getStartFieldFromTime(AlertRestFilter filterDTO){
        return new Date(filterDTO.getAlertStartRange().get(0));
    }
    public Date getStartFieldToTime(AlertRestFilter filterDTO){
        return new Date(filterDTO.getAlertStartRange().get(1));
    }

    /**
     * Takes indicatorTypes as revieved from the front end, and parses it into  List<DataSourceAnomalyTypePair>
     * @param indicatorTypes string received from the front end. A csv of parseble values,
     *                          representing data source id to list of anomaly type field names
     * @return a List object with parsed values
     */
//    public Set<DataSourceAnomalyTypePair> digestIndicatorTypes(String indicatorTypes) {
//        if (indicatorTypes == null || indicatorTypes.length() ==  0){
//            return  null;
//        }
//        Set<DataSourceAnomalyTypePair> anomalyTypesList = new HashSet<>();
//
//        Arrays.asList(indicatorTypes.split(",")).forEach(indicatorTypeString -> {
//
//            String[] breakdown = indicatorTypeString.split(ANOMALY_TYPES_MAJOR_DELIMITER);
//
//            String dataSourceId = breakdown[0];
//            List<String> anomalyTypes = new ArrayList<>();
//
//            if(breakdown.length > 1) { //User select data source + indicator
//                Arrays.asList(breakdown[1].split(ANOMALY_TYPES_MINOR_DELIMITER)).forEach(anomalyType -> {
//                    anomalyTypesList.add(new DataSourceAnomalyTypePair(dataSourceId, anomalyType));
//                });
//            } else { // User select only data source, and need all the indicator for the data source
//                anomalyTypesList.add(new DataSourceAnomalyTypePair(dataSourceId, null));
//            }
//
//        });
//        return anomalyTypesList;
//    }
}
