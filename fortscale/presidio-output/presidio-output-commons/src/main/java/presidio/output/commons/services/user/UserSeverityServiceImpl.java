package presidio.output.commons.services.user;

import fortscale.utils.logging.Logger;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import presidio.output.domain.records.PresidioRange;
import presidio.output.domain.records.UserSeveritiesRangeDocument;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.repositories.UserSeveritiesRangeRepository;
import presidio.output.domain.services.users.UserPersistencyService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by efrat Noam on 12/4/17.
 */
public class UserSeverityServiceImpl implements UserSeverityService {

    private static final Logger logger = Logger.getLogger(UserSeverityServiceImpl.class);

    private Map<UserSeverity, UserSeverityComputeData> severityToComputeDataMap;

    @Autowired
    private UserSeveritiesRangeRepository userSeveritiesRangeRepository;

    @Autowired
    private UserPersistencyService userPersistencyService;

    private UserPropertiesUpdateService userPropertiesUpdateService;

    @Value("${user.batch.size:2000}")
    private int defaultUsersBatchSize;

    public UserSeverityServiceImpl(Map<UserSeverity, UserSeverityComputeData> severityToComputeDataMap, UserPropertiesUpdateService userPropertiesUpdateService) {
        this.severityToComputeDataMap = severityToComputeDataMap;
        this.userPropertiesUpdateService = userPropertiesUpdateService;
    }

    /**
     * Calculate severities map which defines the right user severity per user score calculated according to percentiles
     *
     * @return map from score to getSeverity
     */
    @Override
    public UserScoreToSeverity getSeveritiesMap(boolean recalculateUserSeveritiesRange) {
        if (!recalculateUserSeveritiesRange) {
            return getExistingUserScoreToSeverity();
        }

        //calculating percentiles according all user scores
        double[] userScores = getScoresArray();
        UserSeveritiesRangeDocument userSeveritiesRangeDocument = createUserSeveritiesRangeDocument(userScores);
        userSeveritiesRangeRepository.save(userSeveritiesRangeDocument);

        return new UserScoreToSeverity(userSeveritiesRangeDocument.getSeverityToScoreRangeMap());
    }

    /**
     * Create the user severities range document from the received scores
     *
     * @param userScores
     * @return
     */
    protected UserSeveritiesRangeDocument createUserSeveritiesRangeDocument(double[] userScores) {

        Map<UserSeverity, PresidioRange<Double>> severityToScoreRangeMap = calculateUserSeverityRangeMap(userScores);

        UserSeveritiesRangeDocument userSeveritiesRangeDocument = new UserSeveritiesRangeDocument(severityToScoreRangeMap);
        return userSeveritiesRangeDocument;
    }

    protected Map<UserSeverity, PresidioRange<Double>> calculateUserSeverityRangeMap(double[] userScores) {

        if (ArrayUtils.isEmpty(userScores)) {
            return createEmptyMap();
        }

        Map<UserSeverity, PresidioRange<Double>> severityToScoreRangeMap = new LinkedHashMap<>();

        // sorting the scores
        Arrays.sort(userScores);
        ArrayUtils.reverse(userScores);

        // Get the severities in desc order
        List<UserSeverity> severitiesOrderedAsc = UserSeverity.getSeveritiesOrderedAsc();
        List<UserSeverity> severitiesOrderedDesc = new LinkedList<>(severitiesOrderedAsc);
        Collections.reverse(severitiesOrderedDesc);

        Integer rangeStartIndex = 0;

        // Go over all the severities from Critical to Low
        for (UserSeverity userSeverity : severitiesOrderedDesc) {

            if (userSeverity.equals(UserSeverity.LOW)) {
                severityToScoreRangeMap.put(UserSeverity.LOW, new PresidioRange<>(0d, userScores[rangeStartIndex]));
            } else {

                // Get the details
                UserSeverityComputeData userSeverityComputeData = severityToComputeDataMap.get(userSeverity);

                // Calculate the max users that can get the severity as percentage from the users
                int numberOfCalculatedFromPercent = (int) Math.floor(userSeverityComputeData.getPercentageOfUsers() * ((double) userScores.length) / 100);
                int rangeEndIndex = rangeStartIndex + numberOfCalculatedFromPercent;

                // If maxUsers set check that the amount calculated from percentage is not bigger that the users allowed
                if (userSeverityComputeData.getMaximumUsers() != null && numberOfCalculatedFromPercent > userSeverityComputeData.getMaximumUsers()) {
                    rangeEndIndex = (int) (rangeStartIndex + userSeverityComputeData.getMaximumUsers());
                }

                // Looking for the separation point between the severities
                for (int i = rangeEndIndex; i > rangeStartIndex; i--) {

                    // The delta between the scores is big enough to separate the severities
                    if (userScores[i] * userSeverityComputeData.getMinimumDeltaFactor() <= userScores[i - 1]) {
                        double minSeverityScore = userScores[i - 1];

                        // Set the severity boundaries
                        severityToScoreRangeMap.put(userSeverity, new PresidioRange<>(minSeverityScore, userScores[rangeStartIndex]));
                        rangeStartIndex = i;

                        break;
                    }
                }
            }
        }

        // Fix the mapping by going from low to critical and calculating the lower bound of each severity according to the
        // upper bound of the lower severity
        for (int i = 0; i < severitiesOrderedAsc.size() - 1; i++) {
            UserSeverity severity = severitiesOrderedAsc.get(i);
            UserSeverity higherSeverity = severitiesOrderedAsc.get(i + 1);

            Double upperBound = severityToScoreRangeMap.get(severity).getUpperBound();
            double minimumDelta = severityToComputeDataMap.get(higherSeverity).getMinimumDeltaFactor();

            PresidioRange<Double> higherSeverityRange = severityToScoreRangeMap.get(higherSeverity);

            // If no range set it
            if (higherSeverityRange == null) {
                severityToScoreRangeMap.put(higherSeverity, new PresidioRange<>(upperBound * minimumDelta, upperBound * minimumDelta));
            } else if (upperBound * minimumDelta < severityToScoreRangeMap.get(higherSeverity).getLowerBound()) {
                // Set the new lower and upper bound
                severityToScoreRangeMap.replace(higherSeverity, new PresidioRange<>(upperBound * minimumDelta, Math.max(higherSeverityRange.getUpperBound(), upperBound * minimumDelta)));
            }
        }

        return severityToScoreRangeMap;
    }

