package presidio.output.commons.services.user;

import com.google.common.collect.Iterables;
import fortscale.utils.logging.Logger;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import presidio.output.domain.records.UserScorePercentilesDocument;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.repositories.UserScorePrcentilesRepository;
import presidio.output.domain.services.users.UserPersistencyService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by efrat Noam on 12/4/17.
 */
public class UserSeverityServiceImpl implements UserSeverityService {

    private static final Logger logger = Logger.getLogger(UserSeverityServiceImpl.class);
    private static final String USER_SCORE_PERCENTILES_DOC_ID = "user-score-percentile-doc-id";

    private int percentThresholdCritical;
    private int percentThresholdHigh;
    private int percentThresholdMedium;

    @Autowired
    private UserScorePrcentilesRepository percentilesRepository;

    @Autowired
    private UserPersistencyService userPersistencyService;

    @Value("${user.severities.batch.size:2000}")
    private int defaultUsersBatchSize;

    public UserSeverityServiceImpl(int percentThresholdCritical,
                                int percentThresholdHigh,
                                int percentThresholdMedium) {
        this.percentThresholdCritical = percentThresholdCritical;
        this.percentThresholdHigh = percentThresholdHigh;
        this.percentThresholdMedium = percentThresholdMedium;
    }


    /**
     * Calculate severities map which defines the right user severity per user score calculated according to percentiles
     *
     * @return map from score to getSeverity
     */
    @Override
    public UserScoreToSeverity getSeveritiesMap(boolean recalcUserScorePercentiles) {
        if(!recalcUserScorePercentiles) {
            return getExistingUserScoreToSeverity();
        }

        //calculating percentiles according all user scores
        double[] userScores = getScoresArray();
        Percentile p = new Percentile();

        p.setData(userScores);

        double ceilScoreForLowSeverity = p.evaluate(percentThresholdMedium); //The maximum score that user score still considered low
        double ceilScoreForMediumSeverity = p.evaluate(percentThresholdHigh);//The maximum score that user score still considered medium
        double ceilScoreForHighSeverity = p.evaluate(percentThresholdCritical); //The maximum score that user score still considered high

        //Storing the new percentiles doc-
        UserScorePercentilesDocument percentileDoc = percentilesRepository.findOne(USER_SCORE_PERCENTILES_DOC_ID);
        percentileDoc.setCeilScoreForHighSeverity(ceilScoreForHighSeverity);
        percentileDoc.setCeilScoreForMediumSeverity(ceilScoreForMediumSeverity);
        percentileDoc.setCeilScoreForLowSeverity(ceilScoreForLowSeverity);
        percentilesRepository.save(percentileDoc);

        return new UserScoreToSeverity(ceilScoreForLowSeverity, ceilScoreForMediumSeverity, ceilScoreForHighSeverity);

    }

    private UserScoreToSeverity getExistingUserScoreToSeverity() {
        Iterable<UserScorePercentilesDocument> percentilesThresholds = percentilesRepository.findAll();

        if(! percentilesThresholds.iterator().hasNext()) { //no existing percentiles were found
            logger.debug("No user score percentile calculation results were found, setting scores thresholds to zero (all users will get LOW severity (till next daily calculation)");
            return new UserScoreToSeverity(-1, -1, -1);
        }

        if (Iterables.size(percentilesThresholds) > 1) {
            logger.error("Found more than 1 user score percentile calculation results, taking one of them randomly");
        }
        UserScorePercentilesDocument userSeverityPercentilesDoc = percentilesThresholds.iterator().next();
        return new UserScoreToSeverity(
                userSeverityPercentilesDoc.getCeilScoreForLowSeverity(),
                userSeverityPercentilesDoc.getCeilScoreForMediumSeverity(),
                userSeverityPercentilesDoc.getCeilScoreForHighSeverity());
    }

