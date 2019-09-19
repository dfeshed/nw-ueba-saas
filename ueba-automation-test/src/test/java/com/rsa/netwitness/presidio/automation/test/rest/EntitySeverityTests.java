package com.rsa.netwitness.presidio.automation.test.rest;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.domain.output.EntitiesStoredRecord;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import com.rsa.netwitness.presidio.automation.test_managers.OutputTestManager;
import org.assertj.core.api.SoftAssertions;
import org.json.JSONException;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, OutputTestManager.class})
public class EntitySeverityTests extends AbstractTestNGSpringContextTests {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(EntitySeverityTests.class);

    /**
     * https://wiki.na.rsa.net/display/PRES/User+Severity
     **/

    private SoftAssertions softly = new SoftAssertions();
    private RestHelper restHelper = new RestHelper();

    private ImmutableList<EntitiesStoredRecord> allActualEntitiesSortedByScore;
    private PresidioUrl allEntitiesUrl;
    private ImmutableList<String> entityTypes = ImmutableList.of("sslSubject", "ja3", "userId");


    @BeforeClass
    public void prepareData() throws JSONException {
        allEntitiesUrl = restHelper.entities().url().withMaxSizeAndSortedParameters("ASC", "SCORE");
        allActualEntitiesSortedByScore = ImmutableList.copyOf(restHelper.entities().request().getEntities(allEntitiesUrl));
        assertThat(allActualEntitiesSortedByScore.stream().map(e -> Integer.valueOf(e.getScore()))).isSorted();
    }


    @Test
    public void maximum_amount_of_entities_should_not_exceed_percentage_or_static_limit() {
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

                    softly.assertThat(topCategoryLowestScore)
                            .as(allEntitiesUrl + "\nentityType=" + entityType +
                                    "\nExpected score distance is exceeded between severities: [" + severities.get(index) +
                                    " and " + severities.get(index + 1) + "]" + "\nSeverity distance=" + distance +
                                    "\nExpected: higherCategoryLowestScore > lowerCategoryHighestScore*distance" +
                                    "\nCompared entities:" +
                                    topCategoryEntity.toString() + "\n" + bottomCategoryEntity.toString())
                            .isGreaterThan(expectedDistanceLimit);
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
                .collect(Collectors.toList());

        assertThat(criticalWithScoreLess15)
                .as(allEntitiesUrl + "\nThe following entities have a critical severity with score less than 15:\n" +
                        criticalWithScoreLess15.stream().map(EntitiesStoredRecord::toString).collect(Collectors.joining("\n")))
                .isEmpty();
    }


    @Test
    public void total_entity_score_equal_to_sum_of_related_alerts_severity_scores() {
        RestHelper restHelper = new RestHelper();
        PresidioUrl url = restHelper.entities().url().withMaxSizeAndSortedAndExpendedParameters("DESC", "SCORE");
        List<EntitiesStoredRecord> entities = restHelper.entities().request().getEntities(url);

        assertThat(entities)
                .withFailMessage(url + "\nEntities list is empty.")
                .isNotNull()
                .isNotEmpty();

        int sumScoreSeverity = 0;

        for (EntitiesStoredRecord entity : entities) {
            List<AlertsStoredRecord> alerts = entity.getAlerts();
            if (alerts.size() > 0) {
                for (AlertsStoredRecord alert : alerts) {
                    sumScoreSeverity += getSeverityScore(alert.getSeverity());
                }
            }

            Assert.assertEquals(Integer.parseInt(entity.getScore()), sumScoreSeverity, url + "\n");
            sumScoreSeverity = 0;
        }
    }


    private int getSeverityScore(String name) {
        HashMap<String, Integer> severityScoreMap = new HashMap<>();
        severityScoreMap.put("CRITICAL", 20);
        severityScoreMap.put("HIGH", 15);
        severityScoreMap.put("MEDIUM", 10);
        severityScoreMap.put("LOW", 1);

        return severityScoreMap.get(name);
    }

}
