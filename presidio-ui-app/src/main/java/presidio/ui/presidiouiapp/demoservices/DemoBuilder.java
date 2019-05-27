package presidio.ui.presidiouiapp.demoservices;

import fortscale.common.dataentity.DataSourceType;
import fortscale.domain.core.*;
import presidio.ui.presidiouiapp.rest.entities.SupportingInformationEntry;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

import java.util.stream.Collectors;

/**
 * Created by shays on 23/07/2017.
 */
//@Service("demoBuilder")
//@Profile("mock")
public class DemoBuilder {

    private Map<String, List<SupportingInformationEntry>> suppotingInformationForIndicatorId=new HashMap<>();
    private List<Entity> entities;
    private List<Alert> alerts;
    private List<Evidence> indicators;

    public DemoBuilder(){
        try {

            this.entities = new DemoEntityFactory().getEntities();
            this.alerts = new DemoAlertFactory().getAlerts();
            DemoIndicatorsFactory demoIndicatorsFactory = new DemoIndicatorsFactory();
            this.indicators = demoIndicatorsFactory.getEvidences();
            suppotingInformationForIndicatorId = demoIndicatorsFactory.suppotingInformationForIndicatorId;

            populateEntityNamesOnAlert();
            populateEvidenceToAlerts();
            populateEntitySeverity();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DemoBuilder(List<Entity> entities, List<Alert> alerts, List<Evidence> indicators){

            this.entities = entities;
            this.alerts = alerts;
            this.indicators = indicators;


    }

    public void populateEntitySeverity(){
        Entity entityWithMaxScore = this.entities.stream().max((user1, user2)->Double.compare(user1.getScore(),user2.getScore())).get();

        double maxScore =  entityWithMaxScore.getScore();

        this.entities.forEach(entity->{
            double percent = 100*entity.getScore()/maxScore;
            if (percent<70){
                entity.setScoreSeverity(Severity.Low);
            } else if (percent<80){
                entity.setScoreSeverity(Severity.Medium);
            } else if (percent<95){
                entity.setScoreSeverity(Severity.High);
            } else {
                entity.setScoreSeverity(Severity.Critical);
            }
        });

    }

    private void populateEntityNamesOnAlert(){
        alerts.forEach((alert -> {
            Entity entity1 = getEntityByName(alert.getEntityName());
            alert.setEntityId(entity1.getId());
            int alertsCount = entity1.getAlertsCount();
            entity1.setAlertsCount(alertsCount+1);

        }));
    }

    public Entity getEntityByName(String entityName) {
        List<Entity> immutableUsersForStreaming = Collections.unmodifiableList(this.entities);
        return immutableUsersForStreaming.stream()
                        .filter(user -> entityName.equals(user.getUsername()))
                        .findAny()
                        .orElse(createAndReturnEntity(entityName));
    }

    private Entity createAndReturnEntity(String entityName){
        Entity entity = new Entity();
        entity.setMockId(entityName);
        entity.setUsername(entityName);
        entity.setNoDomainUsername(entityName);
        entity.setDisplayName(entityName);
        entity.setScore(0);
        entity.setScoreSeverity(Severity.Low);
        entity.setNoDomainUsername(entityName);
        entity.setAlertsCount(0);
        entity.setFollowed(false);
        entity.setSearchField(entityName);

        boolean isExist=this.getEntities().stream()
                .filter(tempUser -> entityName.equals(entity.getUsername()))
                .count()>0;
        if(isExist) {
            this.getEntities().add(entity);
        }
        return entity;
    }

    private void populateEvidenceToAlerts(){
        alerts.forEach(alert->{
                alert.setDataSourceAnomalyTypePair(new HashSet<>());
                List<Evidence> dummyEvidences = new ArrayList<>();
                alert.setEvidences(dummyEvidences);
        });

        indicators.forEach((indicator) ->{
           long startTime = indicator.getStartDate();
           long endTime = indicator.getEndDate();
           String username = indicator.getEntityName();

           Alert a = getAlertsByEntityNameAndTime(username,startTime,endTime,indicator.getTimeframe());
           if (a!=null) {
               List<Evidence> indicators = a.getEvidences();

               indicators.add(indicator);
               a.setEvidenceSize(indicators.size());
               a.getDataSourceAnomalyTypePair().add(new DataSourceAnomalyTypePair(indicator.getDataEntitiesIds().get(0), indicator.getAnomalyType()));
           }



        });

        alerts.forEach(alert->{
            if (CollectionUtils.isEmpty(alert.getEvidences()) && alert.getEvidenceSize()>0){
                //Alert without demo indicators
                List<Evidence> dummyEvidences = new ArrayList<>();
                alert.setEvidences(dummyEvidences);
                for (int i=0;i<alert.getEvidenceSize();i++) {
                    Evidence e = new Evidence();
                    e.setName("Abnormal Computer Accessed Remotely");
                    e.setAnomalyType("Abnormal Computer Accessed Remotely");
                    e.setAnomalyTypeFieldName("abnormal_computer_accessed_remotely");
                    e.setAnomalyValue("DC-02");
                    e.setScore(70);
                    e.setDataEntitiesIds(Arrays.asList(DataSourceType.ACTIVE_DIRECTORY.getValue()));
                    e.setTimeframe(EvidenceTimeframe.Hourly);
                    e.setStartDate(1498636800000L);
                    e.setEndDate(1498640400000L);
                    e.setEntityName(alert.getEntityName());
                    e.setEntityType(EntityType.User);
                    e.setNumOfEvents(1);
                    e.setEvidenceType(EvidenceType.AnomalyAggregatedEvent);
                    e.setMockId("4");
                    dummyEvidences.add(e);
                }

            }
        });


    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public List<Evidence> getIndicators() {
        return indicators;
    }

    public List<Alert> getAlertsByEntityName(String entityName) {
        return alerts.stream().filter(alert -> alert.getEntityName().equals(entityName)).collect(Collectors.toList());
    }

    public Alert getAlertsByEntityNameAndTime(String username, long startTime, long endTime, EvidenceTimeframe evidenceTimeframe) {
        try {
            return alerts.stream().filter(alert ->
                    alert.getEntityName().equals(username) &&
                            alert.getStartDate() <= startTime &&
                            alert.getEndDate() >= startTime &&
                            evidenceTimeframe.name().equals(alert.getTimeframe().name())


            ).findAny().orElse(null);
        } catch (RuntimeException e){
            System.out.println("No alert for indicators: startTime: "+startTime+" end time: "+endTime+ " username "+username);

            throw e;
        }
    }

    public void updateOrAddAlert(Alert newAlert) {
        this.alerts.removeIf(alert -> alert.getId().equals(newAlert.getId()));
        this.alerts.add(newAlert);
    }

    public List<SupportingInformationEntry> getSupportingInformation(String evidenceId) {
        if (this.suppotingInformationForIndicatorId.size()>0) {
            return this.suppotingInformationForIndicatorId.get(evidenceId);
        }
        return null;
    }
}
