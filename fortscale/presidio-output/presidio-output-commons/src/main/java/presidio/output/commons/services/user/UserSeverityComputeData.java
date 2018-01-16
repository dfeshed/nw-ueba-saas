package presidio.output.commons.services.user;

public class UserSeverityComputeData {
    private double percentageIfUsers;
    private double minimumDelta;
    private Double maximumUsers;

    public UserSeverityComputeData(double percentageIfUsers, double minimumDelta, Double maximumUser) {
        this.percentageIfUsers = percentageIfUsers;
        this.minimumDelta = minimumDelta;
        this.maximumUsers = maximumUser;
    }

    public UserSeverityComputeData(double percentageIfUsers, double minimumDelta) {
        this.percentageIfUsers = percentageIfUsers;
        this.minimumDelta = minimumDelta;
    }

    public double getPercentageIfUsers() {
        return percentageIfUsers;
    }

    public void setPercentageIfUsers(double percentageIfUsers) {
        this.percentageIfUsers = percentageIfUsers;
    }

    public double getMinimumDelta() {
        return minimumDelta;
    }

    public void setMinimumDelta(double minimumDelta) {
        this.minimumDelta = minimumDelta;
    }

    public Double getMaximumUsers() {
        return maximumUsers;
    }

    public void setMaximumUsers(Double maximumUsers) {
        this.maximumUsers = maximumUsers;
    }
}