package fortscale.services.configuration.Impl;

import fortscale.domain.core.Severity;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by shays on 05/06/2016.
 */
public class UserScoreConfiguration {
    private long daysRelevantForUnresolvedAlerts;

    public double contributionOfLowSeverityAlert;
    public double contributionOfMediumSeverityAlert;
    public double contributionOfHighSeverityAlert;
    public double contributionOfCriticalSeverityAlert;

    public double minPercentileForUserSeverityLow;
    public double minPercentileForUserSeverityMedium;
    public double minPercentileForUserSeverityHigh;
    public double minPercentileForUserSeverityCritical;

    public NavigableMap<Double, Severity> percentilesToSeveriyConfigurationMap;


    public UserScoreConfiguration() {
        //Create a Navigable map which get the user score and return the match user severity
        percentilesToSeveriyConfigurationMap = new TreeMap<>();

    }



    public long getDaysRelevantForUnresolvedAlerts() {
        return daysRelevantForUnresolvedAlerts;
    }

    public void setDaysRelevantForUnresolvedAlerts(long daysRelevantForUnresolvedAlerts) {
        this.daysRelevantForUnresolvedAlerts = daysRelevantForUnresolvedAlerts;
    }

    public double getContributionOfLowSeverityAlert() {
        return contributionOfLowSeverityAlert;
    }

    public void setContributionOfLowSeverityAlert(double contributionOfLowSeverityAlert) {
        this.contributionOfLowSeverityAlert = contributionOfLowSeverityAlert;
    }

    public double getContributionOfMediumSeverityAlert() {
        return contributionOfMediumSeverityAlert;
    }

    public void setContributionOfMediumSeverityAlert(double contributionOfMediumSeverityAlert) {
        this.contributionOfMediumSeverityAlert = contributionOfMediumSeverityAlert;
    }

    public double getContributionOfHighSeverityAlert() {
        return contributionOfHighSeverityAlert;
    }

    public void setContributionOfHighSeverityAlert(double contributionOfHighSeverityAlert) {
        this.contributionOfHighSeverityAlert = contributionOfHighSeverityAlert;
    }

    public double getContributionOfCriticalSeverityAlert() {
        return contributionOfCriticalSeverityAlert;
    }

    public void setContributionOfCriticalSeverityAlert(double contributionOfCriticalSeverityAlert) {
        this.contributionOfCriticalSeverityAlert = contributionOfCriticalSeverityAlert;
    }


    public double getMinPercentileForUserSeverityLow() {
        return minPercentileForUserSeverityLow;
    }

    public void setMinPercentileForUserSeverityLow(double minPercentileForUserSeverityLow) {
        this.minPercentileForUserSeverityLow = minPercentileForUserSeverityLow;
        percentilesToSeveriyConfigurationMap.put(this.getMinPercentileForUserSeverityLow(), Severity.Low);
    }

    public double getMinPercentileForUserSeverityMedium() {
        return minPercentileForUserSeverityMedium;
    }

    public void setMinPercentileForUserSeverityMedium(double minPercentileForUserSeverityMedium) {
        this.minPercentileForUserSeverityMedium = minPercentileForUserSeverityMedium;
        percentilesToSeveriyConfigurationMap.put(this.getMinPercentileForUserSeverityMedium(), Severity.Medium);
    }

    public double getMinPercentileForUserSeverityHigh() {
        return minPercentileForUserSeverityHigh;
    }

    public void setMinPercentileForUserSeverityHigh(double minPercentileForUserSeverityHigh) {
        this.minPercentileForUserSeverityHigh = minPercentileForUserSeverityHigh;
        percentilesToSeveriyConfigurationMap.put(this.getMinPercentileForUserSeverityHigh(), Severity.High);
    }

    public double getMinPercentileForUserSeverityCritical() {
        return minPercentileForUserSeverityCritical;
    }

    public void setMinPercentileForUserSeverityCritical(double minPercentileForUserSeverityCritical) {
        this.minPercentileForUserSeverityCritical = minPercentileForUserSeverityCritical;
        percentilesToSeveriyConfigurationMap.put(this.getMinPercentileForUserSeverityCritical(), Severity.Critical);

    }

    public Severity fetchSeverity(double userScroe){
        return percentilesToSeveriyConfigurationMap.floorEntry(userScroe).getValue();
    }

    /**
     *
     * @param severity
     * @return the score that the specific alert severity contribute
     */
    public double fetchContributionBySeverity(Severity severity) {
        switch (severity) {
            case Critical:
                return this.getContributionOfCriticalSeverityAlert();
            case High:
                return this.getContributionOfHighSeverityAlert();
            case Medium:
                return this.getContributionOfMediumSeverityAlert();
            case Low:
                return this.getContributionOfLowSeverityAlert();
            default:
                throw new RuntimeException("Severity is not legal");
        }

    }
}
