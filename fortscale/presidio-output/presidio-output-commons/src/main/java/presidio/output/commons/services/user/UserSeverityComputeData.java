package presidio.output.commons.services.user;

public class UserSeverityComputeData {
    private double percentageOfUsers;
    private double minimumDelta;
    private Double maximumUsers;

    public UserSeverityComputeData(double percentageOfUsers, double minimumDelta, Double maximumUser) {
        this.percentageOfUsers = percentageOfUsers;
        this.minimumDelta = minimumDelta;
        this.maximumUsers = maximumUser;
    }

    public UserSeverityComputeData(double percentageOfUsers, double minimumDelta) {
        this.percentageOfUsers = percentageOfUsers;
        this.minimumDelta = minimumDelta;
    }

    public double getPercentageOfUsers() {
        return percentageOfUsers;
    }

    public void setPercentageOfUsers(double percentageOfUsers) {
        this.percentageOfUsers = percentageOfUsers;
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