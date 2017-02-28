package fortscale.services.impl;

import fortscale.domain.core.*;
import fortscale.domain.core.alert.analystfeedback.AnalystRiskFeedback;
import fortscale.domain.core.dao.AlertsRepository;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.domain.dto.DailySeveiryConuntDTO;
import fortscale.domain.dto.DateRange;
import fortscale.domain.rest.UserRestFilter;
import fortscale.services.AlertsService;
import fortscale.services.UserScoreService;
import fortscale.services.UserService;

import fortscale.services.UserWithAlertService;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Services for managing the alerts
 *
 * Date: 6/23/2015.
 */
@Service("alertsService")
public class AlertsServiceImpl implements AlertsService {

	/**
	 * Mongo repository for alerts
	 */
	@Autowired
	private AlertsRepository alertsRepository;

	/**
	 * Mongo repository for users
	 */
	@Autowired
	private UserService userService;

    @Autowired
    private UserScoreService userScoreService;

	private Set<String> feedbackNoRejectedSet;

	{
		feedbackNoRejectedSet = new HashSet<>();

		Arrays.stream(AlertFeedback.values()).forEach(alertFeedback -> {
			if (!alertFeedback.equals(AlertFeedback.Rejected)) {
				feedbackNoRejectedSet.add(alertFeedback.toString());
			}
		});
	}

	@Autowired
	private UserWithAlertService userWithAlertService;

	@Override
	public void saveAlertInRepository(Alert alert) {
		saveAlert(alert);
	}

	/**
	 * Saves Alerts in Mongo
	 * @param alert the alert to save
	 * @return the saved alert
	 */
	private Alert saveAlert(Alert alert){

		alert = userScoreService.updateAlertContirubtion(alert);
		alert = alertsRepository.save(alert);
		userScoreService.recalculateUserScore(alert.getEntityId());

		userWithAlertService.recalculateNumberOfUserAlertsByUserId(alert.getEntityId());
		return alert;
	}



	@Override
	public Alerts findAll(PageRequest pageRequest) {
		return alertsRepository.findAll(pageRequest);
	}

	@Override
	public Long count(PageRequest pageRequest) {
		return alertsRepository.count(pageRequest);
	}

	private Set<String> getUserIds(String entityTags, String entityId) {
		Set<String> ids = null;
		if (entityTags != null) {
			String[] tagsFilter = entityTags.split(",");
			ids = userService.findIdsByTags(tagsFilter, entityId);
		}

		return ids;
	}


	@Override
	public Alerts findAlertsByFilters(PageRequest pageRequest, String severityArray, String statusArrayFilter,
			String feedbackArrayFilter, DateRange dateRangeFilter, String entityName, String entityTags, String entityId,
									  Set<DataSourceAnomalyTypePair> indicatorTypes) {
		Set<String> ids = getUserIds(entityTags, entityId);
		if (ids == null && entityId != null) {
			ids = new HashSet<>();
			ids.addAll(Arrays.asList(entityId.split(",")));
		}

		return alertsRepository.findAlertsByFilters(pageRequest, severityArray, statusArrayFilter, feedbackArrayFilter, dateRangeFilter,
				entityName, ids, indicatorTypes);
	}


	@Override
	public Long countAlertsByFilters(PageRequest pageRequest, String severityArray, String statusArrayFilter,
			String feedbackArrayFilter, DateRange dateRangeFilter, String entityName, String entityTags, String entityId,
									 Set<DataSourceAnomalyTypePair> indicatorTypes) {
		Set<String> ids = getUserIds(entityTags, entityId);
		if (ids == null && entityId != null) {
			ids = new HashSet<>();
			ids.addAll(Arrays.asList(entityId.split(",")));
		}

		return alertsRepository.countAlertsByFilters(pageRequest, severityArray, statusArrayFilter, feedbackArrayFilter, dateRangeFilter, entityName, ids, indicatorTypes);
	}

	@Override
	public void add(Alert alert) {
		alertsRepository.add(alert);
		userScoreService.recalculateUserScore(alert.getEntityName());
		userWithAlertService.recalculateNumberOfUserAlertsByUserName(alert.getEntityName());
	}

