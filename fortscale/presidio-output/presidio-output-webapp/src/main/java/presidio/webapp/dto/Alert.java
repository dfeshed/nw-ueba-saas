package presidio.webapp.dto;


import java.io.Serializable;
import java.time.Instant;


public class Alert implements Serializable {
    private String id;
    private String alertClassification;
    private Instant startDate;
    private Instant endDate;
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

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
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

    public Integer getIndicatorsNum() {
        return indicatorsNum;
    }

    public void setIndicatorsNum(Integer indicatorsNum) {
        this.indicatorsNum = indicatorsNum;
    }
}
