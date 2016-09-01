package fortscale.domain.core.dao;


import com.google.common.collect.Lists;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import fortscale.domain.core.*;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.domain.dto.DateRange;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


//imports as static
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;



import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;


/**
 * Created by rans on 21/06/15.
 */
public class AlertsRepositoryImpl implements AlertsRepositoryCustom {

	@Autowired private MongoTemplate mongoTemplate;

	@Autowired MongoDbRepositoryUtil mongoDbRepositoryUtil;

	private static Logger logger = LoggerFactory.getLogger(AlertsRepositoryImpl.class);


	/**
	 * returns all alerts in the collection in a json object represented by @Alerts
	 *
	 * @param pageRequest
	 * @return
	 */
	@Override public Alerts findAll(PageRequest pageRequest) {
		Query query = new Query().with(pageRequest.getSort());
		int pageSize = pageRequest.getPageSize();
		int pageNum = pageRequest.getPageNumber();
		query.limit(pageSize);
		query.skip(pageNum * pageSize);
		List<Alert> alertsList = mongoTemplate.find(query, Alert.class);
		Alerts alerts = new Alerts();
		alerts.setAlerts(alertsList);
		return alerts;
	}

	@Override public Long count(PageRequest pageRequest) {
		Query query = new Query().with(pageRequest.getSort());
		query.limit(pageRequest.getPageSize());
		query.skip(pageRequest.getPageNumber());
		Long count = mongoTemplate.count(query, Alert.class);
		return count;
	}

	/**
	 * Adds alert object to the Alerts collection
	 *
	 * @param alert
	 */
	@Override public void add(Alert alert) {
		mongoTemplate.insert(alert);
	}

	/**
	 * gets a single alert according to id
	 *
	 * @param id
	 * @return
	 */
	@Override public Alert getAlertById(String id) {
		return mongoTemplate.findById(id, Alert.class);
	}

	@Override
	public Alerts findAlertsByFilters(PageRequest pageRequest, String severityArrayFilter, String statusArrayFilter,
			String feedbackArrayFilter, DateRange dateRangeFilter, String entityName, Set<String> entitiesIds,
									  Set<DataSourceAnomalyTypePair> indicatorTypes) {

		//build the query
		Query query = buildQuery(pageRequest, Alert.severityField, Alert.statusField, Alert.feedbackField,
				Alert.startDateField, Alert.entityNameField, severityArrayFilter, statusArrayFilter,
				feedbackArrayFilter, dateRangeFilter, entityName, entitiesIds, pageRequest, indicatorTypes);
		List<Alert> alertsList = mongoTemplate.find(query, Alert.class);
		Alerts alerts = new Alerts();
		alerts.setAlerts(alertsList);
		return alerts;
	}

	@Override
	public Long countAlertsByFilters(PageRequest pageRequest, String severityArrayFilter, String statusArrayFilter,
									 String feedbackArrayFilter, DateRange dateRangeFilter, String entityName, Set<String> entitiesIds, Set<DataSourceAnomalyTypePair> indicatorTypes) {

		//build the query
		Query query = buildQuery(pageRequest, Alert.severityField, Alert.statusField, Alert.feedbackField,
				Alert.startDateField, Alert.entityNameField, severityArrayFilter, statusArrayFilter,
				feedbackArrayFilter, dateRangeFilter, entityName, entitiesIds, pageRequest, indicatorTypes);
		return mongoTemplate.count(query, Alert.class);
	}

	public Map<String, Integer> groupCount(String fieldName, String severityArrayFilter, String statusArrayFilter,
										   String feedbackArrayFilter, DateRange dateRangeFilter, String entityName,
										   Set<String> entitiesIds, Set<DataSourceAnomalyTypePair> indicatorTypes){
		Criteria criteria = getCriteriaForGroupCount(severityArrayFilter, statusArrayFilter, feedbackArrayFilter, dateRangeFilter, entityName, entitiesIds, indicatorTypes);
		return mongoDbRepositoryUtil.groupCount(fieldName, criteria, "alerts");


	}

