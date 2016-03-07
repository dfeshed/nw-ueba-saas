package fortscale.domain.core.dao;


import fortscale.domain.core.*;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.utils.time.TimestampUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;




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
			String feedbackArrayFilter, String dateRangeFilter, String entityName, Set<String> entitiesIds,
									  List<String> indicatorIds) {

		//build the query
		Query query = buildQuery(pageRequest, Alert.severityField, Alert.statusField, Alert.feedbackField,
				Alert.startDateField, Alert.entityNameField, severityArrayFilter, statusArrayFilter,
				feedbackArrayFilter, dateRangeFilter, entityName, entitiesIds, pageRequest, indicatorIds);
		List<Alert> alertsList = mongoTemplate.find(query, Alert.class);
		Alerts alerts = new Alerts();
		alerts.setAlerts(alertsList);
		return alerts;
	}

	@Override
	public Long countAlertsByFilters(PageRequest pageRequest, String severityArrayFilter, String statusArrayFilter,
									 String feedbackArrayFilter, String dateRangeFilter, String entityName, Set<String> entitiesIds, List<String> indicatorIds) {

		//build the query
		Query query = buildQuery(pageRequest, Alert.severityField, Alert.statusField, Alert.feedbackField,
				Alert.startDateField, Alert.entityNameField, severityArrayFilter, statusArrayFilter,
				feedbackArrayFilter, dateRangeFilter, entityName, entitiesIds, pageRequest, indicatorIds);
		return mongoTemplate.count(query, Alert.class);
	}

	public Map<String, Integer> groupCount(String fieldName, String severityArrayFilter, String statusArrayFilter,
										   String feedbackArrayFilter, String dateRangeFilter, String entityName,
										   Set<String> entitiesIds, List<String> indicatorIds){
		Criteria criteria = getCriteriaForGroupCount(severityArrayFilter, statusArrayFilter, feedbackArrayFilter, dateRangeFilter, entityName, entitiesIds, indicatorIds);
		return mongoDbRepositoryUtil.groupCount(fieldName,criteria, "alerts");


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
	public List<Alert> getAlertsByTimeRange(long startDate, long endDate, List<String> severities){
		startDate =  TimestampUtils.convertToMilliSeconds(startDate);
		endDate =  TimestampUtils.convertToMilliSeconds(endDate);
		Query query = new Query();

		query.addCriteria(where(Alert.endDateField).lte(endDate))
				.addCriteria(where(Alert.startDateField).gte(startDate))
				.with(new Sort(Sort.Direction.DESC, Alert.scoreField))
				.with(new Sort(Sort.Direction.DESC, Alert.endDateField));

		if (severities.size() != 0) {
			query.addCriteria(where(Alert.severityField).in(severities));
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
							 String dateRangeFilter, String entityFilter, Set<String> users, Pageable pageable, List<String> indicatorIds) {

		Query query = new Query().with(pageRequest.getSort());

		//Get list of criteria
		List<Criteria> criteriaList = getCriteriaList(severityFieldName, statusFieldName, feedbackFieldName,
				startDateFieldName, entityFieldName, severityArrayFilter, statusArrayFilter, feedbackArrayFilter,
				dateRangeFilter, entityFilter, users, indicatorIds);

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
	private List<Criteria> getCriteriaList(String severityFieldName, String statusFieldName, String feedbackFieldName, String startDateFieldName, String entityFieldName, String severityArrayFilter, String statusArrayFilter, String feedbackArrayFilter, String dateRangeFilter, String entityFilter, Set<String> users, List<String> indicatorIds) {

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
			String[] dateRangeFilterVals = dateRangeFilter.split(",");
			if (dateRangeFilterVals.length == 2) {
				try {
					Long startDate = Long.parseLong(dateRangeFilterVals[0]);
					startDate = TimestampUtils.convertToMilliSeconds(startDate);
					Long endDate = Long.parseLong(dateRangeFilterVals[1]);
					endDate = TimestampUtils.convertToMilliSeconds(endDate);
					Criteria criteria= Criteria.where(startDateFieldName).gte(startDate).lte((endDate));
					criteriaList.add(criteria);
				} catch (NumberFormatException ex) {

					logger.error("wrong date value: " + dateRangeFilterVals.toString(), ex);
				}
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
		if (indicatorIds != null) {
			criteriaList.add(where(Alert.evidencesField + ".$id").in(indicatorIds.toArray()));
		}

		return criteriaList;
	}



	private Criteria getCriteriaForGroupCount(String severityArrayFilter, String statusArrayFilter,
											  String feedbackArrayFilter, String dateRangeFilter, String entityName,
											  Set<String> entitiesIds, List<String> indicatorIds) {
		List<Criteria> criteriaList = getCriteriaList( Alert.severityField, Alert.statusField, Alert.feedbackField,
				Alert.startDateField, Alert.entityNameField, severityArrayFilter, statusArrayFilter,
				feedbackArrayFilter, dateRangeFilter, entityName, entitiesIds, indicatorIds );

		Criteria criteria = null;

		criteria= criteriaList.get(0);

		if (criteriaList.size() > 1) {
			//Concate all other criterias
			criteriaList.remove(0);
			criteria.andOperator(criteriaList.toArray(new Criteria[0]));
		}

		return criteria;
	}

}