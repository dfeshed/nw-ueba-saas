package presidio.output.processor.services.user;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by efratn on 11/09/2017.
 */
public class UsersAlertData {
    private double userScore;
    private int alertsCount;
    private Set<String> classifications;
    private Set<String> indicators;


    public UsersAlertData() {
        classifications = new HashSet<String>();
        indicators = new HashSet<String>();
    }

    public UsersAlertData(double userScore, int alertsCount, String classification, List<String> indicators) {
        this.userScore = userScore;
        this.alertsCount = alertsCount;
        this.classifications = new HashSet<String>();
        this.indicators = new HashSet<String>();
        addClassification(classification);
        addIndicators(indicators);
    }

    public double getUserScore() {
        return userScore;
    }

    public int getAlertsCount() {
        return alertsCount;
    }

    public void setUserScore(double userScore) {
        this.userScore = userScore;
    }

    public void setAlertsCount(int alertsCount) {
        this.alertsCount = alertsCount;
    }

    public Set<String> getClassifications() {
        return classifications;
    }

    public void setClassifications(Set<String> classifications) {
        this.classifications = classifications;
    }

    public void addClassification(String classification) {
        if (StringUtils.isNotEmpty(classification)) {
            this.classifications.add(classification);
        }
    }

    public Set<String> getIndicators() {
        return indicators;
    }

    public void addIndicators(List<String> indicators) {
        if (indicators!=null) {
            this.indicators.addAll(indicators);
        }
    }

    public void incrementUserScore(double userScore) {
        this.userScore += userScore;
    }

    public void incrementAlertsCount() {
        this.alertsCount++;
    }
}
