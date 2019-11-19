package com.rsa.netwitness.presidio.automation.test.rest;

import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.domain.output.EntitiesStoredRecord;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import org.json.JSONObject;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;


public class RestEntityTests extends AbstractTestNGSpringContextTests {

    private RestHelper restHelper = new RestHelper();
    private String testName;
    private List<EntitiesStoredRecord> allEntities;
    private PresidioUrl allEntitiesUrl;

    @BeforeClass
    public void preconditionCheck() {
        PresidioUrl url = restHelper.entities().url().withNoParameters();
        List<EntitiesStoredRecord> entities = restHelper.entities().request().getEntities(url);
        assertThat(entities).as(url+ "Entities list is empty. Skipping next tests.").isNotNull().isNotEmpty();

        url = restHelper.entities().url().withExpandedParameter();
        entities = restHelper.entities().request().getEntities(url);
        assertThat(entities).as(url+ "Entities list is empty. Skipping next tests.").isNotNull().isNotEmpty();

        allEntitiesUrl = restHelper.entities().url().withMaxSizeAndExpendedParameters();
        allEntities = restHelper.entities().request().getEntities(allEntitiesUrl);
        assertThat(allEntities).as(allEntitiesUrl+ "Entities list is empty. Skipping next tests.").isNotNull().isNotEmpty();
    }

    @BeforeMethod
    public void nameBefore(Method method) {
        testName = method.getName();
        System.out.println("Start running test: " + testName);
    }

    @Test
    public void entities_should_be_sorted_by_score_desc() {
        PresidioUrl url = restHelper.entities().url().withMaxSizeAndSortedParameters("DESC", "SCORE");
        List<EntitiesStoredRecord> entities = restHelper.entities().request().getEntities(url);

        for(int i=0 ; i<entities.size()-1 ; i++) {
            int current = Integer.parseInt(entities.get(i).getScore());
            int next = Integer.parseInt(entities.get(i+1).getScore());

            if(current < next) {
                Assert.fail(url + "\nScores are not sorted correctly. \ncurrent score is " + current + ", next score is " + next + "\nentity: " + entities.get(i).toString() );
            }
        }
    }

    @Test
    public void entities_should_be_sorted_by_score_asc() {
        PresidioUrl url = restHelper.entities().url().withMaxSizeAndSortedParameters("ASC", "SCORE");
        List<EntitiesStoredRecord> entities = restHelper.entities().request().getEntities(url);

        for(int i=0 ; i < entities.size()-1 ; i++) {
            int current = Integer.parseInt(entities.get(i).getScore());
            int next = Integer.parseInt(entities.get(i+1).getScore());

            if(current > next) {
                Assert.fail(url + "\nScores are not sorted correctly. \ncurrent score is " + current + ", next score is " + next + "\nentity: " + entities.get(i).toString() );
            }
        }
    }

    @Test
    public void expand_argument_should_return_non_empty_alerts_array() {
        PresidioUrl url = restHelper.entities().url().withMinScoreAndExpanded(50);
        List<EntitiesStoredRecord> entities = restHelper.entities().request().getEntities(url);
        assertThat(entities.stream().mapToInt(e -> e.getAlerts().size()).min().getAsInt()).as(url + "\nAlerts list did not appears with the Expand=true flag.").isGreaterThan(0);
    }

    @Test
    public void get_entity_by_entity_id_correctness_test() {
        EntitiesStoredRecord entitiesStoredRecord = allEntities.get(ThreadLocalRandom.current().nextInt(allEntities.size()));

        String entityId = entitiesStoredRecord.getId();
        PresidioUrl entityIdUrl = restHelper.entities().withId(entityId).url().withNoParameters();
        List<EntitiesStoredRecord> entities = restHelper.entities().request().getEntities(entityIdUrl);

        assertThat(entities).as("Get entities by id should return 1 element").hasSize(1);

        for(EntitiesStoredRecord e : entities) {
            Assert.assertEquals(e.getId(), entityId, entityIdUrl + "\nFilter by id return incorrect entity.");
            Assert.assertEquals(e.getEntityName(), entityId, entitiesStoredRecord.getEntityName() + "\nFilter by id return incorrect entity.");
            Assert.assertEquals(e.getEntityType(), entityId, entitiesStoredRecord.getEntityType() + "\nFilter by id return incorrect entity.");
        }
    }


