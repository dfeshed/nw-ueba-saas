package presidio.output.commons.services.user;

public class UserSeverityComputeData {
    private double percentageOfUsers;
    private double minimumDeltaFactor;
    private Double maximumUsers;

    public UserSeverityComputeData(double percentageOfUsers, double minimumDeltaFactor, Double maximumUser) {
        this.percentageOfUsers = percentageOfUsers;
        this.minimumDeltaFactor = minimumDeltaFactor;
        this.maximumUsers = maximumUser;
    }

    public UserSeverityComputeData(double percentageOfUsers, double minimumDeltaFactor) {
        this.percentageOfUsers = percentageOfUsers;
        this.minimumDeltaFactor = minimumDeltaFactor;
    }

    public double getPercentageOfUsers() {
        return percentageOfUsers;
    }

    public void setPercentageOfUsers(double percentageOfUsers) {
        this.percentageOfUsers = percentageOfUsers;
    }

    public double getMinimumDeltaFactor() {
        return minimumDeltaFactor;
    }

    public void setMinimumDeltaFactor(double minimumDeltaFactor) {
        this.minimumDeltaFactor = minimumDeltaFactor;
    }

    public Double getMaximumUsers() {
        return maximumUsers;
    }

    public void setMaximumUsers(Double maximumUsers) {
        this.maximumUsers = maximumUsers;
    }
}