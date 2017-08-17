package presidio.webapp.dto;

public class Alert {
    private String id;
    private String alertClassification;
    private long startDate;
    private long endDate;
    private String username;
    private double score;
    private Integer indicatorsNum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlertClassification() {
        return alertClassification;
    }

    public void setAlertClassification(String alertClassification) {
        this.alertClassification = alertClassification;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setIndicatorsNum(Integer indicatorsNum) {
        this.indicatorsNum = indicatorsNum;
    }

    public Integer getIndicatorsNum() {
        return indicatorsNum;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
}