    @Override
    public void updateSeverities() {
        final UserSeverityServiceImpl.UserScoreToSeverity severitiesMap = getSeveritiesMap(true);
        UserQuery.UserQueryBuilder userQueryBuilder =
                new UserQuery.UserQueryBuilder()
                        .pageNumber(0)
                        .pageSize(defaultUsersBatchSize)
                        .sort(new Sort(new Sort.Order(Sort.Direction.ASC, User.SCORE_FIELD_NAME)));
        Page<User> page = userPersistencyService.find(userQueryBuilder.build());

        while (page != null && page.hasContent()) {
            logger.info("Updating severity for user's page: " + page.toString());
            updateSeveritiesForUsersList(severitiesMap, page.getContent(), true);
            page = getNextUserPage(userQueryBuilder, page);

        }
    }

    public void updateSeveritiesForUsersList(List<User> users, boolean persistChanges) {
        final UserSeverityServiceImpl.UserScoreToSeverity severitiesMap = getSeveritiesMap(false);
        updateSeveritiesForUsersList(severitiesMap, users, persistChanges);

    }

    /**
     * This function load all users' score and store it in a double array
     * Only for user scores above 0
     */
    private double[] getScoresArray() {


        Sort sort = new Sort(Sort.Direction.ASC, User.SCORE_FIELD_NAME);
        UserQuery.UserQueryBuilder userQueryBuilder = new UserQuery.UserQueryBuilder().minScore(1).pageNumber(0).pageSize(this.defaultUsersBatchSize).sort(sort);
        Page<User> page = userPersistencyService.find(userQueryBuilder.build());
        int numberOfElements = new Long(page.getTotalElements()).intValue();
        double[] scores = new double[numberOfElements];
        AtomicInteger courser = new AtomicInteger(0);


        while (page != null && page.hasContent()) {
            page.getContent().forEach(user -> {
                scores[courser.getAndAdd(1)] = user.getScore();
            });
            page = getNextUserPage(userQueryBuilder, page);

        }

        return scores;
    }

    /**
     * Return the next user page or null if no next
     *
     * @param page
     * @return
     */

    private Page<User> getNextUserPage(UserQuery.UserQueryBuilder userQueryBuilder, Page<User> page) {
        if (page.hasNext()) {
            Pageable pageable = page.nextPageable();
            userQueryBuilder.pageNumber(pageable.getPageNumber());
            page = userPersistencyService.find(userQueryBuilder.build());

        } else {
            page = null;
        }
        return page;
    }

    private void updateSeveritiesForUsersList(UserSeverityServiceImpl.UserScoreToSeverity severitiesMap, List<User> users, boolean persistChanges) {
        List<User> updatedUsers = new ArrayList<>();
        if (users == null) {
            return;
        }
        users.forEach(user -> {
            double userScore = user.getScore();
            UserSeverity newUserSeverity = severitiesMap.getUserSeverity(userScore);

            logger.debug("Updating user severity for userId: " + user.getUserId());
            if (!newUserSeverity.equals(user.getSeverity())) {
                user.setSeverity(newUserSeverity);
                updatedUsers.add(user); //Update user only if severity changes
            }
        });

        if (updatedUsers.size() > 0 && persistChanges) {
            userPersistencyService.save(updatedUsers);
        }
    }

    public static class UserScoreToSeverity {
        private double ceilScoreForLowSeverity;
        private double ceilScoreForMediumSeverity;
        private double ceilScoreForHighSeverity;

        public UserScoreToSeverity(double ceilScoreForLowSeverity, double ceilScoreForMediumSeverity, double ceilScoreForHighSeverity) {
            this.ceilScoreForLowSeverity = ceilScoreForLowSeverity;
            this.ceilScoreForMediumSeverity = ceilScoreForMediumSeverity;
            this.ceilScoreForHighSeverity = ceilScoreForHighSeverity;
        }

        public UserSeverity getUserSeverity(double score) {
            if(ceilScoreForHighSeverity == -1) {
                return UserSeverity.LOW;
            }

            if (score <= ceilScoreForLowSeverity) {
                return UserSeverity.LOW;
            } else if (score <= ceilScoreForMediumSeverity) {
                return UserSeverity.MEDIUM;
            } else if (score <= ceilScoreForHighSeverity) {
                return UserSeverity.HIGH;
            } else {
                return UserSeverity.CRITICAL;
            }

        }
    }
}
