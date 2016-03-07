package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.*;
import fortscale.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
public class AlertCreationSubscriber extends AbstractSubscriber {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(AlertCreationSubscriber.class);


	/**
	 * Alerts service (for Mongo export)
	 */
	@Autowired protected AlertsService alertsService;

	@Autowired private UserService userService;

	@Autowired private TagService tagService;

	@Autowired private UserSupportingInformationService userSupportingInformationService;

	/**
	 * Computer service (for resolving id)
	 */
	@Autowired protected ComputerService computerService;

	/**
	 * Evidence service (for Mongo export)
	 */
	@Autowired protected EvidencesService evidencesService;

	/**
	 * Listener method called when Esper has detected a pattern match.
	 * Creates an alert and saves it in mongo. this includes the references to its evidences, which are already in mongo.
	 */
	public void update(Map[] insertStream, Map[] removeStream) {
		if (insertStream != null) {
			for (Map insertStreamOutput : insertStream) {
				try {
					List<Evidence> evidences = new ArrayList<>();
					String[] idList = (String[]) insertStreamOutput.get("idList");
					for (String id : idList) {
						//create new Evidence with the evidence id. it creates reference to the evidence object in mongo.
						Evidence evidence = new Evidence(id);
						evidences.add(evidence);
					}
					String title = (String) insertStreamOutput.get("title");
					Long startDate = (Long) insertStreamOutput.get("startDate");
					Long endDate = (Long) insertStreamOutput.get("endDate");
					EntityType entityType = (EntityType) insertStreamOutput.get(Evidence.entityTypeField);
					String entityName = (String) insertStreamOutput.get(Evidence.entityNameField);
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
						entityId = "";
					}
					//TODO - handle the rest of the entity types
					}
					Double score = (Double) insertStreamOutput.get("score");
					Integer roundScore = score.intValue();
					Severity severity = alertsService.getScoreToSeverity().floorEntry(roundScore).getValue();
					//if this is a statement containing tags
					if (insertStreamOutput.containsKey("tags") && insertStreamOutput.get("tags") != null) {
						String tagStr = (String) insertStreamOutput.get("tag");
						Tag tag = tagService.getTag(tagStr);
						if (tag != null && tag.getCreatesIndicator()) {
							User user = userService.findByUsername(entityName);
							Evidence tagEvidence = evidencesService.createTagEvidence(entityType,
									Evidence.entityTypeFieldNameField, entityName, startDate, endDate, tagStr, user, userService);
							evidences.add(tagEvidence);
						}
					}
					Alert alert = new Alert(title, startDate, endDate, entityType, entityName, evidences, evidences.size(),
							roundScore,	severity, AlertStatus.Open, AlertFeedback.None, "", entityId);
					//Save alert to mongoDB
					alertsService.saveAlertInRepository(alert);
				} catch (RuntimeException ex) {
					logger.error(ex.getMessage(), ex);
					ex.printStackTrace();
				}
			}
		}
	}
}