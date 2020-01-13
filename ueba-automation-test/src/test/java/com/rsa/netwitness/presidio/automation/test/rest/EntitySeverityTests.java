package com.rsa.netwitness.presidio.automation.test.rest;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.domain.output.EntitiesStoredRecord;
import com.rsa.netwitness.presidio.automation.jdbc.AirflowTasksPostgres;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import org.assertj.core.api.Fail;
import org.assertj.core.api.SoftAssertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.assertThat;

public class EntitySeverityTests extends AbstractTestNGSpringContextTests {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(EntitySeverityTests.class);

    /**
     * https://wiki.na.rsa.net/display/PRES/User+Severity
     **/

    private RestHelper restHelper = new RestHelper();

    private ImmutableList<EntitiesStoredRecord> allActualEntitiesSortedByScore;
    private PresidioUrl allEntitiesUrl;
    private ImmutableList<String> entityTypes = ImmutableList.of("sslSubject", "ja3", "userId");


    @BeforeClass
    public void prepareData() throws JSONException {
        allEntitiesUrl = restHelper.entities().url().withMaxSizeAndSortedAndExpendedParameters("ASC", "SCORE");
        allActualEntitiesSortedByScore = ImmutableList.copyOf(restHelper.entities().request().getEntities(allEntitiesUrl));
        assertThat(allActualEntitiesSortedByScore).isNotNull().isNotEmpty();
        assertThat(allActualEntitiesSortedByScore.stream().map(e -> Integer.valueOf(e.getScore()))).isSorted();
    }

