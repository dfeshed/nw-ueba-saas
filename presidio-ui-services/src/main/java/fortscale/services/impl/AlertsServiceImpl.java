package fortscale.services.impl;

import fortscale.domain.core.*;
import fortscale.domain.core.Alert;
import fortscale.domain.core.User;
import fortscale.domain.core.alert.analystfeedback.AnalystFeedback;
import fortscale.domain.core.alert.analystfeedback.AnalystRiskFeedback;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.domain.dto.DailySeveiryConuntDTO;
import fortscale.domain.dto.DateRange;
import fortscale.services.AlertCommentsService;
import fortscale.services.AlertsService;
import fortscale.services.UserService;
import fortscale.services.exception.UserNotFoundExeption;
import fortscale.services.presidio.core.converters.AggregationConverterHelper;
import fortscale.services.presidio.core.converters.AlertConverterHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import presidio.output.client.api.AlertsApi;
import presidio.output.client.client.ApiClient;
import presidio.output.client.client.ApiException;
import presidio.output.client.model.*;

import java.util.*;

/**
 * Services for managing the alerts
 * <p>
 * Date: 6/23/2015.
 */
@Service("alertsService")
public class AlertsServiceImpl extends RemoteClientServiceAbs<AlertsApi> implements AlertsService {


    /**
     * Mongo repository for users
     */
    @Autowired
    private UserService userService;

    @Autowired
    private AlertConverterHelper alertConverterHelper;

    @Autowired
    private AlertCommentsService alertCommentsService;


    private AggregationConverterHelper aggregationConverterHelper = new AggregationConverterHelper();
    private static final String SEVERITY_COLUMN_NAME = "severity";
    private static final String FEEDBACK_COLUMN_NAME = "Feedback";

    private Set<String> feedbackNoRejectedSet;

    {
        feedbackNoRejectedSet = new HashSet<>();

        Arrays.stream(AlertFeedback.values()).forEach(alertFeedback -> {
            if (!alertFeedback.equals(AlertFeedback.Rejected)) {
                feedbackNoRejectedSet.add(alertFeedback.toString());
            }
        });
    }

//	@Autowired
//	private UserWithAlertService userWithAlertService;

    @Override
    public Alerts findAll(PageRequest pageRequest,boolean expand) {
        return findAlertsByFilters(pageRequest, null, null, null, null, null, null, null, null,expand,false);
    }


    @Override
    public Long count(PageRequest pageRequest) {
        try {
            AlertQuery query = alertConverterHelper.convertUiFilterToQueryDto(pageRequest,null,null,null,null,null,null,null,null,false);
            AlertsWrapper alertsBean = super.getConterollerApi().getAlerts(query);

            return alertsBean.getTotal().longValue();

        } catch (ApiException e) {
            logger.error("Failed to fetch alerts from presidio output");
            return 0L;
        }

    }

    private Set<String> filterUserIdsForTag(String entityTags, String entityId) {
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
                                      Set<String> indicatorTypes, boolean expand, boolean loadComments) {

        entityId = extendEntityIdWithTaggedUsers(entityTags, entityId);

        AlertQuery query = alertConverterHelper.convertUiFilterToQueryDto(pageRequest,severityArray,statusArrayFilter,feedbackArrayFilter,dateRangeFilter,
                entityName,entityTags,entityId,indicatorTypes,expand);

        Alerts alerts = null;
        try {
            AlertsWrapper alertsBean = super.getConterollerApi().getAlerts(query);

            alerts = new Alerts(alertConverterHelper.convertResponseToUiDto(alertsBean), alertsBean.getTotal().longValue());


        } catch (ApiException e) {
            logger.error("Failed to fetch alerts from presidio output");
        }

        if (alerts==null){
            alerts = new Alerts(Collections.emptyList(),0);
        }

        if (loadComments && alerts.getAlerts().size()>0){
            updateCommentsOnAlert(alerts);

        }
        return alerts;



    }