	@Override
	public List<Alert> getAlertSummary(List<String> severities, long endDate) {
		Query query = new Query();
		query.addCriteria(where(Alert.endDateField).gte(endDate))
				.addCriteria(where(Alert.severityField).in(severities))
				.with(new Sort(Sort.Direction.DESC, Alert.scoreField))
				.with(new Sort(Sort.Direction.DESC, Alert.endDateField))
				.limit(10);
		return mongoTemplate.find(query, Alert.class);
	}

	@Override
	public List<Alert> getAlertsByTimeRange(DateRange dateRange, List<String> severities, boolean excludeEvidences){
		long startDate =  TimestampUtils.convertToMilliSeconds(dateRange.getFromTime());
        long endDate =  TimestampUtils.convertToMilliSeconds(dateRange.getToTime());
		Query query = new Query();

		query.addCriteria(where(Alert.endDateField).lte(endDate))
				.addCriteria(where(Alert.startDateField).gte(startDate))
                .with(new Sort(Sort.Direction.DESC, Alert.scoreField))
                .with(new Sort(Sort.Direction.DESC, Alert.endDateField));

		if (severities!=null && severities.size() != 0) {
			query.addCriteria(where(Alert.severityField).in(severities));
		}

        if (excludeEvidences){
            query.fields().exclude(Alert.evidencesField);
        }
		return mongoTemplate.find(query, Alert.class);
	}

	@Override
	public void removeRedundantAlertsForUser(String username, String alertId) {
		Query query = new Query();
		query.addCriteria(where(Alert.entityNameField).is(username)).addCriteria(where(Alert.ID_FIELD).ne(alertId));
		mongoTemplate.remove(query, Alert.class);
	}

	/**
	 * Build a query to be used by mongo API
	 *
	 * @param pageRequest
	 * @param severityFieldName   name of the field to access severity property
	 * @param statusFieldName     name of the field to access status property
	 * @param feedbackFieldName   name of the field to access feedback property
	 * @param severityArrayFilter comma separated list of severity attributes to include
	 * @param statusArrayFilter   comma separated list of status attributes to include
	 * @param feedbackArrayFilter comma separated list of feedback attributes to include
	 * @param users   		  	  set of users to search if alerts contain them
	 * @param pageable
	 * @return
	 */
	private Query buildQuery(PageRequest pageRequest, String severityFieldName, String statusFieldName,
							 String feedbackFieldName, String startDateFieldName, String entityFieldName,
							 String severityArrayFilter, String statusArrayFilter, String feedbackArrayFilter,
							 DateRange dateRangeFilter, String entityFilter, Set<String> users, Pageable pageable, Set<DataSourceAnomalyTypePair> indicatorTypes) {

		Query query = new Query().with(pageRequest.getSort());

		//Get list of criteria
		List<Criteria> criteriaList = getCriteriaList(severityFieldName, statusFieldName, feedbackFieldName,
				startDateFieldName, entityFieldName, severityArrayFilter, statusArrayFilter, feedbackArrayFilter,
				dateRangeFilter, entityFilter, users, indicatorTypes);

		//Add the criterias to the query
		for (Criteria criteria : criteriaList){
			query.addCriteria(criteria);
		}

		int pageSize = pageRequest.getPageSize();
		int pageNum = pageRequest.getPageNumber();
		query.limit(pageSize);
		query.skip(pageNum * pageSize);
		if (pageable != null) {
			query.with(pageable);
		}
		return query;
	}