    @Test
    public void trending_score_equals_sum_of_entity_score_contributions() {
        /**
         * The hourly trends calculates the last 24 hours / last 7 days includes the current hour.
         * For example, if the output run for:
         * 	2020-01-07T09:00:00Z - 2020-01-07T10:00:00Z
         * The hourly trends include alerts in this period: 2020-01-06T10:00:00Z - 2020-01-07T10:00:00Z (alert start_time >= 01/06 10:00 and alert end_time <= 01/07 10:00)
         * The weekly trends include alerts in this period: 2019-12-31T10:00:00Z - 2020-01-07T10:00:00Z (alert start_time >= 31/12 10:00 and alert end_time <= 01/07 10:00)
         */

        SoftAssertions softly = new SoftAssertions();

        Instant lastExecutionDateUser = fetchLastExecutionDateOfOutputJob("userId_hourly_ueba_flow", "hourly_output_processor");
        Instant lastExecutionDateSslSubject = fetchLastExecutionDateOfOutputJob("sslSubject_hourly_ueba_flow", "hourly_output_processor");
        Instant lastExecutionDateJa3 = fetchLastExecutionDateOfOutputJob("ja3_hourly_ueba_flow", "hourly_output_processor");

        ImmutableMap<String, Instant> lastExecutionDates = new ImmutableMap.Builder<String, Instant>()
                .put("sslSubject", lastExecutionDateSslSubject)
                .put("ja3", lastExecutionDateJa3)
                .put("userId", lastExecutionDateUser)
                .build();


        List<EntitiesStoredRecord> entitiesWithoutAlerts = allActualEntitiesSortedByScore.parallelStream()
                .filter(e -> e.getAlerts().isEmpty())
                .collect(toList());

        assertThat(allActualEntitiesSortedByScore)
                .as(allEntitiesUrl + "\nFound entity REST without trending values.")
                .extracting(EntitiesStoredRecord::getTrendingScore)
                .doesNotContainNull()
                .isNotNull()
                .isNotEmpty();

        assertThat(entitiesWithoutAlerts)
                .as(allEntitiesUrl + "\nTrending values for entities without alerts must be 0.")
                .flatExtracting(e -> e.getTrendingScore().values())
                .doesNotContainNull()
                .containsOnly(0);


        List<EntitiesStoredRecord> entitiesWithAlerts = allActualEntitiesSortedByScore.parallelStream()
                .filter(e -> !e.getAlerts().isEmpty())
                .collect(toList());

        for (EntitiesStoredRecord entity : entitiesWithAlerts) {
            int dailyTrend = entity.getTrendingScore().get("daily");
            int weeklyTrend = entity.getTrendingScore().get("weekly");

            String entityType = entity.getEntityType();
            assertThat(lastExecutionDates).as(allEntitiesUrl + "\nEntity type doesn't match. EntityId=" + entity.getId()).containsKey(entityType);
            Instant lastExecutionDateOfOutput = lastExecutionDates.get(entityType);

            int dailySumOfScoreContributions = entity.getAlerts().parallelStream()
                    .filter(alert -> alert.getStartDate().isAfter(lastExecutionDateOfOutput.minus(1, DAYS)))
                    .mapToInt(e -> Integer.valueOf(e.getEntityScoreContribution()))
                    .sum();

            int weeklySumOfScoreContributions = entity.getAlerts().parallelStream()
                    .filter(alert -> alert.getStartDate().isAfter(lastExecutionDateOfOutput.minus(7, DAYS)))
                    .mapToInt(e -> Integer.valueOf(e.getEntityScoreContribution()))
                    .sum();

            softly.assertThat(dailyTrend)
                    .as(allEntitiesUrl + "\nDaily trending value result mismatch for entityId: " + entity.getId()
                            + " entityType=" + entity.getEntityType()
                            + "\nAlerts:\n" + entity.getAlerts().stream().map(e -> "[" + e.getId()
                            + ", entityType=" + e.getEntityType()
                            + ", StartDate=" + e.getStartDate() + ", EndDate=" + e.getEndDate() + ", EntityScoreContribution="
                            + e.getEntityScoreContribution() + "]").collect(joining(",\n"))
                            + "\nlastExecutionDateOfOutput=" + lastExecutionDateOfOutput
                            + "\nstartDate should be after " + lastExecutionDateOfOutput.minus(1, DAYS))
                    .isEqualTo(dailySumOfScoreContributions);

            softly.assertThat(weeklyTrend)
                    .as(allEntitiesUrl + "\nWeekly trending value result mismatch for entityId: " + entity.getId()
                            + "\nAlerts:\n" + entity.getAlerts().stream().map(e -> "[" + e.getId()
                            + ", entityType=" + e.getEntityType()
                            + ", StartDate=" + e.getStartDate() + ", EndDate=" + e.getEndDate() + ", EntityScoreContribution="
                            + e.getEntityScoreContribution() + "]").collect(joining(",\n"))
                            + "\nlastExecutionDateOfOutput=" + lastExecutionDateOfOutput
                            + "\nstartDate should be after " + lastExecutionDateOfOutput.minus(7, DAYS))
                    .isEqualTo(weeklySumOfScoreContributions);
        }

        softly.assertAll();
    }

    private Instant fetchLastExecutionDateOfOutputJob(String dagId, String taskId) {
        return new AirflowTasksPostgres()
                .fetchTaskDetails(dagId, taskId, Instant.now().minus(3, DAYS))
                .stream().filter(e -> e.state.equals("success"))
                .map(e -> e.executionDate)
                .max(Instant::compareTo)                // last execution of output for E2E automation
                .orElse(Instant.now().truncatedTo(DAYS).minus(3, DAYS));  // for core automation
    }


