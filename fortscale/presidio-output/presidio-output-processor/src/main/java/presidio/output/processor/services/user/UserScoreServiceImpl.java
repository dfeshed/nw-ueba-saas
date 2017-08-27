package presidio.output.processor.services.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.users.UserPersistencyService;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by shays on 27/08/2017.
 */
public class UserScoreServiceImpl implements UserScoreService{
    private UserPersistencyService userPersistencyService;

    public int defaultUsersBatchFile;

    private int percentThresholdCritical;

    private int percentThresholdHigh;

    private int percentThresholdMedium;

    private Map<AlertEnums.AlertSeverity, Double> alertSeverityToScoreContribution;

    public UserScoreServiceImpl(UserPersistencyService userPersistencyService,
                                int defaultUsersBatchFile,
                                int percentThresholdCritical,
                                int percentThresholdHigh,
                                int percentThresholdMedium,
                                double alertContributionLow,
                                double alertContributionMedium,
                                double alertContributionHigh,
                                double alertContributionCritical) {
        this.userPersistencyService = userPersistencyService;
        this.defaultUsersBatchFile = defaultUsersBatchFile;
        this.percentThresholdCritical = percentThresholdCritical;
        this.percentThresholdHigh = percentThresholdHigh;
        this.percentThresholdMedium = percentThresholdMedium;

        alertSeverityToScoreContribution=new TreeMap<>();
        alertSeverityToScoreContribution.put(AlertEnums.AlertSeverity.CRITICAL,alertContributionCritical);
        alertSeverityToScoreContribution.put(AlertEnums.AlertSeverity.HIGH,alertContributionHigh);
        alertSeverityToScoreContribution.put(AlertEnums.AlertSeverity.MEDIUM,alertContributionMedium);
        alertSeverityToScoreContribution.put(AlertEnums.AlertSeverity.LOW,alertContributionLow);


    }


    public void updateUserScore(Alert alert){
        AlertEnums.AlertSeverity alertSeverity = alert.getSeverity();
        double userScoreContribution = this.alertSeverityToScoreContribution.get(alertSeverity);
        //User user = userPersistencyService.findUserById(alert.get)
        User user = new User(); //wait for Maor's code
        double userScore = user.getUserScore();
        userScoreContribution.



    }

    /**
     * Calculate Severities map
     *
     * @return a navigable map which the key is the user score and the value is the severity
     * To get the severity of user with score X you need to "valueToPercentile.floorEntry(X).getValue()"
     */
    public TreeMap<Double, UserSeverity> getSeveritiesMap(double[] userScores){


        double value = 20;
        Percentile p =new Percentile();

        p.setData(userScores);


        TreeMap<Double, UserSeverity> severitiesMap = new TreeMap<>();
        double lowThresholdScore=0;
        double mediumThresholdScore = p.evaluate(percentThresholdMedium);
        double highThresholdScore = p.evaluate(percentThresholdHigh);
        double criticalThresholdScore = p.evaluate(percentThresholdCritical);

        severitiesMap.put(lowThresholdScore,UserSeverity.LOW);
        severitiesMap.put(mediumThresholdScore,UserSeverity.MEDIUM);
        severitiesMap.put(highThresholdScore,UserSeverity.HIGH);
        severitiesMap.put(criticalThresholdScore,UserSeverity.CRITICAL);


        return severitiesMap;

    }


    public void updateSeverities(){
       final double[] scores= getScoresArray();
       final  TreeMap<Double, UserSeverity> severitiesMap = getSeveritiesMap(scores);

       Page<User> page = userPersistencyService.find(new UserQuery.UserQueryBuilder().pageNumber(0).pageSize(defaultUsersBatchFile).sortField("score", true).build());

        while (page != null && page.hasContent()) {
            List<User> updatedUsers = new ArrayList<>();
            page.getContent().forEach(user -> {
                double userScore = user.getUserScore();
                UserSeverity newUserSeverity =  severitiesMap.floorEntry(userScore).getValue();
                if (!newUserSeverity.equals(user.getUserSeverity())){
                    user.setUserSeverity(newUserSeverity);
                    updatedUsers.add(user); //Update user only if severity changes
                }
            });

            if (updatedUsers.size()>0){
                userPersistencyService.save(updatedUsers);
            }
            page = getNextUserPage(page,"score",true);

        }
    }

    /**
     * Return the next user page or null if no next
     * @param page
     * @return
     */

    private Page<User> getNextUserPage(Page<User> page, String sortField, boolean ascendingOrder) {
        if (page.hasNext()) {
            Pageable pageable = page.nextPageable();
            page = userPersistencyService.find(new UserQuery.UserQueryBuilder().
                    pageNumber(pageable.getPageNumber()).
                    pageSize(pageable.getPageSize()).


                    sortField(sortField, ascendingOrder).
                    build());

        } else {
            page = null;
        }
        return page;
    }

    /**
     * This function load all users' score and store it in a double array
     */
    private double[] getScoresArray() {
        Page<User> page = userPersistencyService.find(new UserQuery.UserQueryBuilder().pageNumber(0).pageSize(1000).sortField("score", true).build());
        int numberOfElements = new Long(page.getNumberOfElements()).intValue();
        double[] scores = new double[numberOfElements];
        AtomicInteger courser = new AtomicInteger(0);


        while (page != null && page.hasContent()) {
            page.getContent().forEach(user -> {
                scores[courser.getAndAdd(1)] = user.getUserScore();
            });
            page = getNextUserPage(page,"score",true);

        }

        return scores;
    }

}
