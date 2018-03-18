package fortscale.web.demoservices.services;

import fortscale.domain.core.*;
import fortscale.domain.core.alert.analystfeedback.AnalystRiskFeedback;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.domain.dto.DailySeveiryConuntDTO;
import fortscale.domain.dto.DateRange;
import fortscale.services.AlertsService;
import fortscale.services.UserService;
import fortscale.services.UserWithAlertService;
import fortscale.web.demoservices.DemoBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Services for managing the alerts
 *
 * Date: 6/23/2015.
 */
//@Service("alertsService")
//@Profile("mock")
public class MockDemoAlertsServiceImpl implements AlertsService {


	/**
	 * Mongo repository for users
	 */

	private UserService userService;

//    @Autowired
//    private UserScoreService userScoreService;
//

	private DemoBuilder demoBuilder;

	private Set<String> feedbackNoRejectedSet;

	{
		feedbackNoRejectedSet = new HashSet<>();

		Arrays.stream(AlertFeedback.values()).forEach(alertFeedback -> {
			if (!alertFeedback.equals(AlertFeedback.Rejected)) {
				feedbackNoRejectedSet.add(alertFeedback.toString());
			}
		});
	}

	public MockDemoAlertsServiceImpl(UserService userService, DemoBuilder demoBuilder){
		this.userService = userService;
		this.demoBuilder = demoBuilder;
	}

	@Autowired
	private UserWithAlertService userWithAlertService;

	@Override
	public Alerts findAll(PageRequest pageRequest, boolean expand) {

//		alerts.setAlerts(MockScenarioGenerator.generateMocksAlertsList());
		List<Alert> relevantAlerts = demoBuilder.getAlerts();
		if (relevantAlerts.size()>=pageRequest.getPageSize()*pageRequest.getPageNumber()){
			relevantAlerts= relevantAlerts.subList((pageRequest.getPageSize()-1)*pageRequest.getPageNumber(),pageRequest.getPageSize()*pageRequest.getPageNumber());
		}
		Alerts alerts = new Alerts(relevantAlerts,relevantAlerts.size());

		return alerts;
	}

	@Override
	public Long count(PageRequest pageRequest) {
		return new Long(demoBuilder.getAlerts().size());
	}

	private Set<String> getUserIds(String entityTags, String entityId) {
		Set<String> ids=null;



		if (entityTags != null) {
			String[] tagsFilter = entityTags.split(",");
//			ids = userService.findIdsByTags(tagsFilter, entityId);
			ids=demoBuilder.getUsers().
					stream().
					filter(user -> user.getTags().contains("admin")).
					map(User::getId).
					collect(Collectors.toSet());
		}

		Set<String> entityIdsSet = null;
		if (entityId!=null) {
			if (entityId.startsWith(",")) {
				entityId.replaceFirst(",", "");
			}
			entityIdsSet = new HashSet<>(Arrays.asList(entityId.split(",")));

		}

		//Join the to sets
		if (entityIdsSet!=null){
			if (ids==null){
				ids=new HashSet<>();
			}
			ids.addAll(entityIdsSet);
		}

		return ids;

	}


	@Override
	public Alerts findAlertsByFilters(PageRequest pageRequest, String severityArray, String statusArrayFilter,
									  String feedbackArrayFilter, DateRange dateRangeFilter, String entityName, String entityTags, String entityId,
									  Set<String> indicatorTypes,boolean expand, boolean loadComments) {
		Set<String> ids = getUserIds(entityTags, entityId);
		if (ids == null && entityId != null) {
			ids = new HashSet<>();
			ids.addAll(Arrays.asList(entityId.split(",")));
		}

		final Set<String> finalIds = ids;
		List<Alert> alertsList = getAlertsByFilter(severityArray, statusArrayFilter, feedbackArrayFilter, dateRangeFilter, entityName, indicatorTypes);

		if (CollectionUtils.isNotEmpty(ids)) {
			alertsList = alertsList.stream().filter(alert -> finalIds.contains(alert.getEntityId())).collect(Collectors.toList());
		}

		return new Alerts(MockServiceUtils.getPage(alertsList,pageRequest,Alert.class));


	}






