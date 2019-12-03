package com.rsa.netwitness.presidio.automation.test.rest;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.domain.output.EntitiesStoredRecord;
import com.rsa.netwitness.presidio.automation.rest.client.RestApiResponse;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Integer.valueOf;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class AlertsUpdateFeedbackTest extends AbstractTestNGSpringContextTests {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(AlertsUpdateFeedbackTest.class);

    private final int MINIMUM_REQUARED_ENTITIES = 4;
    private final long MINIMUM_NON_ZERO_SCORE_ALERTS = 4;

    private EntitiesStoredRecord someAlertsToIgnoreEntity, allAlertsToIgnoreEntity, unIgnoreEntity;
    private RestHelper restHelper = new RestHelper();
    private PresidioUrl allEntitiesUrl;


    @BeforeClass
    public void prepareData() {
        allEntitiesUrl = restHelper.entities().url().withSortedAndExpandedParameters("DESC", "SCORE");

        List<EntitiesStoredRecord> selectedEntities = restHelper.entities().request().getEntities(allEntitiesUrl)
                .stream()
                .filter(byAlertsWithoutAnyTag)
                .filter(byMinimumNonZeroScoreContributionAlerts)
                .collect(toList());

        assertThat(selectedEntities)
                .as(allEntitiesUrl + "\nMinimum amount of entities meet the test requirements - not found.")
                .hasSizeGreaterThanOrEqualTo(MINIMUM_REQUARED_ENTITIES);

        unIgnoreEntity = selectedEntities.get(0);
        someAlertsToIgnoreEntity = selectedEntities.get(1);
        allAlertsToIgnoreEntity = selectedEntities.get(2);
    }

    @Test
    public void ignore_part_of_alerts_validate_entity_score() {
        final int totalAlertsToIgnore = 2;

        // ignore random alerts
        List<AlertsStoredRecord> allAlertsWithScoreContribution = getAllNonZeroScoreContributionAlerts(totalAlertsToIgnore, someAlertsToIgnoreEntity);
        List<AlertsStoredRecord> alertsToIgnore = selectAlertsToIgnore(totalAlertsToIgnore, allAlertsWithScoreContribution);
        int sumOfIgnoredEntityScoreContributions = alertsToIgnore.stream().mapToInt(alert -> valueOf(alert.getEntityScoreContribution())).sum();
        assertThat(sumOfIgnoredEntityScoreContributions).as("sumOfIgnoredEntityScoreContributions == 0").isGreaterThan(0);

        sendNotA_Risk(alertsToIgnore);

        PresidioUrl actualEntityUrl = restHelper.entities().withId(someAlertsToIgnoreEntity.getId()).url().withExpandedParameter();
        EntitiesStoredRecord actualEntity = restHelper.entities().request().getEntities(actualEntityUrl).get(0);
        List<AlertsStoredRecord> actualNotARiskAlerts = actualEntity.getAlerts().stream().filter(alert -> alert.getFeedback().equals("NOT_RISK")).collect(toList());

        assertThat(getIds(actualNotARiskAlerts))
                .as(actualEntityUrl + "\n'NOT_RISK' tag is missing from some alerts.")
                .containsExactlyInAnyOrderElementsOf(getIds(alertsToIgnore));

        assertThat(actualNotARiskAlerts)
                .extracting(e -> valueOf(e.getEntityScoreContribution()))
                .as(actualEntityUrl + "\nExpected zero EntityScoreContributions for these alerts: " +
                        "\nAlertIds: [ \"" + alertsToIgnore.stream().map(AlertsStoredRecord::getId).collect(Collectors.joining("\", \"")) + "\" ]")
                .containsOnly(0);

        int expectedEntityScore = valueOf(someAlertsToIgnoreEntity.getScore()) - sumOfIgnoredEntityScoreContributions;
        assertThat(valueOf(actualEntity.getScore()))
                .as(actualEntityUrl + "\nActual and calculated EntityScoreContributions mismatch." +
                        "\nIgnored AlertIds: [ \"" + alertsToIgnore.stream().map(AlertsStoredRecord::getId).collect(Collectors.joining("\", \"")) + "\" ]" +
                        "\nsumOfIgnoredEntityScoreContributions=" + sumOfIgnoredEntityScoreContributions)
                .isEqualTo(expectedEntityScore);
    }




    @Test
    public void ignore_all_alerts_validate_entity_score() {
        LOGGER.info("All related alerts will be marked as 'not a risk': EntityId=" + allAlertsToIgnoreEntity.getId());
        int ignoredEntityScoreContribution = valueOf(allAlertsToIgnoreEntity.getScore());
        assertThat(ignoredEntityScoreContribution).as("entityScoreContribution == 0. EntityId=" + allAlertsToIgnoreEntity.getId()).isGreaterThan(0);

        sendNotA_Risk(allAlertsToIgnoreEntity.getAlerts());

        PresidioUrl actualEntityUrl = restHelper.entities().withId(allAlertsToIgnoreEntity.getId()).url().withExpandedParameter();
        EntitiesStoredRecord actualEntity = restHelper.entities().request().getEntities(actualEntityUrl).get(0);

        List<AlertsStoredRecord> actualNotARiskAlerts = actualEntity.getAlerts().stream().filter(alert -> alert.getFeedback().equals("NOT_RISK")).collect(toList());

        assertThat(getIds(actualNotARiskAlerts))
                .as(actualEntityUrl + "\n'NOT_RISK' tag is missing from some alerts.")
                .containsExactlyInAnyOrderElementsOf(getIds(allAlertsToIgnoreEntity.getAlerts()));

        assertThat(actualNotARiskAlerts)
                .extracting(e -> valueOf(e.getEntityScoreContribution()))
                .as(actualEntityUrl + "\nExpected zero EntityScoreContributions for all alerts related to selected entity.")
                .containsOnly(0);

        assertThat(valueOf(actualEntity.getScore()))
                .as(actualEntityUrl + "\nSelected entity score is not 0.")
                .isEqualTo(0);
    }

    @Test
    public void unignore_some_alerts_validate_entity_score() {
        final int totalAlertsToIgnoreFirst = 3;
        final int totalAlertsToUnignore = 2;

        List<AlertsStoredRecord> allAlertsWithScoreContribution = getAllNonZeroScoreContributionAlerts(totalAlertsToIgnoreFirst, unIgnoreEntity);
        List<AlertsStoredRecord> firstIgnored = selectAlertsToIgnore(totalAlertsToIgnoreFirst, allAlertsWithScoreContribution);
        List<AlertsStoredRecord> alertsToUnignore = IntStream.range(0, totalAlertsToUnignore).mapToObj(firstIgnored::get).collect(toList());
        List<AlertsStoredRecord> leftIgnored = IntStream.range(totalAlertsToUnignore, firstIgnored.size()).mapToObj(firstIgnored::get).collect(toList());

        LOGGER.info("Going to untag before final test:\nEntityId=" + unIgnoreEntity.getId() +
                "\nAlertIds: [ \"" + alertsToUnignore.stream().map(AlertsStoredRecord::getId).collect(Collectors.joining("\", \"")) + "\" ]");

        int sumOfEntityScoreContributionsLeftIgnored = leftIgnored.stream().mapToInt(alert -> valueOf(alert.getEntityScoreContribution())).sum();
        assertThat(sumOfEntityScoreContributionsLeftIgnored).as("sumOfEntityScoreContributionsLeftIgnored == 0").isGreaterThan(0);

        sendNotA_Risk(firstIgnored);
        removeNotA_Risk(alertsToUnignore);


        PresidioUrl actualEntityUrl = restHelper.entities().withId(unIgnoreEntity.getId()).url().withExpandedParameter();
        EntitiesStoredRecord actualEntity = restHelper.entities().request().getEntities(actualEntityUrl).get(0);

        List<AlertsStoredRecord> actualNotARisk = actualEntity.getAlerts().stream().filter(alert -> alert.getFeedback().equals("NOT_RISK")).collect(toList());

        assertThat(getIds(actualNotARisk))
                .as(actualEntityUrl + "\n'NOT_RISK' tag is missing from some alerts.")
                .containsExactlyInAnyOrderElementsOf(getIds(leftIgnored));

        assertThat(actualNotARisk)
                .extracting(e -> valueOf(e.getEntityScoreContribution()))
                .as(actualEntityUrl + "\nExpected zero EntityScoreContributions for these alerts: " +
                        "\nAlertIds: [ \"" + firstIgnored.stream().map(AlertsStoredRecord::getId).collect(Collectors.joining("\", \"")) + "\" ]")
                .containsOnly(0);

        int expectedEntityScore = valueOf(unIgnoreEntity.getScore()) - sumOfEntityScoreContributionsLeftIgnored;

        assertThat(valueOf(actualEntity.getScore()))
                .as(actualEntityUrl + "\nActual and calculated EntityScoreContributions mismatch." +
                        "\nIgnored AlertIds: [ \"" + firstIgnored.stream().map(AlertsStoredRecord::getId).collect(Collectors.joining("\", \"")) + "\" ]" +
                        "\nsumOfEntityScoreContributionsLeftIgnored=" + sumOfEntityScoreContributionsLeftIgnored)
                .isEqualTo(expectedEntityScore);
    }






    private Predicate<EntitiesStoredRecord> byMinimumNonZeroScoreContributionAlerts = entity ->
            entity.getAlerts().stream().filter(alert -> valueOf(alert.getEntityScoreContribution()) > 0)
                    .count() >= MINIMUM_NON_ZERO_SCORE_ALERTS;

    private Predicate<EntitiesStoredRecord> byAlertsWithoutAnyTag = entity ->
            entity.getAlerts().parallelStream().allMatch(alert -> alert.getFeedback().equals("NONE"));

    private List<AlertsStoredRecord> getAllNonZeroScoreContributionAlerts(int maxAlertsToIgnore, EntitiesStoredRecord entity) {
        List<AlertsStoredRecord> allAlertsWithScoreContribution = entity.getAlerts().stream()
                .filter(alert -> valueOf(alert.getEntityScoreContribution()) > 0)
                .collect(toList());

        assertThat(allAlertsWithScoreContribution)
                .as("Required minimum " + maxAlertsToIgnore + 1 + " alerts with EntityScoreContribution > 0. EntityId=" + entity.getId())
                .hasSizeGreaterThan(maxAlertsToIgnore);
        return allAlertsWithScoreContribution;
    }

    private List<AlertsStoredRecord> selectAlertsToIgnore(int numOfAlertsToIgnore, List<AlertsStoredRecord> allAlertsWithScoreContribution) {
        IntStream alertsIndexesToIgnore = RandomDistinctRange(numOfAlertsToIgnore, 0, allAlertsWithScoreContribution.size());
        List<AlertsStoredRecord> alertsToIgnore = alertsIndexesToIgnore.mapToObj(allAlertsWithScoreContribution::get).collect(toList());
        LOGGER.info("Going to mark as 'not a risk': \nEntityId=" + allAlertsWithScoreContribution.get(0).getEntityDocumentId() +
                "\nAlertIds: [ \"" + alertsToIgnore.stream().map(AlertsStoredRecord::getId).collect(Collectors.joining("\", \"")) + "\" ]" +
                "\nFeedbacks: [ \"" + alertsToIgnore.stream().map(AlertsStoredRecord::getFeedback).collect(Collectors.joining("\", \"")) + "\" ]");
        return alertsToIgnore;
    }

    private List<String> getIds(List<AlertsStoredRecord> alerts) {
        return alerts.stream().map(AlertsStoredRecord::getId).collect(toList());
    }

    private void sendNotA_Risk(List<AlertsStoredRecord> alerts) {
        PresidioUrl notARiskUrl = restHelper.alerts().updateFeedback().setNotA_Risk(getIds(alerts));
        RestApiResponse sendNotARisk = restHelper.alerts().request().getRestApiResponse(notARiskUrl);
        if (sendNotARisk.getResponseCode() == 500) {
            LOGGER.error("Alerts, probably, are already tagged as 'NOT_RISK'");
        }
        assertThat(sendNotARisk.getResponseCode()).as(notARiskUrl.print() + "\nError response code.").isEqualTo(200);
    }

    private void removeNotA_Risk(List<AlertsStoredRecord> alerts) {
        PresidioUrl notARiskUrl = restHelper.alerts().updateFeedback().removeNotA_Risk(getIds(alerts));
        RestApiResponse sendNotARisk = restHelper.alerts().request().getRestApiResponse(notARiskUrl);
        if (sendNotARisk.getResponseCode() == 500) {
            LOGGER.error("Alerts, probably, are already tagged as 'NOT_RISK'");
        }
        assertThat(sendNotARisk.getResponseCode()).as(notARiskUrl.print() + "\nError response code.").isEqualTo(200);
    }

    private IntStream RandomDistinctRange(int size, int startInclusive, int endExclusive) {
        List<Integer> list = IntStream.range(startInclusive, endExclusive).boxed().collect(toList());
        Collections.shuffle(list);
        return list.stream().mapToInt(e -> e).limit(size);
    }

}