	@Override
	public void delete(String id) {
		alertsRepository.delete(id);
	}

	@Override
	public Alert getAlertById(String id) {
		return alertsRepository.getAlertById(id);
	}

	@Override
	public Map<String, Integer> groupCount(String fieldName, String severityArrayFilter, String statusArrayFilter,
										   String feedbackArrayFilter, DateRange dateRangeFilter, String entityName,
										   String entityTags, String entityId, Set<DataSourceAnomalyTypePair> indicatorTypes){

		Set<String> ids = getUserIds(entityTags, entityId);
		if (ids == null && entityId != null) {
			ids = new HashSet<>();
			ids.addAll(Arrays.asList(entityId.split(",")));
		}

		return alertsRepository.groupCount(fieldName, severityArrayFilter, statusArrayFilter, feedbackArrayFilter, dateRangeFilter, entityName, ids, indicatorTypes);
	}

	@Override
	public Map<String, Integer> getAlertsTypesCounted(Boolean ignoreRejected){

		String feedback = StringUtils.arrayToCommaDelimitedString(getFeedbackListForFilter(ignoreRejected).toArray());
		return alertsRepository.groupCount(Alert.nameField,null, null,feedback ,
				null, null, null, null);
	}

	@Override
	public Map<Set<String>, Set<String>> getAlertsTypesByUser(Boolean ignoreRejected) {
		final String HOURLY_SUFFIX = "_hourly";
		final String DAILY_SUFFIX = "_daily";
		Map<Set<String>, Set<String>> results = new HashMap<>();
		String feedback = StringUtils.arrayToCommaDelimitedString(getFeedbackListForFilter(ignoreRejected).toArray());
		 //the userAndAlertType map contains each pair of alert_name + alert_field once.
		Set<Pair<String,String>> userAndAlertName = alertsRepository.groupCountBy2Fields(Alert.nameField,
				Alert.entityNameField, null, null, feedback, null, null, null, null).keySet();
		//build the results
		for (Pair<String,String> alertNameAndUserName: userAndAlertName) {
			String agnosticAlertName = alertNameAndUserName.getLeft().replace(HOURLY_SUFFIX, "").replace(DAILY_SUFFIX,
					"");
			Set<String> alertTypesToAdd = null;
			for (Set<String> alertTypes: results.keySet()) {
				if (alertTypesToAdd != null) {
					break;
				}
				for (String alertType: alertTypes) {
					if (alertType.startsWith(agnosticAlertName)) {
						alertTypesToAdd = alertTypes;
						break;
					}
				}
			}
			if (alertTypesToAdd == null) {
				alertTypesToAdd = new HashSet<>();
			}
			alertTypesToAdd.add(alertNameAndUserName.getLeft());
			boolean added = false;
			for (Map.Entry<Set<String>, Set<String>> entry: results.entrySet()) {
				if (entry.getKey() == alertTypesToAdd) {
					entry.getValue().add(alertNameAndUserName.getRight());
					added = true;
					break;
				}
			}
			if (!added) {
				results.put(alertTypesToAdd, new HashSet<>(Arrays.asList(alertNameAndUserName.getRight())));
			}
		}
		return results;
	}

	@Override
	public List<Alert> getAlertSummary(List<String> severities, long endDate) {
		return alertsRepository.getAlertSummary(severities, endDate);
	}

	@Override
    public List<Alert> getAlertsByTimeRange(DateRange dateRange, List<String> severities) {
		return getAlertsByTimeRange(dateRange, severities, false);
    }

	private List<Alert> getAlertsByTimeRange(DateRange dateRange, List<String> severities, boolean excludeEvidences){
		return alertsRepository.getAlertsByTimeRange(dateRange, severities, excludeEvidences);
	}

	@Override
	public void removeRedundantAlertsForUser(String username, String alertId) {
		alertsRepository.removeRedundantAlertsForUser(username, alertId);
	}
    @Override
    public Set<DataSourceAnomalyTypePair> getDistinctAnomalyType(){

        return alertsRepository.getDataSourceAnomalyTypePairs();
    }