    @Test
    public void maximum_amount_of_entities_should_not_exceed_percentage_or_static_limit() {
        SoftAssertions softly = new SoftAssertions();

        ImmutableMap<String, Integer> expectedPercentageLimitBySeverity = new ImmutableMap.Builder<String, Integer>()
                .put("CRITICAL", 1).put("HIGH", 4).put("MEDIUM", 10).build();

        ImmutableMap<String, Integer> expectedStaticLimitBySeverity = new ImmutableMap.Builder<String, Integer>()
                .put("CRITICAL", 5).put("HIGH", 10).build();

        for (String entityType : entityTypes) {
            long totalEntities = allActualEntitiesSortedByScore.parallelStream()
                    .filter(entity -> entity.getEntityType().equals(entityType))
                    .count();

            Map<String, Long> entitiesCountBySeverity = allActualEntitiesSortedByScore.parallelStream()
                    .filter(entity -> entity.getEntityType().equals(entityType))
                    .collect(Collectors.groupingBy(EntitiesStoredRecord::getSeverity, counting()));

            for (String severity : expectedPercentageLimitBySeverity.keySet()) {
                int expectedEntitiesPercentageLimit = (int) (((double) expectedPercentageLimitBySeverity.get(severity) / 100) * totalEntities);
                int expectedEntitiesLimit = Math.min(expectedEntitiesPercentageLimit, expectedStaticLimitBySeverity.getOrDefault(severity, Integer.MAX_VALUE));

                softly.assertThat(entitiesCountBySeverity.getOrDefault(severity, -1L))
                        .as(allEntitiesUrl + "\nThere are too many " + severity + " severity " + entityType + " entities.")
                        .isLessThanOrEqualTo(expectedEntitiesLimit);
            }
        }
        softly.assertAll();
    }


    @Test
    public void score_distance_between_categories_should_not_exceed_static_limit() {
        SoftAssertions softly = new SoftAssertions();

        ImmutableList<String> severities = ImmutableList.of("CRITICAL", "HIGH", "MEDIUM", "LOW");

        ImmutableMap<String, Double> expectedScoreDistance = new ImmutableMap.Builder<String, Double>()
                .put("CRITICAL", 1.5).put("HIGH", 1.3).put("MEDIUM", 1.1).put("LOW", 0.0).build();


        for (String entityType : entityTypes) {

            Map<String, List<EntitiesStoredRecord>> entitiesBySeverity = allActualEntitiesSortedByScore.stream().sequential()
                    .filter(entity -> entity.getEntityType().equals(entityType))
                    .collect(Collectors.groupingBy(EntitiesStoredRecord::getSeverity));

            Function<String, Optional<EntitiesStoredRecord>> lowestScoreEntityBySeverity = severity ->
                    entitiesBySeverity.get(severity).parallelStream().min(EntitiesStoredRecord::compareScore);

            Function<String, Optional<EntitiesStoredRecord>> highestScoreEntityBySeverity = severity ->
                    entitiesBySeverity.get(severity).parallelStream().max(EntitiesStoredRecord::compareScore);

            for (int index = 0; index < severities.size() - 1; index++) {
                if (!entitiesBySeverity.containsKey(severities.get(index)) || !entitiesBySeverity.containsKey(severities.get(index + 1))) {
                    LOGGER.warn(allEntitiesUrl + "\nentityType=" + entityType +
                            "\nOne of severities is missing [" + severities.get(index) + " or " + severities.get(index + 1) + "]");
                } else {
                    Optional<EntitiesStoredRecord> topCategoryEntity = lowestScoreEntityBySeverity.apply(severities.get(index));
                    Optional<EntitiesStoredRecord> bottomCategoryEntity = highestScoreEntityBySeverity.apply(severities.get(index + 1));
                    assertThat(topCategoryEntity).isPresent();
                    assertThat(bottomCategoryEntity).isPresent();

                    int topCategoryLowestScore = Integer.valueOf(topCategoryEntity.get().getScore());
                    int bottomCategoryHighestScore = Integer.valueOf(bottomCategoryEntity.get().getScore());
                    double distance = expectedScoreDistance.get(severities.get(index));
                    int expectedDistanceLimit = (int) (bottomCategoryHighestScore * distance);
                    Function<EntitiesStoredRecord, String> printOut = e -> "[severity = " + e.getSeverity() + ", id=" + e.getId() + ", score = " + e.getScore() + "]";

                    softly.assertThat(topCategoryLowestScore)
                            .as(allEntitiesUrl + "\nentityType=" + entityType +
                                    "\nExpected score distance is exceeded between severities: [" + severities.get(index) + " and " + severities.get(index + 1) + "]" +
                                    "\nExpected distance between the severities = " + distance +
                                    "\nCalculation rule: higherCategoryLowestScore > lowerCategoryHighestScore * distance" +
                                    "\nCompared entities:\n" + printOut.apply(topCategoryEntity.get())  +
                                    "\n" + printOut.apply(bottomCategoryEntity.get()))
                            .isGreaterThanOrEqualTo(expectedDistanceLimit);
                }
            }
        }

        softly.assertAll();
    }


