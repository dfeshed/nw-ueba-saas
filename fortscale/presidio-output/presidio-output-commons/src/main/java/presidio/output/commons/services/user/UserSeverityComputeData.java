package presidio.output.commons.services.user;

public class UserSeverityComputeData {
    private Double percentageOfUsers;
    private Double minimumDeltaFactor;
    private Double maximumUsers;

    public UserSeverityComputeData(double percentageOfUsers, double minimumDeltaFactor, Double maximumUser) {
        this.percentageOfUsers = percentageOfUsers;
        this.minimumDeltaFactor = minimumDeltaFactor;
        this.maximumUsers = maximumUser;
    }

    public UserSeverityComputeData(double percentageOfUsers) {
        this.percentageOfUsers = percentageOfUsers;
    }

    public UserSeverityComputeData(Double percentageOfUsers, Double minimumDeltaFactor) {
        this.percentageOfUsers = percentageOfUsers;
        this.minimumDeltaFactor = minimumDeltaFactor;
    }

    public Double getPercentageOfUsers() {
        return percentageOfUsers;
    }

    public void setPercentageOfUsers(Double percentageOfUsers) {
        this.percentageOfUsers = percentageOfUsers;
    }

    public Double getMinimumDeltaFactor() {
        return minimumDeltaFactor;
    }

    public void setMinimumDeltaFactor(Double minimumDeltaFactor) {
        this.minimumDeltaFactor = minimumDeltaFactor;
    }

    public Double getMaximumUsers() {
        return maximumUsers;
    }

    public void setMaximumUsers(Double maximumUsers) {
        this.maximumUsers = maximumUsers;
    }
}