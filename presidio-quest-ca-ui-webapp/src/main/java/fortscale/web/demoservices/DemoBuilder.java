package fortscale.web.demoservices;

import fortscale.common.dataentity.DataSourceType;
import fortscale.domain.core.*;
import fortscale.web.rest.entities.SupportingInformationEntry;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.activation.DataSource;
import java.util.*;

import java.util.stream.Collectors;

/**
 * Created by shays on 23/07/2017.
 */
//@Service("demoBuilder")
//@Profile("mock")
public class DemoBuilder {

    private Map<String, List<SupportingInformationEntry>> suppotingInformationForIndicatorId=new HashMap<>();
    private List<User> users;
    private List<Alert> alerts;
    private List<Evidence> indicators;

    public DemoBuilder(){
        try {

            this.users = new DemoUserFactory().getUsers();
            this.alerts = new DemoAlertFactory().getAlerts();
            DemoIndicatorsFactory demoIndicatorsFactory = new DemoIndicatorsFactory();
            this.indicators = demoIndicatorsFactory.getEvidences();
            suppotingInformationForIndicatorId = demoIndicatorsFactory.suppotingInformationForIndicatorId;

            populateUserNamesOnAlert();
            populateEvidenceToAlerts();
            populateUserSeverity();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DemoBuilder(List<User> users, List<Alert> alerts,  List<Evidence> indicators){

            this.users = users;
            this.alerts = alerts;
            this.indicators = indicators;


    }

    public void populateUserSeverity(){
        User userWithMaxScore = this.users.stream().max((user1,user2)->Double.compare(user1.getScore(),user2.getScore())).get();

        double maxScore =  userWithMaxScore.getScore();

        this.users.forEach(user->{
            double percent = 100*user.getScore()/maxScore;
            if (percent<70){
                user.setScoreSeverity(Severity.Low);
            } else if (percent<80){
                user.setScoreSeverity(Severity.Medium);
            } else if (percent<95){
                user.setScoreSeverity(Severity.High);
            } else {
                user.setScoreSeverity(Severity.Critical);
            }
        });

    }

    private void populateUserNamesOnAlert(){
        alerts.forEach((alert -> {
            User user1 = getUserByName(alert.getEntityName());
            alert.setEntityId(user1.getId());
            int alertsCount = user1.getAlertsCount();
            user1.setAlertsCount(alertsCount+1);

        }));
    }

    public User getUserByName(String userName) {
        List<User> immutableUsersForStreaming = Collections.unmodifiableList(this.users);
        return immutableUsersForStreaming.stream()
                        .filter(user -> userName.equals(user.getUsername()))
                        .findAny()
                        .orElse(createAndReturnUser(userName));
    }

    private User createAndReturnUser(String userName){
        User user= new User();
        user.setMockId(userName);
        user.setUsername(userName);
        user.setNoDomainUsername(userName);
        user.setDisplayName(userName);
        user.setScore(0);
        user.setScoreSeverity(Severity.Low);
        user.setNoDomainUsername(userName);
        user.setAlertsCount(0);
        user.setFollowed(false);
        user.setSearchField(userName);

        boolean isExist=this.getUsers().stream()
                .filter(tempUser -> userName.equals(user.getUsername()))
                .count()>0;
        if(isExist) {
            this.getUsers().add(user);
        }
        return user;
    }

    private User getDefaultUser(){
        User def = new User();
        def.setMockId("def");
        def.setUsername("default user");
        return def;
    }

    private void populateAlertsCountForUser(){
        Map<String, Long> counting = alerts.stream().collect(
                Collectors.groupingBy(Alert::getEntityName, Collectors.counting()));

        counting.forEach((user,count)->{
            getUserByName(user).setAlertsCount(count.intValue());
        });

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

           Alert a = getAlertsByUserNameAndTime(username,startTime,endTime,indicator.getTimeframe());
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

    public List<User> getUsers() {
        return users;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public List<Evidence> getIndicators() {
        return indicators;
    }

    public List<Alert> getAlertsByUserName(String username) {
        return alerts.stream().filter(alert -> alert.getEntityName().equals(username)).collect(Collectors.toList());
    }

    public Alert getAlertsByUserNameAndTime(String username, long startTime, long endTime, EvidenceTimeframe evidenceTimeframe) {
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