	private List<Alert> getAlertsByFilter(String severityArray, String statusArrayFilter, String feedbackArrayFilter, DateRange dateRangeFilter, String entityName, Set<String> indicatorTypes) {
		List<Alert> originalAlertsList = demoBuilder.getAlerts();

		List<Alert> alertsList = filterBySetOfEnumAsString(originalAlertsList,severityArray, Severity.class, (alert)->alert.getSeverity());
		alertsList = filterBySetOfEnumAsString(alertsList,statusArrayFilter, AlertStatus.class, (alert)->alert.getStatus());
		alertsList = filterBySetOfEnumAsString(alertsList,feedbackArrayFilter, AlertFeedback.class, (alert)->alert.getFeedback());

		if (dateRangeFilter!=null) {
			alertsList = alertsList.stream().filter(alert -> dateRangeFilter.checkBetween(alert.getStartDate())).collect(Collectors.toList());
		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(entityName)) {
			alertsList = alertsList.stream().filter(alert -> entityName.equals(alert.getEntityName())).collect(Collectors.toList());
		}


		if (CollectionUtils.isNotEmpty(indicatorTypes)) {
			alertsList = alertsList.stream().filter(alert -> {
				if(alert.getDataSourceAnomalyTypePair()==null){
					return false;
				}
				return CollectionUtils.intersection(indicatorTypes, alert.getDataSourceAnomalyTypePair()).size()>0;

			}).collect(Collectors.toList());
		}
		return alertsList;
	}


	private <E extends Enum<E>> List<Alert> filterBySetOfEnumAsString(Collection<Alert> originalAlerts, String commonsSeperatedData, Class<E> clazz,
																	  Function<Alert,E> getter){

		if (originalAlerts == null || originalAlerts.isEmpty()){
			return  Collections.emptyList();
		}
		if (StringUtils.isEmpty(commonsSeperatedData)){
			return  new ArrayList<>(originalAlerts);
		}

		Set<E> valuesToFilterBy = Arrays.asList(commonsSeperatedData.split(",")).
				stream().
				map(value -> {
					//Get the enum by string, igrnore case
					for (E enumValue:clazz.getEnumConstants()){
						if (enumValue.name().toLowerCase().equals(value.toLowerCase())){
							return enumValue;
						}
					}
					return  null;
				}).

				collect(Collectors.toSet());
		valuesToFilterBy.removeIf(value-> value == null);
		if (valuesToFilterBy.size()==0) {
			return Collections.emptyList();
		}

		return originalAlerts.stream().filter(alert -> valuesToFilterBy.contains(getter.apply(alert))).collect(Collectors.toList());


	}



	@Override
	public Long countAlertsByFilters(String severityArray, String statusArrayFilter,
									 String feedbackArrayFilter, DateRange dateRangeFilter, String entityName, String entityTags, String entityId,
									 Set<String> indicatorTypes) {
//		Set<String> ids = getUserIds(entityTags, entityId);
//		if (ids == null && entityId != null) {
//			ids = new HashSet<>();
//			ids.addAll(Arrays.asList(entityId.split(",")));
//		}

		//return alertsRepository.countAlertsByFilters(pageRequest, severityArray, statusArrayFilter, feedbackArrayFilter, dateRangeFilter, entityName, ids, indicatorTypes);
		List<Alert> alerts=findAlertsByFilters(null, severityArray, statusArrayFilter,feedbackArrayFilter, dateRangeFilter, entityName, entityTags, entityId,indicatorTypes,true,false).getAlerts();

		return new Long(alerts.size());

	}



	@Override
	public Alert getAlertById(String id) {
		return demoBuilder.getAlerts().stream().filter(alert -> alert.getId().equals(id)).findAny().get();
	}

