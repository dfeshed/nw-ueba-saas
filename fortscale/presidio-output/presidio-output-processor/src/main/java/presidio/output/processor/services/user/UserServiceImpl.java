package presidio.output.processor.services.user;

import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import presidio.output.commons.services.user.UserSeverityService;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.events.EnrichedUserEvent;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by efratn on 22/08/2017.
 */
public class UserServiceImpl implements UserService {

    private static Logger log = Logger.getLogger(UserServiceImpl.class);

    private static final int USERS_SAVE_PAGE_SIZE = 1000;

    private final AlertPersistencyService alertPersistencyService;
    private final EventPersistencyService eventPersistencyService;
    private final UserPersistencyService userPersistencyService;
    private final UserScoreService userScoreService;
    private final UserSeverityService userSeverityService;
    private final String TAG_ADMIN = "admin";

    private final int alertEffectiveDurationInDays;//How much days an alert can affect on the user score
    private final int defaultUsersBatchSize;

    public UserServiceImpl(EventPersistencyService eventPersistencyService,
                           UserPersistencyService userPersistencyService,
                           AlertPersistencyService alertPersistencyService,
                           UserScoreService userScoreService,
                           UserSeverityService userSeverityService,
                           int alertEffectiveDurationInDays,
                           int defaultUsersBatchSize) {
        this.eventPersistencyService = eventPersistencyService;
        this.userPersistencyService = userPersistencyService;
        this.alertPersistencyService = alertPersistencyService;
        this.userScoreService = userScoreService;
        this.userSeverityService = userSeverityService;
        this.alertEffectiveDurationInDays = alertEffectiveDurationInDays;
        this.defaultUsersBatchSize = defaultUsersBatchSize;
    }

    public int getDefaultUsersBatchSize() {
        return defaultUsersBatchSize;
    }

    @Override
    public User createUserEntity(String userId) {
        UserDetails userDetails = getUserDetails(userId);
        if (userDetails == null) {
            return null;
        }
        return new User(userDetails.getUserId(), userDetails.getUserName(), userDetails.getUserDisplayName(), userDetails.getTags());
    }

    @Override
    public User findUserById(String userId) {
        return userPersistencyService.findUserById(userId);
    }

    @Override
    public List<User> save(List<User> users) {
        Iterable<User> savedUsers = userPersistencyService.save(users);
        List<User> usersList = IteratorUtils.toList(savedUsers.iterator());
        return usersList;
    }

    private UserDetails getUserDetails(String userId) {
        List<String> collectionNames = userSeverityService.collectionNamesByOrderForEvents();
        EnrichedUserEvent event = eventPersistencyService.findLatestEventForUser(userId, collectionNames);
        if (event == null) {
            log.error("no events were found for user {}", userId);
            return null;
        }
        String userDisplayName = event.getUserDisplayName();
        String userName = event.getUserName();
        List<String> tags = new ArrayList<>();
        if (event.getAdditionalInfo().get(EnrichedUserEvent.IS_USER_ADMIN) != null && Boolean.parseBoolean(event.getAdditionalInfo().get(EnrichedUserEvent.IS_USER_ADMIN))) {
            tags.add(TAG_ADMIN);
        }
        return new UserDetails(userName, userDisplayName, userId, tags);
    }


    @Override
    public void setUserAlertData(User user, UsersAlertData usersAlertData) {
        if (CollectionUtils.isNotEmpty(usersAlertData.getClassifications())) {
            user.setAlertClassifications(new ArrayList<String>(usersAlertData.getClassifications()));
        }
        if (CollectionUtils.isNotEmpty(usersAlertData.getIndicators())) {
            user.setIndicators(new ArrayList<String>(usersAlertData.getIndicators()));
        }
        user.setAlertsCount(usersAlertData.getAlertsCount());
        user.setScore(usersAlertData.getUserScore());
        UserSeverity newSeverity = userSeverityService.getSeveritiesMap(false).getUserSeverity(user.getScore());
        user.setSeverity(newSeverity);
    }

