package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.*;
import fortscale.domain.core.alert.Alert;
import fortscale.domain.core.alert.AlertFeedback;
import fortscale.domain.core.alert.AlertStatus;
import fortscale.domain.core.alert.AlertTimeframe;
import fortscale.services.*;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.evidence.applicable.AlertFilterApplicableEvidencesService;
import fortscale.streaming.alert.subscribers.evidence.applicable.AlertTypesHisotryCache;
import fortscale.streaming.alert.subscribers.evidence.decider.AlertDeciderServiceImpl;
import fortscale.streaming.service.alert.EvidencesForAlertResolverService;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
public class AlertCreationSubscriber extends AbstractSubscriber {

	private static Logger logger = Logger.getLogger(AlertCreationSubscriber.class);

	/**
	 * Alerts service (for Mongo export)
	 */
	@Autowired
    protected AlertsService alertsService;

	@Autowired
    private UserService userService;

    @Autowired
    private UserScoreService userScoreService;

	/**
	 * Computer service (for resolving id)
	 */
	@Autowired protected ComputerService computerService;

	@Autowired
	private AlertFilterApplicableEvidencesService evidencesApplicableToAlertService;

	@Autowired
	private AlertDeciderServiceImpl decider;

	@Autowired
	private AlertTypesHisotryCache alertTypesHisotryCache;

	@Autowired
	@Qualifier("defaultTagToSeverityMapping")
	private TagsToSeverityMapping defaultTagToSeverityMapping;

	@Autowired
	@Qualifier("priviligedTagToSeverityMapping")
	private TagsToSeverityMapping privilegedTagToSeverityMapping;

    @Autowired
    EvidencesForAlertResolverService  evidencesForAlertResolverService;

	@Value("#{'${fortscale.tags.priviliged:admin,executive,service}'.split(',')}")
	private Set<String> privilegedTags;

	/**
	 * Alert forwarding service (for forwarding new alerts)
	 */
	@Autowired private ForwardingService forwardingService;