	/**
	 * Count how many alerts we have with the same name , in the same time
	 *
	 * @return number of alerts
	 */
	@Override
	public long buildQueryForAlertByTimeAndName(String alertName, long startTime, long endTime) {

		Query query = new Query();

		//Get list of criteria
		List<Criteria> criteriaList = new ArrayList<>();
		Criteria nameCriteria =  where(Alert.nameField).is(alertName);
		criteriaList.add(nameCriteria);

		Criteria startTimeCriteria = where(Alert.startDateField).is(startTime);
		criteriaList.add(startTimeCriteria);

		Criteria endTimeCriteria =  where(Alert.endDateField).is(endTime);
		criteriaList.add(endTimeCriteria);



		//Add the criterias to the query
		for (Criteria criteria : criteriaList){
			query.addCriteria(criteria);
		}

		long result = mongoTemplate.count(query,Alert.class);

		return result;
	}


    @Override
    public Set<DataSourceAnomalyTypePair> getDataSourceAnomalyTypePairs(){

        //unwind - Split each alert with set of anomalyTypes to alert with single anomaly type
        AggregationOperation unwindOperationStep = Aggregation.unwind(Alert.anomalyTypeField);

        //group - group the single couples of anomayType-dataSource to set of _id: {anomalyType: "anomalyType", dataSource:"dataSource")
        //          without duplicates
        AggregationOperation groupOprationStep = Aggregation.group(Alert.anomalyTypeField)
                .push(Alert.anomalyTypeField + "." + DataSourceAnomalyTypePair.anomalyTypeField).as("anomalyType")
                .push(Alert.anomalyTypeField + "." + DataSourceAnomalyTypePair.dataSourceField).as("dataSource");

        //Extract the {anomalyType: "anomalyType", dataSource:"dataSource") from under the "_id" to stand alone object
        AggregationOperation projectOperationStep = Aggregation.project(Fields.from(
                Fields.field("anomalyType", "_id.anomalyType"),
                Fields.field("dataSource", "_id.dataSource")
        ));


        //Execute the pipline
        Aggregation aggPipline = Aggregation.newAggregation(
                unwindOperationStep, groupOprationStep,projectOperationStep


        );

        AggregationResults<DataSourceAnomalyTypePair> groupResults
                = mongoTemplate.aggregate(aggPipline, Alert.COLLECTION_NAME, DataSourceAnomalyTypePair.class);
        return new HashSet<>(groupResults.getMappedResults());

    }


    @Override
    public Set<String> getDistinctUserIdsFromAlertsRelevantToUserScore(){

        Query query = getQueryForAlertsRelevantToUserScore(null);

        List<String> userNames = mongoTemplate.getCollection(Alert.COLLECTION_NAME).distinct(Alert.entityIdField,query.getQueryObject());
        return  new HashSet<>(userNames);
    }

    @Override
    public Set<Alert> getAlertsRelevantToUserScore(String username){

        Query query = getQueryForAlertsRelevantToUserScore(username);
        query.fields().exclude(Alert.evidencesField);

        List<Alert> userNames = mongoTemplate.find(query,Alert.class);

        return  new HashSet<>(userNames);
    }

	@Override
	public void updateUserContribution(String alertId, double newContribution, boolean newContributionFlag ){
		Query q = new Query(Criteria.where("_id").is(alertId));
		Update u = new Update().
				set(Alert.userScoreContributionFlagField, newContributionFlag).
				set(Alert.userScoreContributionField, newContribution);
		mongoTemplate.updateFirst(q,u,Alert.class);

	}

    private  Query getQueryForAlertsRelevantToUserScore(String userName) {
        Criteria criteria = new Criteria();
        criteria.where(Alert.feedbackField).ne(AlertFeedback.None).
                and(Alert.userScoreContributionFlagField).is(Boolean.TRUE);

        if (StringUtils.isNotBlank(userName)){
            criteria.and(Alert.entityNameField).is(userName);
        }
        Query query = new Query();
        query.addCriteria(criteria);
        return query;
    }

