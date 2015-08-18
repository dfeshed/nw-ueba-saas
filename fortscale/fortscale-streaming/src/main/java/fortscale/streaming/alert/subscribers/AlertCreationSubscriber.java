package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.AlertsService;
import fortscale.services.impl.EvidencesService;
import fortscale.streaming.service.SpringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;
import java.util.*;

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
    @Autowired
    protected AlertsService alertsService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Evidence service (for Mongo export)
     */
    @Autowired
    protected EvidencesService evidencesService;

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
                    Double score = (Double) insertStreamOutput.get("score");
                    Integer roundScore = score.intValue();
                    Severity severity = alertsService.getScoreToSeverity().floorEntry(roundScore).getValue();
                    //if this is a statement containing tags
                    if (insertStreamOutput.containsKey("tags") && insertStreamOutput.get("tags") != null) {
                        createTagEvidence(insertStreamOutput, evidences, startDate, endDate, entityType, entityName);
                    }
                    Alert alert = new Alert(title, startDate, endDate, entityType, entityName, evidences, roundScore, severity, AlertStatus.Open, "");

                    //Save alert to mongoDB
                    alertsService.saveAlertInRepository(alert);
                } catch (RuntimeException ex) {
                    logger.error(ex.getMessage(), ex);
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Helper method to create tag evidence.
     * Creates a tag evidence based on the alert to create, saves it to Mongo and adds a reference to it for the alert
     */
    private void createTagEvidence(Map insertStreamOutput, List<Evidence> evidences, Long startDate, Long endDate,
                                   EntityType entityType, String entityName) {
        List<String> tags = (List<String>)insertStreamOutput.get("tags");
        String tag = (String)insertStreamOutput.get("tag");
        if (tags.contains(tag)) {
            String entityTypeFieldName = (String)insertStreamOutput.get(Evidence.entityTypeFieldNameField);
            List<String> dataEntitiesIds = new ArrayList();
            dataEntitiesIds.add("active_directory");
            EvidencesService evidencesService = SpringService.getInstance().resolve(EvidencesService.class);

            Evidence evidence = evidencesService.createTransientEvidence(entityType, entityTypeFieldName,
                    entityName, EvidenceType.Tag, new Date(startDate), new Date(endDate),
                    dataEntitiesIds, 50d, tag, "tag");

            //EvidenceSupportingInformation is part of Evidence. not like supportionInformationData which comes directly from rest
            EvidenceSupportingInformation evidenceSupportingInformation = createTagEvidenceSupportingInformationData(evidence);
            evidence.setSupportingInformation(evidenceSupportingInformation);

            // Save evidence to MongoDB
            try {
                evidencesService.saveEvidenceInRepository(evidence);
            } catch (DuplicateKeyException e) {
                //TODO - what to do here?
                logger.warn("Got duplication for evidence {}. Going to drop it.", evidence.toString());
            }
            evidences.add(new Evidence(evidence.getId()));
        }
    }

    /**
     * create new tag evidence supporting information.
     * it includes mostly the user's data from active directory.
     * @return
     */
    public EvidenceSupportingInformation createTagEvidenceSupportingInformationData(Evidence evidence){
        EvidenceSupportingInformation evidenceSupportingInformation = new EvidenceSupportingInformation();

        User user= userRepository.findByUsername(evidence.getEntityName());
        if(user == null || user.getUsername() == null){
            logger.warn("No user {} exist! ");
            return null;
        }

        //create userDetails object and return it
        UserSupprotingInformation userSupprotingInformation = new UserSupprotingInformation(user);
        evidenceSupportingInformation.setUserDetails(userSupprotingInformation);

        return evidenceSupportingInformation;

    }




}