	@Override
	public Map<String, Integer> groupCount(String fieldName, String severityArrayFilter, String statusArrayFilter,
										   String feedbackArrayFilter, DateRange dateRangeFilter, String entityName,
										   String entityTags, String entityId, Set<String> indicatorTypes){

		Set<String> ids = getUserIds(entityTags, entityId);
		if (ids == null && entityId != null) {
			ids = new HashSet<>();
			ids.addAll(Arrays.asList(entityId.split(",")));
		}

		//return alertsRepository.groupCount(fieldName, severityArrayFilter, statusArrayFilter, feedbackArrayFilter, dateRangeFilter, entityName, ids, indicatorTypes);
		List<Alert> alertsList = getAlertsByFilter(severityArrayFilter, statusArrayFilter, feedbackArrayFilter, dateRangeFilter, entityName, indicatorTypes);
		Map<String, Integer> counts = new HashMap<>();
		alertsList.forEach(alert -> {

			String value="";
			if ("severity".equals(fieldName)){
				value=alert.getSeverity().name();
			} else if("feedback".equals(fieldName)){
				value = alert.getFeedback().name();
			}
			Integer count = counts.get(value);
			if (count == null) {
				counts.put(value, 1);
			}
			else {
				counts.put(value, count + 1);
			}
		});

		return  counts;

	}

	@Override
	public Map<String, Integer> getAlertsTypesCounted(Boolean ignoreRejected){

		Map <String, Long> byName = this.demoBuilder.getAlerts().stream().
				filter(alert -> {return ignoreRejected?!AlertFeedback.Rejected.equals(alert.getFeedback()) :true;}) //If shouldn't ignore
				.collect(Collectors.groupingBy(alert -> alert.getName(), Collectors.counting()));

		Map <String, Integer> byNameAsInt = new HashMap<>();
		for (Map.Entry<String,Long> entry:byName.entrySet()){
			byNameAsInt.put(entry.getKey(), entry.getValue().intValue());
		}


		return byNameAsInt;
	}