    @Test
    public void alerts_count_correctness_test() {
        for(EntitiesStoredRecord entity : allEntities) {
            int alertCount = entity.getAlertCount();
            int alertSize = entity.getAlerts().size();

            Assert.assertEquals(alertCount, alertSize, allEntitiesUrl + "\nThe alertCount and the size of the alert list are not equals for entityId " + entity.getId());
        }
    }

    @Test
    public void aggregate_by_severity_without_min_score_correctness_test() {
        PresidioUrl entitiesUrl = restHelper.entities().url().withMaxSizeAndSortedAndAggregated("ASC", "SCORE", "SEVERITY");
        PresidioUrl aggregationDataUrl = restHelper.entities().url().withMaxSizeAndAggregated("SEVERITY");
        verifySeverityAggregation(entitiesUrl, aggregationDataUrl, 0);
    }

    @Test
    public void aggregate_by_severity_with_min_score_correctness_test() {
        int secondMinScore = getDistinctScoresOrdered().get(1);
        PresidioUrl entitiesUrl = restHelper.entities().url().withMaxSizeAndSortedAndAggregatedAndMinScore("ASC", "SCORE", "SEVERITY", secondMinScore);
        PresidioUrl aggregationDataUrl = restHelper.entities().url().withMaxSizeAndAggregatedAndMinScore("SEVERITY", secondMinScore);
        verifySeverityAggregation(entitiesUrl, aggregationDataUrl, secondMinScore);
    }