    private void updateCommentsOnAlert(Alerts alerts) {
        Set<String> alertIds = new HashSet<>();
        alerts.getAlerts().forEach(alert->{
            alertIds.add(alert.getId());
        });
        Map<String, List<AnalystFeedback>> commentsPerAlert = this.alertCommentsService.getCommentByAlertIds(alertIds);
        if (MapUtils.isEmpty(commentsPerAlert)){
            return;
        }

        alerts.getAlerts().forEach(alert->{
            List<AnalystFeedback> comments = commentsPerAlert.get(alert.getId());
            if (CollectionUtils.isNotEmpty(comments)){
                alert.setAnalystFeedback(comments);
            }
        });
    }

    /**
     * This method return list of entityIDs to look for.
     * If entityIds is null the list will return all the ids of the users with thr requested tag(s)
     * If entityIds is not null, the list will return subset of the original entity ids list- only those who have the requsted tag(s)
     *
     * @param entityTags
     * @param entityIds
     * @return
     */
    private String extendEntityIdWithTaggedUsers(String entityTags, String entityIds) {
        Set<String> idsForTag = filterUserIdsForTag(entityTags, entityIds);
        if (CollectionUtils.isEmpty(idsForTag)) {
            //No users with relevant tags, no need to merge user ids, only return the original entityIds
            return entityIds;
        }
        //There are users with requrest tag

        if (entityIds != null) {
            //Add original entity ids list to the final list
            idsForTag.addAll(Arrays.asList(entityIds.split(",")));

        }


        return StringUtils.join(idsForTag, ",");

    }


    @Override
    public Long countAlertsByFilters(String severityArray, String statusArrayFilter,
                                     String feedbackArrayFilter, DateRange dateRangeFilter, String entityName, String entityTags, String entityId,
                                     Set<String> indicatorTypes) {


        return findAlertsByFilters(null, severityArray, statusArrayFilter, feedbackArrayFilter, dateRangeFilter, entityName, entityTags, entityId, indicatorTypes,false,false).getTotalCount();
    }


    @Override
    public Alert getAlertById(String id) {

        try {
            presidio.output.client.model.Alert singleAlert = super.getConterollerApi().getAlert(id,true);
            return alertConverterHelper.convertResponseToUiDto(singleAlert);
        } catch (ApiException e) {
            logger.error("some error took place while asking for alert by id from presidio output");
        }
        return null;
    }

    @Override
    public Map<String, Integer> groupCount(String fieldName, String severityArrayFilter, String statusArrayFilter,
                                           String feedbackArrayFilter, DateRange dateRangeFilter, String entityName,
                                           String entityTags, String entityId, Set<String> indicatorTypes) {


        AlertQuery query=alertConverterHelper.convertUiFilterToQueryDto(null,severityArrayFilter,statusArrayFilter,feedbackArrayFilter,dateRangeFilter,entityName,
                entityTags,entityId,indicatorTypes,false);

        AlertQuery.AggregateByEnum aggregateByEnum=null;

        List<AlertQuery.AggregateByEnum> aggregateBy = new ArrayList<>();
        if (fieldName.equalsIgnoreCase(SEVERITY_COLUMN_NAME)){
            aggregateByEnum = AlertQuery.AggregateByEnum.SEVERITY;
        } else if (fieldName.equalsIgnoreCase(FEEDBACK_COLUMN_NAME)){
            aggregateByEnum = AlertQuery.AggregateByEnum.FEEDBACK;
        }

        if (aggregateByEnum == null){
            return MapUtils.EMPTY_MAP;
        }

        aggregateBy.add(aggregateByEnum);
        query.aggregateBy(aggregateBy);
        try {
            AlertsWrapper alertsWrapper = super.getConterollerApi().getAlerts(query);
            Map<String,Map<String,Long>> results = alertsWrapper.getAggregationData();

            return  aggregationConverterHelper.convertAggregation(results,aggregateByEnum.name());
        } catch (ApiException e) {
            return MapUtils.EMPTY_MAP;
        }


    }