    /**
	 * Translate alert filter to list of Criteria
	 * @param severityFieldName
	 * @param statusFieldName
	 * @param feedbackFieldName
	 * @param startDateFieldName
	 * @param entityFieldName
	 * @param severityArrayFilter
	 * @param statusArrayFilter
	 * @param feedbackArrayFilter
	 * @param dateRangeFilter
	 * @param entityFilter
	 * @param users
	 * @return
	 */
	private List<Criteria> getCriteriaList(String severityFieldName, String statusFieldName, String feedbackFieldName, String startDateFieldName, String entityFieldName,
										   String severityArrayFilter, String statusArrayFilter, String feedbackArrayFilter,
										   DateRange dateRangeFilter, String entityFilter, Set<String> users, Set<DataSourceAnomalyTypePair> indicatorTypes) {

		List<Criteria> criteriaList = new ArrayList<>();
		//build severity filter
		if (severityArrayFilter != null) {
			String[] severityFilterVals = severityArrayFilter.split(",");
			List<String> severityList = new ArrayList<>();
			for (String val : severityFilterVals) {
				Severity severity = Severity.getByStringCaseInsensitive(val);
				if (severity != null) {
					severityList.add(severity.name());
				}
			}
			//If filter includes all severity entries, ignore the filter as it is the same as without filter
			if (severityList.size() != Severity.values().length) {
				Criteria severityCriteria = where(severityFieldName).in(severityList);
				criteriaList.add(severityCriteria);
			}
		}
		//build status filter
		if (statusArrayFilter != null) {
			String[] statusFilterVals = statusArrayFilter.split(",");
			List<String> statusList = new ArrayList<>();
			for (String val : statusFilterVals) {
				AlertStatus status = AlertStatus.getByStringCaseInsensitive(val);
				if (status != null) {
					statusList.add(status.name());
				}
			}
			//If filter includes all status entries, ignore the filter as it is the same as without filter
			if (statusList.size() != AlertStatus.values().length) {
				Criteria statusCriteria = where(statusFieldName).in(statusList);
				criteriaList.add(statusCriteria);
			}
		}
		//build feedback filter
		if (feedbackArrayFilter != null) {
			String[] feedbackFilterVals = feedbackArrayFilter.split(",");
			List<String> feedbackList = new ArrayList();
			for (String val : feedbackFilterVals) {
				AlertFeedback feedback = AlertFeedback.getByStringCaseInsensitive(val);
				if (feedback != null) {
					feedbackList.add(feedback.name());
				}
			}
			//If filter includes all feedback entries, ignore the filter as it is the same as without filter
			if (feedbackList.size() != AlertFeedback.values().length) {
				Criteria feedbackCriteria = where(feedbackFieldName).in(feedbackList);
				criteriaList.add(feedbackCriteria);
			}
		}
        //build dateRange filter
		if (dateRangeFilter != null) {
				try {

					Long startDate = TimestampUtils.convertToMilliSeconds(dateRangeFilter.getFromTime());
					Long endDate = TimestampUtils.convertToMilliSeconds(dateRangeFilter.getToTime());
					Criteria criteria= Criteria.where(startDateFieldName).gte(startDate).lte((endDate));
					criteriaList.add(criteria);
				} catch (NumberFormatException ex) {

					logger.error("wrong date value: " + dateRangeFilter.toString(), ex);
				}
		}
		//build entity filter
		if (entityFilter != null) {
			String[] entityNameFilterVals = entityFilter.split(",");
			Criteria entityCriteria = where(entityFieldName).in(entityNameFilterVals);
			criteriaList.add(entityCriteria);
		}
		//build tags filter
		if (users != null) {
			Criteria evidenceCriteria = where(Alert.entityIdField).in(users);
			criteriaList.add(evidenceCriteria);
		}

		// Build indicator filter
		if (indicatorTypes != null) {
            Criteria indicatorTypeCriteria = fetchAnomalyTypeCriteria(indicatorTypes);
            criteriaList.add(indicatorTypeCriteria);

        }

        return criteriaList;
	}

