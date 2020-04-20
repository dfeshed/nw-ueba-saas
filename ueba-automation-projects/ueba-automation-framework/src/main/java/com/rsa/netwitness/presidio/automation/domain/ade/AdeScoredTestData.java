package com.rsa.netwitness.presidio.automation.domain.ade;

/**
 * Data Provides class fore ADE Scored collections tests
 */
public class AdeScoredTestData {

    private String testUser;
    private int lowestScore;
    private int highestScore;
    private String collection;
    private long expectedCount;
    private String testFailedMessage;

    public AdeScoredTestData(String testUser, int lowestScore, int highestScore, String collection, long expectedCount, String testFailedMessage) {
        this.testUser = testUser;
        this.lowestScore = lowestScore;
        this.highestScore = highestScore;
        this.collection = collection;
        this.expectedCount = expectedCount;
        this.testFailedMessage = testFailedMessage;
    }

    public String getTestUser() {
        return testUser;
    }

    public void setTestUser(String testUser) {
        this.testUser = testUser;
    }

    public int getLowestScore() {
        return lowestScore;
    }

    public void setLowestScore(int lowestScore) {
        this.lowestScore = lowestScore;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public long getExpectedCount() {
        return expectedCount;
    }

    public String getTestFailedMessage() {
        return testFailedMessage;
    }

    public void setTestFailedMessage(String testFailedMessage) {
        this.testFailedMessage = testFailedMessage;
    }

    public void setExpectedCount(long expectedCount) {
        this.expectedCount = expectedCount;
    }

    @Override
    public String toString() {
        return "AdeScoredTestData{" +
                "testUser='" + testUser + '\'' +
                ", lowestScore=" + lowestScore +
                ", highestScore=" + highestScore +
                ", collection='" + collection + '\'' +
                ", expectedCount=" + expectedCount +
                ", testFailedMessage='" + testFailedMessage + '\'' +
                '}';
    }
}
