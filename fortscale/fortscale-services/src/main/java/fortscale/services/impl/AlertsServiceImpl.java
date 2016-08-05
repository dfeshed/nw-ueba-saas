package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.AlertFeedback;
import fortscale.domain.core.DataSourceAnomalyTypePair;
import fortscale.domain.core.Severity;
import fortscale.domain.core.dao.AlertsRepository;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.domain.dto.DailySeveiryConuntDTO;
import fortscale.domain.dto.DateRange;
import fortscale.domain.dto.SeveritiesCountDTO;
import fortscale.services.AlertsService;
import fortscale.services.UserScoreService;
import fortscale.services.UserService;
import fortscale.utils.time.TimestampUtils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

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
		userScoreService.recalculateUserScore(alert.getEntityName());
		userScoreService.recalculateNumberOfUserAlerts(alert.getEntityName());
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

		return alertsRepository.countAlertsByFilters(pageRequest, severityArray, statusArrayFilter, feedbackArrayFilter, dateRangeFilter,
				entityName, ids, indicatorTypes);
	}

	@Override
	public void add(Alert alert) {
		alertsRepository.add(alert);
		userScoreService.recalculateUserScore(alert.getEntityName());
		userScoreService.recalculateNumberOfUserAlerts(alert.getEntityName());
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

		return alertsRepository.groupCount(fieldName,severityArrayFilter, statusArrayFilter, feedbackArrayFilter,
						dateRangeFilter, entityName, ids, indicatorTypes);
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
    public Set<String> getDistinctUserNamesFromAlertsRelevantToUserScore(){
        return  alertsRepository.getDistinctUserNamesFromAlertsRelevantToUserScore();
    }

    @Override
    public Set<Alert> getAlertsRelevantToUserScore(String userName){
        return  alertsRepository.getAlertsRelevantToUserScore(userName);
    }

	@Override public Set<Alert> getOpenAlertsByUsername(String userName) {
		Set<AlertFeedback> feedbackSet = new HashSet<>();
		feedbackSet.add(AlertFeedback.Approved);
		feedbackSet.add(AlertFeedback.None);

		return alertsRepository.getAlertsForUserByFeedback(userName, feedbackSet);
	}
}