    /**
     * This method return criteria for filtering by anomaly type.
     * The anomaly type is composed object which can be filtered by datasource only or by data source and anomaly type
     *
     * The result of the Criteria can be one for the follwing next version:
     * 1) All indicatorTypes are data source only:
     *
     * Input: All SSH and all VPN indicators:
     *
     * Output:
     * "anomalyTypes.dataSource" : {
         "$in" : ["ssh", "vpn"]
       }

     * 2) All indicatorTypes composed of data source and and anomaly type
     *
     * Input: SSH time_anomaly  + VPN geo_hopping_anomaly:
     *
     * Output:
     *
     *  "anomalyTypes" : {
             "$in" : [{
                 "dataSource" : "ssh",
                 "anomalyType" : "time_anomaly"
             }, {
             "dataSource" : "vpn",
             "anomalyType" : "geo_hopping_anomaly"
             }
         ]
       }

     * 3) Some indicatorTypes composed of data source and anomaly type while other composed of data source only
     ** Input: SSH time_anomaly and ALL vpn & prnlong indicators:
     *
     *
     * Output:
     *  "$or" : [{
                 "anomalyTypes.dataSource" : {
                 "$in" : ["vpn","prnlog"]
                }
        }, {
            "anomalyTypes" : {
            "$in" : [{
                    "dataSource" : "ssh",
                    "anomalyType" : "CCCC"
                } ]
            }
        }
       ]
     *
     *
     * @param indicatorTypes
     * @return
     */
    private Criteria fetchAnomalyTypeCriteria(Set<DataSourceAnomalyTypePair> indicatorTypes) {
        BasicDBList dataSourceAndAnomalyConditions = new BasicDBList();
        List<String> dataSourceOnlyConditions = new ArrayList<>();
        indicatorTypes.forEach(anomalyType ->{
            if (StringUtils.isNotBlank(anomalyType.getAnomalyType())) {
                BasicDBObject anomalyTypeDbObject = anomalyType.wrapAsDbObject();
                if (anomalyTypeDbObject != null && anomalyTypeDbObject.size() > 0) {
                    dataSourceAndAnomalyConditions.add(anomalyTypeDbObject);
                }
            } else { //Filter by all indicators for data source
                dataSourceOnlyConditions.add(anomalyType.getDataSource());
            }
        });

        boolean dataSourceOnlyConditionsExits      = dataSourceOnlyConditions.size()       > 0;
        boolean dataSourceAndAnomalyConditionExits = dataSourceAndAnomalyConditions.size() > 0;

        Criteria dataSourceAndAnomalyCriteria = where(Alert.anomalyTypeField).in(dataSourceAndAnomalyConditions);
        Criteria dataSourceOnlyCriteria = where(Alert.anomalyTypeField+"."+DataSourceAnomalyTypePair.dataSourceField).in(dataSourceOnlyConditions);

        if (dataSourceOnlyConditionsExits && dataSourceAndAnomalyConditionExits){

            Criteria composedOrCriteria = new Criteria();
            composedOrCriteria.orOperator(dataSourceOnlyCriteria, dataSourceAndAnomalyCriteria);
            return  composedOrCriteria;
        } else if (dataSourceAndAnomalyConditionExits){
            return dataSourceAndAnomalyCriteria;
        } else if (dataSourceOnlyConditionsExits){
            return dataSourceOnlyCriteria;
        }

        return null;
    }


    private Criteria getCriteriaForGroupCount(String severityArrayFilter, String statusArrayFilter,
											  String feedbackArrayFilter, DateRange dateRangeFilter, String entityName,
											  Set<String> entitiesIds, Set<DataSourceAnomalyTypePair> indicatorTypes) {
		List<Criteria> criteriaList = getCriteriaList( Alert.severityField, Alert.statusField, Alert.feedbackField,
				Alert.startDateField, Alert.entityNameField, severityArrayFilter, statusArrayFilter,
				feedbackArrayFilter, dateRangeFilter, entityName, entitiesIds, indicatorTypes );

		Criteria criteria = null;

        if (criteriaList.size() == 1 )
		    criteria= criteriaList.get(0);
        else {
            criteria = new Criteria();
            criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        }


		return criteria;
	}

}