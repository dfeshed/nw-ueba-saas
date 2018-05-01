package fortscale.remote.fake.creators;

import presidio.output.client.model.User;
import presidio.output.client.model.UsersWrapper;

public class FakeUsersCreator {

    private FakeCreatorUtils fakeCreatorUtils;
    private FakeAlertsCreator fakeAlertsCreator;

    public FakeUsersCreator(FakeCreatorUtils fakeCreatorUtils,FakeAlertsCreator fakeAlertsCreator) {
        this.fakeCreatorUtils = fakeCreatorUtils;
        this.fakeAlertsCreator = fakeAlertsCreator;
    }

    public User getUser(String id, String name){
        User user = new User();
        user.setId(id);
        user.setUserDisplayName(name);
        user.setScore(100);
        user.setSeverity(User.SeverityEnum.CRITICAL);
        user.setUsername(name);
        user.setUserId(id);

        fakeAlertsCreator.getAlerts(10).getAlerts().forEach(alert -> {
            user.addAlertClassificationsItem(alert.getClassifiation().get(0));
            user.addAlertsItem(alert);

        });
        user.setAlertsCount(10);

        return user;
    }
    public UsersWrapper getUsers(int amount){
        UsersWrapper usersWrapper = new UsersWrapper();
        for (int i=0;i<amount;i++) {
            usersWrapper.addUsersItem(getUser("1", "Cool User"));

        }
        usersWrapper.setTotal(amount);

        return usersWrapper;

    }
}
