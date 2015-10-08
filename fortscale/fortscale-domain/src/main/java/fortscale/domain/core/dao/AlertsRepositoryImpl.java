package fortscale.domain.core.dao;

import com.mongodb.BasicDBObject;
import fortscale.domain.core.*;
import fortscale.domain.core.dao.rest.Alerts;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Field;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;



//imports as static
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;


import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;


/**
 * Created by rans on 21/06/15.
 */
public class AlertsRepositoryImpl implements AlertsRepositoryCustom {

	@Autowired private MongoTemplate mongoTemplate;

	private static Logger logger = LoggerFactory.getLogger(AlertsRepositoryImpl.class);
	private static  String TOTAL_FIELD_NAME = "total";

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
			String feedbackArrayFilter, String dateRangeFilter, String entityName, Set<String> entitiesIds) {

		//build the query
		Query query = buildQuery(pageRequest, Alert.severityField, Alert.statusField, Alert.feedbackField,
				Alert.startDateField, Alert.entityNameField, severityArrayFilter, statusArrayFilter,
				feedbackArrayFilter, dateRangeFilter, entityName, entitiesIds, pageRequest);
		List<Alert> alertsList = mongoTemplate.find(query, Alert.class);
		Alerts alerts = new Alerts();
		alerts.setAlerts(alertsList);
		return alerts;
	}

	@Override
	public Long countAlertsByFilters(PageRequest pageRequest, String severityArrayFilter, String statusArrayFilter,
			String feedbackArrayFilter, String dateRangeFilter, String entityName, Set<String> entitiesIds) {

		//build the query
		Query query = buildQuery(pageRequest, Alert.severityField, Alert.statusField, Alert.feedbackField,
				Alert.startDateField, Alert.entityNameField, severityArrayFilter, statusArrayFilter,
				feedbackArrayFilter, dateRangeFilter, entityName, entitiesIds, pageRequest);
		return mongoTemplate.count(query, Alert.class);
	}

	public Map<String, Integer> groupCount(String fieldName, long afterDate){

		//Create aggregation on fieldName, for all alerts which started after "afterDate").
		Aggregation agg = Aggregation.newAggregation(
				match(Criteria.where("startDate").gte(afterDate)),
				group(fieldName).count().as(TOTAL_FIELD_NAME),
				project(TOTAL_FIELD_NAME).and(fieldName).previousOperation()
		);


		AggregationResults<BasicDBObject> groupResults
				= mongoTemplate.aggregate(agg, "alerts" , BasicDBObject.class);

		//Convert the aggregation result into a map of "key = fieldValue, value= field count"
		Map<String, Integer> results = new HashMap<>();
		for (BasicDBObject item:  groupResults.getMappedResults()) {
			String fieldValue = item.get(fieldName).toString();
			String countAsString = item.get(TOTAL_FIELD_NAME).toString();

			int count;
			if (StringUtils.isBlank(countAsString)){
				count = 0;
			} else {
				count = Integer.parseInt(countAsString);
			}
			results.put(fieldValue,count);
		}

		//Return the map
		return results;
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
							 String dateRangeFilter, String entityFilter, Set<String> users, Pageable pageable) {
		Query query = new Query().with(pageRequest.getSort());
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
				query.addCriteria(severityCriteria);
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
				query.addCriteria(statusCriteria);
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
				query.addCriteria(feedbackCriteria);
			}
		}
		//build dateRange filter
		if (dateRangeFilter != null) {
			String[] dateRangeFilterVals = dateRangeFilter.split(",");
			if (dateRangeFilterVals.length == 2) {
				try {
					Long startDate = Long.parseLong(dateRangeFilterVals[0]);
					Long endDate = Long.parseLong(dateRangeFilterVals[1]);
					Criteria startDateCriteria = where(startDateFieldName).gte(startDate);
					Criteria endDateCriteria = where(startDateFieldName).lte(endDate);
					query.addCriteria(new Criteria().andOperator(startDateCriteria, endDateCriteria));
				} catch (NumberFormatException ex) {

					logger.error("wrong date value: " + dateRangeFilterVals.toString(), ex);
				}
			}
		}
		//build entity filter
		if (entityFilter != null) {
			String[] entityNameFilterVals = entityFilter.split(",");
			Criteria entityCriteria = where(entityFieldName).in(entityNameFilterVals);
			query.addCriteria(entityCriteria);
		}
		//build tags filter
		if (users != null) {
			Criteria evidenceCriteria = where(Alert.entityIdField).in(users);
			query.addCriteria(evidenceCriteria);
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

}