	@Override
	public Map<Set<String>, Set<String>> getAlertsTypesByUser(Boolean ignoreRejected) {
		final String HOURLY_SUFFIX = "_hourly";
		final String DAILY_SUFFIX = "_daily";
		Map<Set<String>, Set<String>> results = new HashMap<>();
		String feedback = StringUtils.arrayToCommaDelimitedString(getFeedbackListForFilter(ignoreRejected).toArray());
		//the userAndAlertType map contains each pair of alert_name + alert_field once.
		Set<Pair<String,String>> userAndAlertName = new HashSet<>();
		this.demoBuilder.getAlerts().forEach(x->{
			userAndAlertName.add(new ImmutablePair<>(x.getName(),x.getEntityName()));
		});
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
	public Map<String, Integer> getAlertsTypes(){
		Map<String, Integer> alertTypes = new HashMap<>();
		for (Map.Entry<Set<String>, Set<String>> entry:getAlertsTypesByUser(true).entrySet()){
			Integer size = new Integer(entry.getValue().size());
			alertTypes.put(entry.getKey().stream().findFirst().get(),size);

		}
		return  alertTypes;


	}

//	@Override
//	public List<Alert> getAlertSummary(List<String> severities, long endDate) {
//		return Arrays.asList(new AlertMockBuilder(44).createInstance());
//	}

	@Override
	public List<Alert> getAlertsByTimeRange(DateRange dateRange, List<String> severities) {
		return getAlertsByTimeRange(dateRange, severities, false);
	}

	private List<Alert> getAlertsByTimeRange(DateRange dateRange, List<String> severities, boolean excludeEvidences){
		return demoBuilder.getAlerts().stream().filter(alert -> {
			return dateRange.checkBetween(alert.getStartDate()) &&
					(severities==null || severities.contains(alert.getSeverity().name()));
		}).
				collect(Collectors.toList());
	}


	@Override
	public Map<String,Integer> getDistinctAnomalyType(){

		Map<String,Integer> anomalyTypes=new HashMap<>();
		for (Alert a: demoBuilder.getAlerts()){
			for (Evidence indicator: a.getEvidences()){
				anomalyTypes.put(indicator.getAnomalyType(),1);
			}
		}

		return anomalyTypes;
	}


	@Override
	public Alerts getAlertsByUsername(String userName){
		List<Alert> alerts =  demoBuilder.getAlerts().stream().filter(alert -> alert.getEntityName().equals(userName)).collect(Collectors.toList());
		Alerts wrapper = new Alerts(alerts,alerts.size());
		return  wrapper;
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

//		List<DailySeveiryConuntDTO> dailySeverityCount = new ArrayList<>();
//		LocalDateTime now = LocalDateTime.now();
//
//		for (int i=7;i>0;i--){
//			LocalDateTime dayJava8 = now.minusDays(i).toLocalDate().atStartOfDay();
//			Date day = Date.from(dayJava8.toInstant(ZoneOffset.UTC));
//			DailySeveiryConuntDTO dayCount = new DailySeveiryConuntDTO(day.getTime());
//			List<SeveritiesCountDTO> severitiesCountDTOS= Arrays.asList(
//					new SeveritiesCountDTO(Severity.Critical,5),
//					new SeveritiesCountDTO(Severity.High,5),
//					new SeveritiesCountDTO(Severity.Medium,5),
//					new SeveritiesCountDTO(Severity.Low,5)
//
//			);
//
//
//			dayCount.setSeverities(severitiesCountDTOS);
//			dailySeverityCount.add(dayCount);
//
//		}

//		return dailySeverityCount;
	}

//    @Override
//    public Set<String> getDistinctUserIdsFromAlertsRelevantToUserScore(){
//        return SetUtils.EMPTY_SET;
//    }

	@Override
	public Alerts getAlertsRelevantToUserScore(String userId){
		return new Alerts(Collections.emptyList(),0);
	}

	@Override public List<Alert> getOpenAlertsByUsername(String userName) {
		return demoBuilder.getAlerts().stream().
				filter(alert -> alert.getEntityName().equals(userName) &&
						alert.getStatus().equals(AlertStatus.Open)).
				collect(Collectors.toList());


	}

//	@Override public Set<String> getDistinctAlertNames(Boolean ignoreRejected) {
//		Set<String> alertNames;
////		alertNames = alertsRepository.getDistinctAlertNames(getFeedbackListForFilter(ignoreRejected));
////
////		return alertNames.stream().sorted().collect(Collectors.toSet());
//		return demoBuilder.getAlerts().stream().
//									   filter(alert -> {
//											return !ignoreRejected || !AlertFeedback.Rejected.equals(alert.getFeedback());
//										}).
//										map(Alert::getName).collect(Collectors.toSet());
//
//
//	}

	private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
	{
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	private Set<String> getFeedbackListForFilter(Boolean ignoreRejected){
		if (BooleanUtils.isFalse(ignoreRejected)) {
			return null;
		} else {
			return feedbackNoRejectedSet;
		}
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
//				Severity userSeverity = userScoreService.getUserSeverityForScore(user.getScore());

				// Create analystRiskFeedback, add it to the alert and save
				analystRiskFeedback = new AnalystRiskFeedback(analystUserName, alertFeedback,
						userScoreContributionBeforeUpdate, user.getScore(),
						user.getScoreSeverity(), System.currentTimeMillis(),alert.getId());
				alert.addAnalystFeedback(analystRiskFeedback);
				recalculateUserScore(user);
				demoBuilder.populateUserSeverity();
				saveAlertInRepository(alert);
			}
		}
		return analystRiskFeedback;

	}

	private void recalculateUserScore(User user){
		List<Alert> userAlerts = demoBuilder.getAlertsByUserName(user.getUsername());

		double[] score=new double[1];

		if (CollectionUtils.isNotEmpty(userAlerts)){
			userAlerts.forEach(alert->{
				if(!AlertFeedback.Rejected.equals(alert.getFeedback())) {
					score[0]+=alert.getUserScoreContribution();
				}
			});
		}
		user.setScore(score[0]);


	}
	public void saveAlertInRepository(Alert a){
		this.demoBuilder.updateOrAddAlert(a);
	}
}