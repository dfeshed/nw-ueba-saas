package fortscale.services.presidio.core.converters;

import fortscale.utils.logging.Logger;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import presidio.output.client.model.AlertQuery;
import presidio.output.client.model.UserQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shays on 10/09/2017.
 */
public class PageConverter {
    private static final String SORT_SEPERATOR = ":";
    Logger logger = Logger.getLogger(this.getClass());


    public Integer convertUiFilterToQueryDtoPageNumber(PageRequest pageRequest) {
        if (pageRequest == null || pageRequest.getPageNumber()<0){
            return null;

        }
        return pageRequest.getPageNumber();
    }

    public Integer convertUiFilterToQueryDtoPageSize(PageRequest pageRequest) {
        if (pageRequest == null || pageRequest.getPageSize()<1){
            return null;

        }
        return pageRequest.getPageSize();
    }


    public List<AlertQuery.SortFieldNamesEnum> convertUiFilterToQueryDtoAlertSortFields(PageRequest pageRequest) {
        if (pageRequest==null || pageRequest.getSort()==null){
            return null;

        }
        List<AlertQuery.SortFieldNamesEnum> sortByFields = new ArrayList<>();
        pageRequest.getSort().forEach(field->{
            switch(field.getProperty()){
                case "name": break;
                case "severityCode":
                        sortByFields.add(AlertQuery.SortFieldNamesEnum.SCORE);
                        break;
                case "score":
                    sortByFields.add(AlertQuery.SortFieldNamesEnum.SCORE);
                    break;
                case "entityName": sortByFields.add(AlertQuery.SortFieldNamesEnum.USER_NAME); break;
                case "startDate": sortByFields.add(AlertQuery.SortFieldNamesEnum.START_DATE);break;
                case "indicatorsNum":
                    sortByFields.add(AlertQuery.SortFieldNamesEnum.INDICATORS_NUM);
                    break;
                case "feedback":
                    sortByFields.add(AlertQuery.SortFieldNamesEnum.FEEDBACK);
                    break;

            }
        });
        if (sortByFields.isEmpty()){
            return null;
        } else {
            return sortByFields;
        }


    }

    public List<UserQuery.SortFieldNamesEnum> convertUiFilterToQueryDtoUserSortFields(PageRequest pageRequest) {
        if (pageRequest==null || pageRequest.getSort()==null){
            return null;

        }
        List<UserQuery.SortFieldNamesEnum> sortByFields = new ArrayList<>();
        pageRequest.getSort().forEach(field->{
            switch(field.getProperty()){
                case "name":
                    sortByFields.add(UserQuery.SortFieldNamesEnum.USER_DISPLAY_NAME);
                    break;
                case "score":
                    sortByFields.add(UserQuery.SortFieldNamesEnum.SCORE);
                    break;
                case "alertsCount":
                    sortByFields.add(UserQuery.SortFieldNamesEnum.ALERT_NUM);
                    break;

            }
        });
        if (sortByFields.isEmpty()){
            return null;
        } else {
            return sortByFields;
        }


    }

    public AlertQuery.SortDirectionEnum convertUiFilterToQueryDtoSortDirectionForAlert(PageRequest pageRequest) {
        if (pageRequest==null || pageRequest.getSort()==null){
            return null;

        }
        List<AlertQuery.SortDirectionEnum> list = new ArrayList<>();

        pageRequest.getSort().forEach(field->{
            if (Sort.Direction.ASC.equals(field.getDirection())){
                list.add(AlertQuery.SortDirectionEnum.ASC);
            } else {
                list.add(AlertQuery.SortDirectionEnum.DESC);
            }
        });

        if (list.size()>0){
            return list.get(0);
        }
        return null;
    }

    public UserQuery.SortDirectionEnum convertUiFilterToQueryDtoSortDirectionForUser(PageRequest pageRequest) {
        if (pageRequest==null || pageRequest.getSort()==null){
            return null;

        }
        List<UserQuery.SortDirectionEnum> list = new ArrayList<>();

        pageRequest.getSort().forEach(field->{
            if (Sort.Direction.ASC.equals(field.getDirection())){
                list.add(UserQuery.SortDirectionEnum.ASC);
            } else {
                list.add(UserQuery.SortDirectionEnum.DESC);
            }
        });

        if (list.size()>0){
            return list.get(0);
        }
        return  null;
    }
}