    private UserScoreToSeverity getExistingUserScoreToSeverity() {
        UserSeveritiesRangeDocument userSeveritiesRangeDocument = userSeveritiesRangeRepository.findOne(UserSeveritiesRangeDocument.USER_SEVERITIES_RANGE_DOC_ID);

        if (userSeveritiesRangeDocument == null) { //no existing percentiles were found
            logger.debug("No user score percentile calculation results were found, setting scores thresholds to zero (all users will get LOW severity (till next daily calculation)");

            Map<UserSeverity, PresidioRange<Double>> severityToScoreRangeMap = createEmptyMap();
            return new UserScoreToSeverity(severityToScoreRangeMap);
        }

        return new UserScoreToSeverity(userSeveritiesRangeDocument.getSeverityToScoreRangeMap());
    }

    private Map<UserSeverity, PresidioRange<Double>> createEmptyMap() {
        Map<UserSeverity, PresidioRange<Double>> severityToScoreRangeMap = new LinkedHashMap<>();
        severityToScoreRangeMap.put(UserSeverity.LOW, new PresidioRange<>(-1d, -1d));
        severityToScoreRangeMap.put(UserSeverity.MEDIUM, new PresidioRange<>(-1d, -1d));
        severityToScoreRangeMap.put(UserSeverity.HIGH, new PresidioRange<>(-1d, -1d));
        severityToScoreRangeMap.put(UserSeverity.CRITICAL, new PresidioRange<>(-1d, -1d));
        return severityToScoreRangeMap;
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
            updateUserSeveritiesAndProperties(severitiesMap, page.getContent(), true);
            page = getNextUserPage(userQueryBuilder, page);

        }
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

    private void updateUserSeveritiesAndProperties(UserSeverityServiceImpl.UserScoreToSeverity severitiesMap, List<User> users, boolean persistChanges) {
        List<User> updatedUsers = new ArrayList<>();
        if (users == null) {
            return;
        }

        users.forEach(user -> {
            boolean userInUpdatedUsers = false;
            User updatedUser;
            double userScore = user.getScore();
            UserSeverity newUserSeverity = severitiesMap.getUserSeverity(userScore);
            updatedUser = userPropertiesUpdateService.userPropertiesUpdate(user);
            logger.debug("Updating user severity for userId: " + user.getUserId());
            if (updatedUser != null) {
                user = updatedUser;
                updatedUsers.add(user);
                userInUpdatedUsers = true;
            }
            if (!newUserSeverity.equals(user.getSeverity())) {
                user.setSeverity(newUserSeverity);
                if (!userInUpdatedUsers) {
                    updatedUsers.add(user);
                }
            }
        });
        if (updatedUsers.size() > 0 && persistChanges) {
            userPersistencyService.save(updatedUsers);
        }
    }

    public static class UserScoreToSeverity {
        private Map<UserSeverity, PresidioRange<Double>> userSeverityRangeMap;

        public UserScoreToSeverity(Map<UserSeverity, PresidioRange<Double>> severityToScoreRangeMap) {
            this.userSeverityRangeMap = severityToScoreRangeMap;
        }

        public UserSeverity getUserSeverity(double score) {
            if (userSeverityRangeMap.get(UserSeverity.LOW).getUpperBound() == -1) {
                return UserSeverity.LOW;
            }

            if (score >= userSeverityRangeMap.get(UserSeverity.CRITICAL).getLowerBound()) {
                return UserSeverity.CRITICAL;
            } else if (score >= userSeverityRangeMap.get(UserSeverity.HIGH).getLowerBound()) {
                return UserSeverity.HIGH;
            } else if (score >= userSeverityRangeMap.get(UserSeverity.MEDIUM).getLowerBound()) {
                return UserSeverity.MEDIUM;
            } else {
                return UserSeverity.LOW;
            }
        }
    }

    @Override
    public List<String> collectionNamesByOrderForEvents() {
        return userPropertiesUpdateService.collectionNamesByOrderForEvents();
    }
}