    @Override
    public void setUserAlertDataToDefault(User user) {
        user.setAlertClassifications(null);
        user.setIndicators(null);
        user.setAlertsCount(0);
        user.setScore(0);
        UserSeverity newSeverity = UserSeverity.LOW;
        user.setSeverity(newSeverity);
    }

    @Override
    public void addUserAlertData(User user, UsersAlertData usersAlertData) {
        List<String> classificationUnion = unionOfCollectionsToList(user.getAlertClassifications(), usersAlertData.getClassifications());
        user.setAlertClassifications(classificationUnion);
        List<String> indicatorsUnion = unionOfCollectionsToList(user.getIndicators(), usersAlertData.getIndicators());
        user.setIndicators(indicatorsUnion);
        user.incrementAlertsCountByNumber(usersAlertData.getAlertsCount());
        user.incrementUserScoreByNumber(usersAlertData.getUserScore());
        UserSeverity newSeverity = userSeverityService.getSeveritiesMap(false).getUserSeverity(user.getScore());
        user.setSeverity(newSeverity);
    }

    @Override
    public void updateUserData(Instant endDate) {
        log.debug("Starting Updating all users alert data.");
        updateAllUsersAlertData(endDate);
        log.debug("finished updating all users alert data.");
        userSeverityService.updateSeverities();
    }

    @Override
    public boolean updateAllUsersAlertData(Instant endDate) {

        //Get map of users ids to new score and alerts count
        Map<String, UsersAlertData> aggregatedUserScore = userScoreService.calculateUserScores(alertEffectiveDurationInDays, endDate);

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

        PageRequest pageRequest = new PageRequest(0, usersIDForBatch.size());
        Page<User> users = userPersistencyService.findByIds(usersIDForBatch, pageRequest);

        if (users.getTotalElements() != usersIDForBatch.size()) {
            log.error("Need to update {} users, but only {} users exists on elastic search", usersIDForBatch.size(), users.getTotalElements());
        }
        users.forEach(user -> {
            double newUserScore = aggregatedUserScore.get(user.getId()).getUserScore();
            if (user.getScore() != newUserScore) {
                setUserAlertData(user, aggregatedUserScore.get(user.getId()));
                changedUsers.add(user);
            }
        });

        return changedUsers;
    }

    public List<User> findUserByVendorUserIds(List<String> vendorUserId) {
        UserQuery userQuery = new UserQuery.UserQueryBuilder().filterByUsersIds(vendorUserId).build();

        Page<User> usersPage = this.userPersistencyService.find(userQuery);
        if (!usersPage.hasContent() || usersPage.getContent().size() < 1) {
            return null;
        }

        return usersPage.getContent();
    }

    private List<String> unionOfCollectionsToList(Collection col1, Collection col2) {
        if (CollectionUtils.isEmpty(col1) || CollectionUtils.isEmpty(col2)) {
            if (CollectionUtils.isEmpty(col1) && CollectionUtils.isEmpty(col2)) {
                return null;
            } else {
                return CollectionUtils.isEmpty(col1) ? new ArrayList<String>(col2) : new ArrayList<String>(col1);
            }
        } else {
            return (List<String>) CollectionUtils.union(col1, col2);
        }
    }

    @Override
    public void recalculateUserAlertData(User user) {
        List<Alert> alerts = alertPersistencyService.findByUserId(user.getId());
        UsersAlertData usersAlertData = new UsersAlertData();
        if (CollectionUtils.isNotEmpty(alerts)) {
            alerts.forEach(alert -> {
                if (alert.getContributionToUserScore() > 0) {
                    usersAlertData.addClassification(alert.alertPrimaryClassification());
                    usersAlertData.addIndicators(alert.getIndicatorsNames());
                    usersAlertData.incrementAlertsCount();
                    usersAlertData.incrementUserScore(alert.getContributionToUserScore());
                }

            });
            setUserAlertData(user, usersAlertData);
        } else {
            setUserAlertDataToDefault(user);
        }
    }

}