    @Override
    public List<Alert> getAlertsByUsername(String userName){
		return alertsRepository.findByEntityName(userName);
    }

    public List<DailySeveiryConuntDTO> getAlertsCountByDayAndSeverity(DateRange alertStartRange){

        //Build empty ordered map from day to severities count
        NavigableMap<Long, DailySeveiryConuntDTO> sortedAlertsCountByDays = new TreeMap<>();
        List<Long> daysInRange = alertStartRange.getDaysInRange(alertStartRange);

        for (Long day : daysInRange){
            sortedAlertsCountByDays.put(day, new DailySeveiryConuntDTO(day));
        }

        //Set counts into map
        List<Alert> alertsInRange = getAlertsByTimeRange(alertStartRange,null, true);
        if (alertsInRange.size() > 0){
            alertsInRange.forEach(alert -> {
                DailySeveiryConuntDTO dailySeveiryConuntDTO = sortedAlertsCountByDays.floorEntry(alert.getStartDate()).getValue();
                dailySeveiryConuntDTO.incrementCountBySeverity(alert.getSeverity());
            });


        }

        return new ArrayList<>(sortedAlertsCountByDays.values());
    }

    @Override
    public Set<String> getDistinctUserIdsFromAlertsRelevantToUserScore(){
        return  alertsRepository.getDistinctUserIdsFromAlertsRelevantToUserScore();
    }

    @Override
    public Set<Alert> getAlertsRelevantToUserScore(String userId){
        return  alertsRepository.getAlertsRelevantToUserScore(userId);
    }

	@Override public List<Alert> getOpenAlertsByUsername(String userName) {
		List<Alert> alerts = alertsRepository.getAlertsForUserByFeedback(userName, feedbackNoRejectedSet);

		return alerts;
	}

	@Override public Set<String> getDistinctAlertNames(Boolean ignoreRejected) {
		Set<String> alertNames;
		alertNames = alertsRepository.getDistinctAlertNames(getFeedbackListForFilter(ignoreRejected));

		return alertNames.stream().sorted().collect(Collectors.toSet());
	}

	private Set<String> getFeedbackListForFilter(Boolean ignoreRejected){
		if (BooleanUtils.isFalse(ignoreRejected)) {
			return null;
		} else {
			return feedbackNoRejectedSet;
		}
	}

	@Override
	public Set<String> getDistinctUserIdByUserFilter(UserRestFilter userRestFilter) {
		return alertsRepository.getDistinctUserIdByUserRestFilter(userRestFilter);
	}

	@Override
	public AnalystRiskFeedback updateAlertStatus(Alert alert, AlertStatus alertStatus, AlertFeedback alertFeedback, String analystUserName) {
		boolean alertUpdated = false;
		AnalystRiskFeedback analystRiskFeedback = null;

		if (alert != null) {

			// update the alerts' status
			if (alertStatus != null) {
				alert.setStatus(alertStatus);
				alertUpdated = true;
			}

			// update the alerts' feedback
			if (alertFeedback != null) {
				alert.setFeedback(alertFeedback);
				alertUpdated = true;
			}

			if (alertUpdated) {
				// Check what was the alerts' user score contribution before the status update
				double userScoreContributionBeforeUpdate = alert.getUserScoreContribution();

				// Save the alert to repository
				saveAlertInRepository(alert);

				// Get the users' score and severity after the status update
				User user = userService.getUserById(alert.getEntityId());
				Severity userSeverity = userScoreService.getUserSeverityForScore(user.getScore());

				// Create analystRiskFeedback, add it to the alert and save
				analystRiskFeedback = new AnalystRiskFeedback(analystUserName, alertFeedback,
						user.getScore(), userScoreContributionBeforeUpdate, alert.getUserScoreContribution(),
						userSeverity, System.currentTimeMillis());
				alert.addAnalystFeedback(analystRiskFeedback);

				saveAlertInRepository(alert);
			}
		}
		return analystRiskFeedback;
	}
}
