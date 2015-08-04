package fortscale.services.dataqueries.querydto;

import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataentity.DataEntity;
import fortscale.services.dataentity.DataEntityField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rans on 23/07/15.
 * This is a helper class that helps generate a Data Query object to pass to the Data Query mechanism that will then generate SQL statement on Impala
 */
@Component
public class DataQueryHelper {


    @Value("${data.query.normalized_username.field:normalized_username}")
    private String normalizedUsernameField;

    @Value("${data.query.timestamp.field:event_time_utc}")
    private String dataEntityTimestampField;

    @Autowired
    private DataEntitiesConfig dataEntitiesConfig;

    public DataEntitiesConfig getDataEntitiesConfig() {
        return dataEntitiesConfig;
    }


    /**
     * Creates a data query based on passed parameters
     * @param dataEntityId - data entity to run the query for
     * @param defaultFieldsString
     * @param termsList
     * @param querySortList
     * @param dataEntityLimit
     * @return
     */
    public DataQueryDTO createDataQuery(String dataEntityId, String defaultFieldsString, List<Term> termsList, List<QuerySort> querySortList, int dataEntityLimit){
        //entity to forward
        DataQueryDTO dataQueryDTO = new DataQueryDTO();
            String[] entities = { dataEntityId };
            dataQueryDTO.setEntities(entities);

            //fields to forward - we don't necessarily want all the fields
            List<DataQueryField> fieldsList = createQueryFields(defaultFieldsString, dataEntityId);
            dataQueryDTO.setFields(fieldsList);

            //conditions on event time and score - not overflowing the client
            ConditionTerm conditionTerm = createDataQueryConditions(termsList);
            dataQueryDTO.setConditions(conditionTerm);

            //sort according to event times for continues forwarding
            dataQueryDTO.setSort(querySortList);

            //no limit when getting the data
            dataEntityLimit = (dataEntityLimit == -1) ? Integer.MAX_VALUE : dataEntityLimit;
            dataQueryDTO.setLimit(dataEntityLimit);
        return dataQueryDTO;
    }

    /**
     * Generate the list of fields to return in the SQL statement
     * @param defaultFieldsString a CSV list of fields to return. if '*' is passed, then return all fields
     * @param dataEntity the Data Entity to query
     * @return
     */
    private List<DataQueryField> createQueryFields(String defaultFieldsString, String dataEntity){
        List<DataQueryField> defaultFieldsList = new ArrayList<DataQueryField>();
        String[] defaultFields = defaultFieldsString.split(",");
        for (String field : defaultFields) {

            DataQueryField dataQueryField = new DataQueryField();
            if (field.equals("*")){
                dataQueryField.setAllFields(true);
                dataQueryField.setEntity(dataEntity);
            } else {
                dataQueryField.setId(field);
                dataQueryField.setAlias(getFieldDisplayName(field, dataEntity));
            }

            defaultFieldsList.add(dataQueryField);

        }
        return defaultFieldsList;
    }

    /**
     * Creates a Sort object for the Data Query object
     * @param sortField
     * @param sortDirection an object with wither ASC or DESC value
     * @return
     */
    public List<QuerySort> createQuerySort(String sortField, SortDirection sortDirection){
        List<QuerySort> querySortList = new ArrayList<QuerySort>();
        QuerySort queryUpdateTimestampSort = new QuerySort();
        DataQueryField dataQueryUpdateTimestampSortField = new DataQueryField();
        dataQueryUpdateTimestampSortField.setId(sortField);
        queryUpdateTimestampSort.setField(dataQueryUpdateTimestampSortField);
        queryUpdateTimestampSort.setDirection(sortDirection);
        querySortList.add(queryUpdateTimestampSort);
        return querySortList;
    }

    /**
     * Create a Data Query Conditions object for the data query
     * @param termsList list of Term objects that hold the conditions
     * @return
     */
    private ConditionTerm createDataQueryConditions(List<Term> termsList){
        ConditionTerm conditionTerm = new ConditionTerm();
        List<Term> terms = new ArrayList<Term>();

        for (Term term : termsList){
            terms.add(term);
        }
        conditionTerm.setTerms(terms);
        conditionTerm.setLogicalOperator(LogicalOperator.AND);
        return conditionTerm;
    }

    /**
     * a function that can be used to create a user id tern filter
     * @param userName the user name to look for
     * @return
     */
    public Term createUserTerm(String dataEntityId, String userName){
        ConditionField updateUserTerm = new ConditionField();
        updateUserTerm.setQueryOperator(QueryOperator.equals);
        updateUserTerm.setValue(userName);
        DataQueryField dataQueryUserField = new DataQueryField();
        dataQueryUserField.setId(normalizedUsernameField);
        dataQueryUserField.setEntity(dataEntityId);
        updateUserTerm.setField(dataQueryUserField);

        return updateUserTerm;
    }

    /**
     * Get the field name in an entity and return the display name for it.
     */
    private String getFieldDisplayName(String field, String entity) {

        // get the relevant data entity
        DataEntity dataEntity = dataEntitiesConfig.getEntityFromOverAllCache(entity);
        DataEntityField dataEntityField = dataEntity.getField(field);
        if (dataEntityField!=null && dataEntityField.getName()!=null)
            return dataEntityField.getName();
        else
            return field;

    }

    /**
     * a function that can be used to create a Data Range tern filter
     * @param startTime start date in range
     * @param endTime end date in range
     * @return
     */
    public Term createDateRangeTerm(String dataEntityId, long startTime, long endTime) {
        ConditionField updateTimestampTerm = new ConditionField();
        updateTimestampTerm.setQueryOperator(QueryOperator.between);
        updateTimestampTerm.setValue(startTime + "," + endTime);
        DataQueryField dataQueryUpdateTimestampField = new DataQueryField();
        dataQueryUpdateTimestampField.setEntity(dataEntityId);
        dataQueryUpdateTimestampField.setId(getDateFieldName(dataEntityId));
        updateTimestampTerm.setField(dataQueryUpdateTimestampField);
        return updateTimestampTerm;
    }




    /**
     * a function that can be used to get the name of timestamp field
     * @param dataEntityId - data entity to run the query for
     * @return
     */
    public String getDateFieldName(String dataEntityId) {
        switch (dataEntityId) {
        case "amt_session":
            return "end_time_utc";
        case "vpn_session":
            return "end_time_utc";
        default:
            return dataEntityTimestampField;
        }
    }
}