    @Test
    public void critical_severity_score_is_more_then_15() {

        List<EntitiesStoredRecord> criticalWithScoreLess15 = allActualEntitiesSortedByScore.parallelStream()
                .filter(e -> e.getSeverity().equals("CRITICAL"))
                .filter(e -> Objects.nonNull(e.getScore()))
                .filter(e -> Integer.valueOf(e.getScore()) <= 15)
                .collect(toList());

        assertThat(criticalWithScoreLess15)
                .as(allEntitiesUrl + "\nThe following entities have a critical severity with score less than 15:\n" +
                        criticalWithScoreLess15.stream().map(EntitiesStoredRecord::toString).collect(Collectors.joining("\n")))
                .isEmpty();
    }


    @Test
    public void total_entity_score_equal_to_sum_of_related_alerts_severity_scores() {
        SoftAssertions softly = new SoftAssertions();

        ImmutableMap<String, Integer> severityScoreMap = new ImmutableMap.Builder<String, Integer>()
                .put("CRITICAL", 20).put("HIGH", 15).put("MEDIUM", 10).put("LOW", 1).build();

        PresidioUrl url = restHelper.entities().url().withMaxSizeAndSortedAndExpendedParameters("DESC", "SCORE");
        ImmutableList<EntitiesStoredRecord> actualEntities = ImmutableList.copyOf(restHelper.entities().request().getEntities(url));

        int sumScoreSeverity = 0;

        for (EntitiesStoredRecord entity : actualEntities) {
            List<AlertsStoredRecord> alerts = entity.getAlerts();
            if (alerts.size() > 0) {
                for (AlertsStoredRecord alert : alerts) {
                    sumScoreSeverity += severityScoreMap.get(alert.getSeverity());
                }
            }

            softly.assertThat(Integer.parseInt(entity.getScore()))
                    .as(url + "\nEntity score not equal to sum of alert scores.\nEntity: " + entity)
                    .isEqualTo(sumScoreSeverity);
            sumScoreSeverity = 0;
        }

        softly.assertAll();
    }

    @Test
    public void aggregation_data_severity_counters_match_entities_array() {
        PresidioUrl url = restHelper.entities().url().withAggregatedFieldParameter("SEVERITY");

        try {
            JSONObject json = restHelper.alerts().request().getRestApiResponseAsJsonObj(url)
                    .getJSONObject("aggregationData")
                    .getJSONObject("SEVERITY");

            assertThat(json).isNotNull();

            Type type = new TypeToken<Map<String, Long>>() {}.getType();
            Map<String, Long> aggregationDataSeverities = new Gson().fromJson(json.toString(), type);

            Map<String, Long> entitiesCountBySeverity = allActualEntitiesSortedByScore.parallelStream()
                    .collect(Collectors.groupingBy(EntitiesStoredRecord::getSeverity, counting()));

            assertThat(aggregationDataSeverities).as(url + "\n'aggregationData' severity counters mismatch").isEqualTo(entitiesCountBySeverity);

        } catch (Exception e) {
            LOGGER.error(url.toString());
            LOGGER.error("Unable to parse severity");
            Fail.fail(e.getMessage());
        }
    }


    @Test
    public void total_counter_matches_entities_array() {
        PresidioUrl url = restHelper.entities().url().withSortedParameters("DESC", "SCORE");

        try {
            JSONObject response = restHelper.alerts().request().getRestApiResponseAsJsonObj(url);
            assertThat(response.has("total")).as(url + "'total' key is missing from\n" + response).isTrue();

            int total = response.getInt("total");
            assertThat(total)
                    .as(url + "\n'total' counter result mismatch")
                    .isEqualTo(allActualEntitiesSortedByScore.size());

        } catch (Exception e) {
            LOGGER.error(url.toString());
            LOGGER.error("Unable to parse");
            Fail.fail(e.getMessage());
        }
    }


}
