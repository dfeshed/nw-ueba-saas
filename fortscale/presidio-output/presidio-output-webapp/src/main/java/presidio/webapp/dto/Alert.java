package presidio.webapp.dto;

import java.time.Instant;

public class Alert {
    private String id;
    private String name;
    private Instant startDate;
    private Instant endDate;
    private String username;
    private Long score;
    private Integer indicatorsNum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public void setIndicatorsNum(Integer indicatorsNum) {
        this.indicatorsNum = indicatorsNum;
    }

    public Integer getIndicatorsNum() {
        return indicatorsNum;
    }
}