    @Override
    public Map<String, Integer> getAlertsTypesCounted(Boolean ignoreRejected) {

        String feedback = StringUtils.join(getFeedbackListForFilter(ignoreRejected).toArray(), ",");
//		return alertsRepository.groupCount(Alert.nameField,null, null,feedback ,
//				null, null, null, null);
        //TODO: need further implementation from Karkens
        return MapUtils.EMPTY_MAP;
    }

    @Override
    public Map<Set<String>, Set<String>> getAlertsTypesByUser(Boolean ignoreRejected) {
        //TODO: need further implementation from Karkens
//		final String HOURLY_SUFFIX = "_hourly";
//		final String DAILY_SUFFIX = "_daily";
//		Map<Set<String>, Set<String>> results = new HashMap<>();
//		String feedback = StringUtils.arrayToCommaDelimitedString(getFeedbackListForFilter(ignoreRejected).toArray());
//		 //the userAndAlertType map contains each pair of alert_name + alert_field once.
//		Set<Pair<String,String>> userAndAlertName = alertsRepository.groupCountBy2Fields(Alert.nameField,
//				Alert.entityNameField, null, null, feedback, null, null, null, null).keySet();
//		//build the results
//		for (Pair<String,String> alertNameAndUserName: userAndAlertName) {
//			String agnosticAlertName = alertNameAndUserName.getLeft().replace(HOURLY_SUFFIX, "").replace(DAILY_SUFFIX,
//					"");
//			Set<String> alertTypesToAdd = null;
//			for (Set<String> alertTypes: results.keySet()) {
//				if (alertTypesToAdd != null) {
//					break;
//				}
//				for (String alertType: alertTypes) {
//					if (alertType.startsWith(agnosticAlertName)) {
//						alertTypesToAdd = alertTypes;
//						break;
//					}
//				}
//			}
//			if (alertTypesToAdd == null) {
//				alertTypesToAdd = new HashSet<>();
//			}
//			alertTypesToAdd.add(alertNameAndUserName.getLeft());
//			boolean added = false;
//			for (Map.Entry<Set<String>, Set<String>> entry: results.entrySet()) {
//				if (entry.getKey() == alertTypesToAdd) {
//					entry.getValue().add(alertNameAndUserName.getRight());
//					added = true;
//					break;
//				}
//			}
//			if (!added) {
//				results.put(alertTypesToAdd, new HashSet<>(Arrays.asList(alertNameAndUserName.getRight())));
//			}
//		}
//		return results;

        return MapUtils.EMPTY_MAP;
    }

    public Map<String,Integer> getAlertsTypes(){
        AlertQuery alertQuery = new AlertQuery();
        alertQuery.addAggregateByItem(AlertQuery.AggregateByEnum.CLASSIFICATIONS);
        try {
            Map<String,Map<String,Long>> aggregationData = super.getConterollerApi().getAlerts(alertQuery).getAggregationData();
            Map<String,Integer> classificiations = aggregationConverterHelper.convertAggregation(aggregationData,AlertQuery.AggregateByEnum.CLASSIFICATIONS.name());
            return classificiations;

        } catch (ApiException e) {
            logger.error("Cannot get alert aggregation by classifications");
            return null;
        }

    }

//	@Override
//	public List<Alert> getAlertSummary(List<String> severities, long endDate) {
//		return Arrays.asList(new AlertMockBuilder(44).createInstance());
//	}

    @Override
    public List<Alert> getAlertsByTimeRange(DateRange dateRange, List<String> severities) {
        return getAlertsByTimeRange(dateRange, severities, false,false);
    }

    private List<Alert> getAlertsByTimeRange(DateRange dateRange, List<String> severities, boolean excludeEvidences, boolean loadComments) {
        return findAlertsByFilters(null, null,
                StringUtils.join(severities, ","), null,
                dateRange, null, null, null, null,!excludeEvidences,loadComments).getAlerts();
    }


