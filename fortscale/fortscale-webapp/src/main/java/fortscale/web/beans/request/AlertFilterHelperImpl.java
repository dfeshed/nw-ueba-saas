package fortscale.web.beans.request;

import fortscale.domain.core.Alert;
import org.springframework.data.domain.Sort;

import java.util.Date;

/**
 * Created by shays on 04/05/2016.
 * Helper for AlertFilter.
 * Helps in common task like parsing of sort attributes, page request attribute and much more
 */

public class AlertFilterHelperImpl extends RequestFilterHelperImpl<AlertRestFilter,Alert> {

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
}
