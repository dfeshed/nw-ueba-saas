package com.rsa.netwitness.presidio.automation.domain.ade;

import java.time.Instant;

/**
 * Data Provides class fore ADE Scored collections tests
 */
public class AdeRawEnrichedTestData {

    private String testUser;
    private Instant startInstant;
    private Instant endInstant;
    private long expectedCount;

    public AdeRawEnrichedTestData(String testUser, Instant startInstant, Instant endInstant, long expectedCount) {
        this.testUser = testUser;
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.expectedCount = expectedCount;
    }

    public String getTestUser() {
        return testUser;
    }

    public void setTestUser(String testUser) {
        this.testUser = testUser;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public void setStartInstant(Instant startInstant) {
        this.startInstant = startInstant;
    }

    public Instant getEndInstant() {
        return endInstant;
    }

    public void setEndInstant(Instant endInstant) {
        this.endInstant = endInstant;
    }

    public long getExpectedCount() {
        return expectedCount;
    }

    public void setExpectedCount(long expectedCount) {
        this.expectedCount = expectedCount;
    }

    @Override
    public String toString() {
        return "AdeRawEnrichedTestData{" +
                "testUser='" + testUser + '\'' +
                ", startInstant=" + startInstant +
                ", endInstant=" + endInstant +
                ", expectedCount=" + expectedCount +
                '}';
    }
}
