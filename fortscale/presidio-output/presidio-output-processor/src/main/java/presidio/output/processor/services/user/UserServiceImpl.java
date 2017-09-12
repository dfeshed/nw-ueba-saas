package presidio.output.processor.services.user;

import fortscale.utils.logging.Logger;
import org.springframework.data.domain.Page;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;

import java.util.*;

/**
 * Created by efratn on 22/08/2017.
 */
public class UserServiceImpl implements UserService {

    private static Logger log = Logger.getLogger(UserServiceImpl.class);

    private static final int USERS_SAVE_PAGE_SIZE = 1000;

    private final EventPersistencyService eventPersistencyService;
    private final UserPersistencyService userPersistencyService;
    private final UserScoreService userScoreService;
    private final String TAG_ADMIN = "admin";


    private int alertEffectiveDurationInDays;//How much days an alert can affect on the user score
    public int defaultUsersBatchSize;

    public UserServiceImpl(EventPersistencyService eventPersistencyService,
                           UserPersistencyService userPersistencyService,
                           UserScoreService userScoreService,
                           int alertEffectiveDurationInDays,
                           int defaultUsersBatchSize) {
        this.eventPersistencyService = eventPersistencyService;
        this.userPersistencyService = userPersistencyService;
        this.userScoreService = userScoreService;
        this.alertEffectiveDurationInDays = alertEffectiveDurationInDays;
        this.defaultUsersBatchSize = defaultUsersBatchSize;
    }

    @Override
    public User createUserEntity(String userId) {
        UserDetails userDetails = getUserDetails(userId);
        if(userDetails == null) {
            return null;
        }
        return new User(userDetails.getUserId(), userDetails.getUserName(), userDetails.getUserDisplayName(), userDetails.getTags());
    }

    @Override
    public User findUserById(String userId) {
        return userPersistencyService.findUserById(userId);
    }

    @Override
    public void save(List<User> users) {
        userPersistencyService.save(users);
    }

    private UserDetails getUserDetails(String userId) {
        EnrichedEvent event = eventPersistencyService.findLatestEventForUser(userId);
        if(event == null) {
            log.error("no events were found for user {}", userId);
            return null;
        }
        String userDisplayName = event.getUserDisplayName();
        String userName = event.getUserName();
        List<String> tags = new ArrayList<>();
        if (event.getAdditionalInfo().get(EnrichedEvent.IS_USER_ADMIN) != null) {
            tags.add(TAG_ADMIN);
        }
        return new UserDetails(userName, userDisplayName, userId, tags);
    }

    public void setClassification(User user, List<String> classification) {
        user.addAlertClassifications(classification);
    }

    @Override
    public void setUserAlertData(User user, List<String> classification, List<String> indicators) {
        user.setAlertClassifications(classification);
        user.setIndicators(indicators);

        int alertsCount = user.getAlertsCount();
        user.setAlertsCount(alertsCount++);
    }

    @Override
    public boolean updateAllUsersAlertData() {

        //Get map of users ids to new score and alerts count
        Map<String, UsersAlertData> aggregatedUserScore = userScoreService.calculateUserScores(alertEffectiveDurationInDays);

        //Get users in batches and update the score only if it changed, and add to changesUsers
        Set<String> usersIDForBatch = new HashSet<>();
        List<User> changedUsers = new ArrayList<>();
        for (Map.Entry<String, UsersAlertData> entry : aggregatedUserScore.entrySet()) {

            usersIDForBatch.add(entry.getKey());
            if (usersIDForBatch.size() < defaultUsersBatchSize) {
                continue;
            }
            //Update user score batch
            changedUsers.addAll(updateUserAlertDataForBatch(aggregatedUserScore, usersIDForBatch));


            //After batch calculation, reset the set
            usersIDForBatch.clear();

        }

        if (!usersIDForBatch.isEmpty()) {
            //there is leftover smaller then batch size
            changedUsers.addAll(updateUserAlertDataForBatch(aggregatedUserScore, usersIDForBatch));
        }

        //Persist users that the score changed
        log.info(changedUsers.size() + " users changed. Saving to database");

        Double pages = Math.ceil(changedUsers.size() / (USERS_SAVE_PAGE_SIZE * 1D));
        for (int i = 0; i < pages.intValue(); i++) {
            List<User> page = changedUsers.subList(i * USERS_SAVE_PAGE_SIZE, Math.min((i + 1) * USERS_SAVE_PAGE_SIZE, changedUsers.size()));
            userPersistencyService.save(page);
        }
        log.info(changedUsers.size() + " users saved to database");

        //Clean users which not have alert in the last 90 days, but still have score
        userScoreService.clearUserScoreForUsersThatShouldNotHaveScore(aggregatedUserScore.keySet());

        return true;
    }

    /**
     * @param aggregatedUserScore - all the users which have at least one alert in the last 3 month with the new score
     * @param usersIDForBatch     - only the ids in the current handled batch
     * @return List of updated users
     */
    public List<User> updateUserAlertDataForBatch(Map<String, UsersAlertData> aggregatedUserScore, Set<String> usersIDForBatch) {
        log.info("Updating user batch (without persistence)- batch contain: " + usersIDForBatch.size() + " users");
        List<User> changedUsers = new ArrayList<>();
        UserQuery.UserQueryBuilder userQueryBuilder = new UserQuery.UserQueryBuilder().filterByUsersIds(new ArrayList<>(usersIDForBatch))
                .pageNumber(0)
                .pageSize(usersIDForBatch.size());
        UserQuery userQuery = userQueryBuilder.build();
        Page<User> users = userPersistencyService.find(userQuery);
        users.forEach(user -> {
            double newUserScore = aggregatedUserScore.get(user.getUserId()).getUserScore();
            if (user.getScore() != newUserScore) {
                user.setScore(newUserScore);
                user.setAlertsCount(user.getAlertsCount() + 1);
                changedUsers.add(user);
            }
        });

        return changedUsers;
    }

}
