package presidio.output.processor.services.user;

/**
 * Created by efratn on 11/09/2017.
 */
public class UsersAlertData {
    private double userScore;
    private int alertsCount;

    public UsersAlertData(Double userScore, int alertsCount) {
        this.userScore = userScore;
        this.alertsCount = alertsCount;
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

    public void incrementUserScore(double userScore) {
        this.userScore += userScore;
    }

    public void incrementAlertsCount() {
        this.alertsCount++;
    }
}