    @Override
    public Map<String,Integer> getDistinctAnomalyType() {
        Set<String> anomalyTypes=new HashSet<>();


        AlertQuery alertQuery = alertConverterHelper.convertUiFilterToQueryDto(null,null,null,null,null,
                null,null,null,null,false );
        alertQuery.addAggregateByItem(AlertQuery.AggregateByEnum.INDICATOR_NAMES);

        try {
            Map<String, Map<String, Long>> aggregationData = super.getConterollerApi().getAlerts(alertQuery).getAggregationData();
            Map<String, Integer> aggregation = aggregationConverterHelper.convertAggregation(aggregationData, AlertQuery.AggregateByEnum.INDICATOR_NAMES.name());

            return  aggregation;
        } catch (ApiException e) {
            logger.error("Cannot get indicators per alerts aggregation");
            return Collections.emptyMap();
        }


    }


    @Override
    public Alerts getAlertsByUsername(String userName) {
        PageRequest p = new PageRequest(-1, 10000);
        return findAlertsByFilters(p, null, null, null, null, userName, null, null, null,true,false);
    }

    public List<DailySeveiryConuntDTO> getAlertsCountByDayAndSeverity(DateRange alertStartRange) {
//TODO: need further implementation from Karkens
        //Build empty ordered map from day to severities count
////        NavigableMap<Long, DailySeveiryConuntDTO> sortedAlertsCountByDays = new TreeMap<>();
////        List<Long> daysInRange = alertStartRange.getDaysInRange(alertStartRange);
////
////        for (Long day : daysInRange){
////            sortedAlertsCountByDays.put(day, new DailySeveiryConuntDTO(day));
////        }
////
////        //Set counts into map
////        List<Alert> alertsInRange = getAlertsByTimeRange(alertStartRange,null, true);
////        if (alertsInRange.size() > 0){
////            alertsInRange.forEach(alert -> {
////                DailySeveiryConuntDTO dailySeveiryConuntDTO = sortedAlertsCountByDays.floorEntry(alert.getStartDate()).getValue();
////                dailySeveiryConuntDTO.incrementCountBySeverity(alert.getSeverity());
////            });
////
////
////        }
//
//        return new ArrayList<>(sortedAlertsCountByDays.values());

//        List<DailySeveiryConuntDTO> dailySeverityCount = new ArrayList<>();
//        LocalDateTime now = LocalDateTime.now();
//
//        for (int i = 7; i > 0; i--) {
//            LocalDateTime dayJava8 = now.minusDays(i).toLocalDate().atStartOfDay();
//            Date day = Date.from(dayJava8.toInstant(ZoneOffset.UTC));
//            DailySeveiryConuntDTO dayCount = new DailySeveiryConuntDTO(day.getTime());
//            List<SeveritiesCountDTO> severitiesCountDTOS = Arrays.asList(
//                    new SeveritiesCountDTO(Severity.Critical, 5),
//                    new SeveritiesCountDTO(Severity.High, 5),
//                    new SeveritiesCountDTO(Severity.Medium, 5),
//                    new SeveritiesCountDTO(Severity.Low, 5)
//
//            );
//
//
//            dayCount.setSeverities(severitiesCountDTOS);
//            dailySeverityCount.add(dayCount);
//
//        }
//
//        return dailySeverityCount;
        List<DailySeveiryConuntDTO> dailySeverityCount = new ArrayList<>();

        Map<Long,DailySeveiryConuntDTO> orderedByDaySeveritiyCount = new LinkedHashMap();

        AlertQuery alertQuery = alertConverterHelper.convertUiFilterToQueryDto(null,null,null,null,alertStartRange,
                null,null,null,null,false );
        alertQuery.addAggregateByItem(AlertQuery.AggregateByEnum.SEVERITY_DAILY);
        try {
            Map<String,Map<String,Long>> aggregationData = super.getConterollerApi().getAlerts(alertQuery).getAggregationData();
            Map<String,Integer> aggregation = aggregationConverterHelper.convertAggregation(aggregationData,AlertQuery.AggregateByEnum.SEVERITY_DAILY.name());

            for (Map.Entry<String,Integer> entry:aggregation.entrySet()){
                String fullKey = entry.getKey();
                String dateAsString = fullKey.split(":")[0];
                String severity = fullKey.split(":")[1];

                long dateMiliSeconds =Long.parseLong(dateAsString);

                Integer count = entry.getValue();
                //Create new or get existing.
                DailySeveiryConuntDTO dailyCount = new DailySeveiryConuntDTO(dateMiliSeconds);
                orderedByDaySeveritiyCount.putIfAbsent(dateMiliSeconds,dailyCount);
                dailyCount=orderedByDaySeveritiyCount.get(dateMiliSeconds);


//                List<SeveritiesCountDTO> dailyValues = dailyCount.getSeverities();
//                dailyValues.add(new SeveritiesCountDTO(Severity.getByStringCaseInsensitive(severity),count));
                dailyCount.updateSeverity(Severity.getByStringCaseInsensitive(severity),count);

            }

            orderedByDaySeveritiyCount.entrySet().forEach(x->{
                dailySeverityCount.add(x.getValue());
            });

            return dailySeverityCount;

        } catch (ApiException e) {
            logger.error("Cannot get alert aggregation by classifications");
            return null;
        }
    }