    private void verifySeverityAggregation(PresidioUrl entitiesUrl, PresidioUrl aggregationDataUrl, int minScore) {

        List<EntitiesStoredRecord> entities = restHelper.entities().request().getEntities(entitiesUrl);

        int low = 0, medium = 0, high = 0, critical = 0;

        for(EntitiesStoredRecord usr : entities) {
            if(usr.getSeverity().equals("LOW")) low++;
            if(usr.getSeverity().equals("MEDIUM")) medium++;
            if(usr.getSeverity().equals("HIGH")) high++;
            if(usr.getSeverity().equals("CRITICAL")) critical++;
        }

        int actualMinScore = entities.stream().mapToInt(e -> Integer.valueOf(e.getScore())).min().orElse(-1);
        assertThat(actualMinScore).as(entitiesUrl + "Min score filter doesn't work properly.").isGreaterThanOrEqualTo(minScore);

        JSONObject agg = restHelper.entities().request().getRestApiResponseAsJsonObj(aggregationDataUrl);
        JSONObject severity = agg.getJSONObject("aggregationData").getJSONObject("SEVERITY");
        Assert.assertNotNull(severity, "severity keys are null");

        Iterator<String> keysItr = severity.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = severity.get(key);

            if(key.equals("LOW")){
                Assert.assertEquals(low, (int) value, "low severity count is not match to actual entities severity");
            }
            else if(key.equals("MEDIUM")){
                Assert.assertEquals(medium, (int) value, "medium severity count is not match to actual entities severity");
            }
            else if(key.equals("HIGH")){
                Assert.assertEquals(high, (int) value, "high severity count is not match to actual entities severity");
            }
            else if(key.equals("CRITICAL")){
                Assert.assertEquals(critical, (int) value, "critical severity count is not match to actual entities severity");
            }
        }

    }

    @Test
    public void aggregated_by_indicators_correctness_test() {
        PresidioUrl entitiesUrl = restHelper.entities().url().withAggregatedFieldParameter("INDICATORS");
        JSONObject agg = restHelper.entities().request().getRestApiResponseAsJsonObj(entitiesUrl);
        JSONObject indicatorsAggregation = agg.getJSONObject("aggregationData").getJSONObject("INDICATORS");
        Assert.assertNotNull(indicatorsAggregation, "indicator's aggregation keys are null");

        List<EntitiesStoredRecord> entities = null;

        Iterator<String> keyItr = indicatorsAggregation.keys();
        while(keyItr.hasNext()) {
            String key = keyItr.next();
            Object value = indicatorsAggregation.get(key);
            int aggregated = (int)value;
            PresidioUrl indicatorUrl = restHelper.entities().url().withMaxSizeAndIndicatorNameParameters(key);
            entities = restHelper.entities().request().getEntities(indicatorUrl);

            assertThat(entities.size())
                    .as("Indicator '" + key + "' aggregateBy count is not matched to 'indicatorName=" + key + " request.\n" + entitiesUrl + "\n" + indicatorUrl)
                    .isEqualTo(aggregated);
        }
    }

    @Test
    public void max_min_score_range_filter_correctness_test() {
        assertThat(getDistinctScoresOrdered()).as("Expected for more distinct entity scores.\nScores=" + getDistinctScoresOrdered().toString()).hasSizeGreaterThan(3);

        int minScore = getDistinctScoresOrdered().get(1);
        int maxScore = getDistinctScoresOrdered().get(getDistinctScoresOrdered().size()-2);
        PresidioUrl url = restHelper.entities().url().withMaxSizeAndSortedAscAndMinMaxScoreParameters(minScore, maxScore);
        List<EntitiesStoredRecord> entities = restHelper.entities().request().getEntities(url);

        Assert.assertEquals(Integer.parseInt(entities.get(0).getScore()), minScore, "minScore is not the minScore that supposed to be.");
        Assert.assertEquals(Integer.parseInt(entities.get(entities.size()-1).getScore()), maxScore, "maxScore is not the maxScore that supposed to be.");
    }


    @Test
    public void search_by_entity_should_be_case_insensitive() {
        EntitiesStoredRecord testEntity = allEntities.get(ThreadLocalRandom.current().nextInt(allEntities.size()));

        String entityName = testEntity.getEntityName().toUpperCase();
        PresidioUrl url = restHelper.entities().url().entitiesWithEntityNameAndMaxSizeParameters(entityName);
        List<EntitiesStoredRecord> entities = restHelper.entities().request().getEntities(url);

        assertThat(entities.stream().map(EntitiesStoredRecord::getId)).as(url + "Looking for entity in upper case: " + entityName).contains(testEntity.getId());
    }

    @Test
    public void search_by_second_word_finds_the_entity(){
        EntitiesStoredRecord doubleSlashName = allEntities.stream().filter(e -> e.getEntityName().contains("\\")).findAny().orElseThrow();
        String nameToSearch = doubleSlashName.getEntityName().split("\\\\")[1];

        PresidioUrl url = restHelper.entities().url().entitiesWithEntityNameAndMaxSizeParameters(nameToSearch);
        List<EntitiesStoredRecord> entities = restHelper.entities().request().getEntities(url);
        assertThat(entities.stream().map(EntitiesStoredRecord::getId))
                .as(url + "\nLooking for partially entity string: " + nameToSearch + "\nFull name: " + doubleSlashName.getEntityName())
                .contains(doubleSlashName.getId());

        List<EntitiesStoredRecord> multiWordEntities = allEntities.stream()
                .filter(e -> e.getEntityName().trim().contains(" "))
                .collect(toList());

        EntitiesStoredRecord entityToSearch = multiWordEntities.get(ThreadLocalRandom.current().nextInt(multiWordEntities.size()));
        nameToSearch = entityToSearch.getEntityName().split("\\s")[1];

        url = restHelper.entities().url().entitiesWithEntityNameAndMaxSizeParameters(nameToSearch);
        entities = restHelper.entities().request().getEntities(url);
        assertThat(entities.stream().map(EntitiesStoredRecord::getId))
                .as(url + "\nLooking for partially entity string: " + nameToSearch + "\nFull name: " + entityToSearch.getEntityName())
                .contains(entityToSearch.getId());
    }


    @Test
    public void alert_document_entity_id_should_match_related_entity_id() {
        List<EntitiesStoredRecord> alertEntities = allEntities.stream().filter(e -> !e.getAlerts().isEmpty()).collect(toList());

        for(EntitiesStoredRecord e : alertEntities) {
            String entityId = e.getId();
            List<AlertsStoredRecord> alerts = e.getAlerts();
            for(AlertsStoredRecord alert : alerts) {
                assertThat(entityId)
                        .as( allEntitiesUrl + "\nalert's entityId is not matched to the it's entity Id.\n" +
                                "EntityId = " + entityId + "\n" +
                                "AlertId = " + alert.getId() + "\n")
                        .isEqualTo(alert.getEntityDocumentId());
            }
        }

    }


    private List<Integer> getDistinctScoresOrdered(){
        List<Integer> orderedScores = allEntities.stream()
                .mapToInt(e -> Integer.valueOf(e.getScore()))
                .distinct()
                .sorted()
                .boxed()
                .collect(toList());

        assertThat(orderedScores).as(allEntitiesUrl + "\nAll entities have the same score.").isNotNull().hasSizeGreaterThan(1);
        return orderedScores;
    }
}