	/**
	 * Listener method called when Esper has detected a pattern match.
	 * Creates an alert and saves it in mongo. this includes the references to its evidences, which are already in mongo.
	 * Map array holds one map for each user for a certain hour/day
	 */
	public void update(Map[] insertStream, Map[] removeStream) {
		if (insertStream != null) {

			logger.info("Alert creation subscriber was called with {} window contexts", insertStream.length);

			for (Map eventStreamByUserAndTimeframe : insertStream)
				try {
					EntityType entityType = (EntityType) eventStreamByUserAndTimeframe.get(Evidence.entityTypeField);
					String entityName = (String) eventStreamByUserAndTimeframe.get(Evidence.entityNameField);
					String entityId;
					switch (entityType) {
						case User: {
							entityId = userService.getUserId(entityName);
							break;
						}
						case Machine: {
							entityId = computerService.getComputerId(entityName);
							break;
						}
						default: {
							logger.warn("Cannot handle entity of type {}", entityType);

							continue;
						}
					}

					AlertTimeframe timeframe = (AlertTimeframe) eventStreamByUserAndTimeframe.get("timeframe");

					Long startDate = (Long) eventStreamByUserAndTimeframe.get("startDate");
					Long endDate = (Long) eventStreamByUserAndTimeframe.get("endDate");

					Map[] rawEventArr = (Map[]) eventStreamByUserAndTimeframe.get("eventList");

					logger.info("Going to create Alert for user {}. Start time = {} ({}). End time = {} ({}). # of received events in window = {}", entityName, startDate, TimeUtils.getUTCFormattedTime(startDate), endDate, TimeUtils.getUTCFormattedTime(endDate), rawEventArr.length);

					List<EnrichedFortscaleEvent> eventList = convertToFortscaleEventList(rawEventArr);

					//create the list of evidences to apply to the decider
					List<EnrichedFortscaleEvent> evidencesEligibleForDecider = evidencesApplicableToAlertService.createIndicatorListApplicableForDecider(
							eventList, startDate, endDate, timeframe);

					if (CollectionUtils.isEmpty(evidencesEligibleForDecider)) {
						logger.warn("Failed to find eligible events for alert creation");
						continue;
					}

					String title = decider.decideName(evidencesEligibleForDecider,timeframe);
					Integer roundScore = decider.decideScore(evidencesEligibleForDecider, timeframe);

					Set<String> userTags = userService.getUserTags(entityName);
					Severity severity = getSeverity(entityName, roundScore, userTags);

					if (title != null && severity != null) {
						logger.info("Alert title = {}. Alert Severity = {}", title, severity);

						List<Evidence> attachedNotifications = evidencesForAlertResolverService.handleNotifications(eventList.stream().
                                filter(event -> (event.getEvidenceType() == EvidenceType.Notification)).collect(Collectors.toList()));

						logger.info("Attaching {} notification indicators to Alert: {}", attachedNotifications.size(), attachedNotifications);

						List<Evidence> attachedEntityEventIndicators = evidencesForAlertResolverService.handleEntityEvents(eventList.stream().
                                filter(event -> (event.getEvidenceType() == EvidenceType.Smart)).collect(Collectors.toList()));
						logger.info("Attaching {} F/P indicators to Alert: {}", attachedEntityEventIndicators.size(), attachedEntityEventIndicators);

						List<Evidence> attachedTags = evidencesForAlertResolverService.handleTags(userTags,entityType, entityName, startDate, endDate);
						logger.info("Attaching {} tag indicators to Alert: {}", attachedTags.size(), attachedTags);

						List<Evidence> finalIndicatorsListForAlert = new ArrayList<>();
						finalIndicatorsListForAlert.addAll(attachedNotifications);
						finalIndicatorsListForAlert.addAll(attachedEntityEventIndicators);

						if (finalIndicatorsListForAlert.isEmpty()) {
							logger.info("Alert is not created, since there aren't any notifications nor indicators. Event value = {}", eventStreamByUserAndTimeframe);
						} else {
							// Add tag indicators
							finalIndicatorsListForAlert.addAll(attachedTags);
							double alertUserScoreContribution = userScoreService.getUserScoreContributionForAlertSeverity(severity, AlertFeedback.None, startDate);
							Alert alert = new Alert(title, startDate, endDate, entityType, entityName, finalIndicatorsListForAlert, finalIndicatorsListForAlert.size(),
									roundScore, severity, AlertStatus.Open, AlertFeedback.None, entityId, timeframe, alertUserScoreContribution, alertUserScoreContribution > 0);
							logger.info("Saving alert in DB: {}", alert);
							alertsService.saveAlertInRepository(alert);
							logger.info("Alert was saved successfully");
							alertTypesHisotryCache.updateCache(alert);
							forwardingService.forwardNewAlert(alert);
						}
					}
				} catch (Exception e) {
					logger.error("Exception while handling stream event. Event value = {}. Exception:", eventStreamByUserAndTimeframe, e);
				}
		}
	}

	private Severity getSeverity(String entityName, Integer roundScore, Set<String> userTags) {
		Severity severity;

		if (Collections.disjoint(userTags, privilegedTags)){
			//Regular user. No privileged tags
			severity = defaultTagToSeverityMapping.getSeverityByScore(roundScore);
		} else {
			//Privileged
			severity = privilegedTagToSeverityMapping.getSeverityByScore(roundScore);
		}
		return severity;
	}

	private List<EnrichedFortscaleEvent> convertToFortscaleEventList(Map[] rawEventArr) {
		List<EnrichedFortscaleEvent>  fortscaleEventList = new ArrayList<>();

		for (Map rawEvent : rawEventArr) {
			EnrichedFortscaleEvent fortscaleEvent = new EnrichedFortscaleEvent();
			fortscaleEvent.fromMap(rawEvent);
			fortscaleEventList.add(fortscaleEvent);
		}
		return  fortscaleEventList;
	}

	public void setEvidencesApplicableToAlertService(AlertFilterApplicableEvidencesService evidencesApplicableToAlertService) {
		this.evidencesApplicableToAlertService = evidencesApplicableToAlertService;
	}

	public void setDecider(AlertDeciderServiceImpl decider) {
		this.decider = decider;
	}

}