    @Override
    public Alerts getAlertsRelevantToUserScore(String userId) {
        return new Alerts(Collections.emptyList(), 0);
    }

    @Override
    public List<Alert> getOpenAlertsByUsername(String userName) {
        return findAlertsByFilters(null, null, AlertStatus.Open.name(), AlertFeedback.None.name(),
                null, null, null, null, null,false,false).getAlerts();

    }

    private Set<String> getFeedbackListForFilter(Boolean ignoreRejected) {
        if (BooleanUtils.isFalse(ignoreRejected)) {
            return null;
        } else {
            return feedbackNoRejectedSet;
        }
    }


    @Override
    public AnalystRiskFeedback updateAlertStatus(Alert alert, AlertStatus alertStatus, AlertFeedback alertFeedback, String analystUserName) throws UserNotFoundExeption {

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
//				double userScoreContributionBeforeUpdate = alert.getUserScoreContribution();

                // Save the alert to repository
//				saveAlertInRepository(alert);

                // Get the users' score and severity after the status update

                // Create analystRiskFeedback, add it to the alert and save
                User oldUser = getUser(alert);
                //Update Presidio-Output
                List<presidio.output.client.model.AlertQuery.FeedbackEnum> feedbackEnum = alertConverterHelper.getFeedbackConverter().convertUiFilterToQueryDto(alertFeedback.name());
                UpdateFeedbackRequest.FeedbackEnum feedbackForUpdate = UpdateFeedbackRequest.FeedbackEnum.fromValue(feedbackEnum.get(0).getValue());

                try {
                    UpdateFeedbackRequest updateFeedbackRequest = new UpdateFeedbackRequest();
                    updateFeedbackRequest.setAlertIds(Arrays.asList(alert.getId()));
                    updateFeedbackRequest.setFeedback(feedbackForUpdate);
                    super.getConterollerApi().updateAlertsFeedback(updateFeedbackRequest);
                } catch (ApiException e) {
                    logger.error("Failed updating user stauts");
                    throw new RuntimeException("Can't update user status");
                }

                //Get the new user score


                User newUser = getUser(alert);

                analystRiskFeedback = new AnalystRiskFeedback(analystUserName, alertFeedback,
                        oldUser.getScore(),newUser.getScore(),
                        newUser.getScoreSeverity(), System.currentTimeMillis(),alert.getId());

                alertCommentsService.addComment(analystRiskFeedback);

            }
        }
        return analystRiskFeedback;

    }

    private User getUser(Alert alert) throws UserNotFoundExeption {
        double userScore;
        List<User> users = userService.findByIds(Arrays.asList(alert.getEntityId()));
        if (users.size()!=1){
            //Cannot find user
            logger.error("Cannot find user with id {}",alert.getEntityId());
            throw new UserNotFoundExeption(alert.getEntityId());
        }
        return users.get(0);
    }

    @Override
    public void saveAlertInRepository(Alert alert) {
        //TODO: need further implementation from Karkens
    }


    @Override
    protected AlertsApi getControllerInstance(ApiClient delegatoeApiClient) {
        return new AlertsApi(delegatoeApiClient);
    }
}