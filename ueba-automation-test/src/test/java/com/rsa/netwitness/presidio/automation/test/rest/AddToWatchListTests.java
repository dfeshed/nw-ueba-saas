package com.rsa.netwitness.presidio.automation.test.rest;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.ImmutableList;
import com.rsa.netwitness.presidio.automation.domain.output.EntitiesStoredRecord;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import org.assertj.core.api.SoftAssertions;
import org.json.JSONException;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class AddToWatchListTests extends AbstractTestNGSpringContextTests {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(AddToWatchListTests.class);

    private RestHelper restHelper = new RestHelper();
    private ImmutableList<EntitiesStoredRecord> allEntities;
    private PresidioUrl allEntitiesUrl;

    @BeforeClass
    public void prepareData() throws JSONException {
        allEntitiesUrl = restHelper.entities().url().withMaxSizeParameters();
        allEntities = ImmutableList.copyOf(restHelper.entities().request().getEntities(allEntitiesUrl));
        assertThat(allEntities).isNotNull().isNotEmpty();
    }

    @Test
    public void add_a_single_entity_to_the_watchlist() {
        EntitiesStoredRecord selectedEntity = selectAndAddToWatchList();

        EntitiesStoredRecord actualFromAllEntities = restHelper.entities().request().getEntities(allEntitiesUrl)
                .parallelStream()
                .filter(e -> e.getId().equals(selectedEntity.getId()))
                .findFirst().orElseThrow();

        assertThat(actualFromAllEntities.getTags()).as(allEntitiesUrl + "\n'Watched' tag is missing for entity Id=" + actualFromAllEntities.getId()).isNotEmpty().containsOnlyOnce("watched");
    }

    @Test
    public void add_remove_a_single_entity_from_the_watchlist() {
        EntitiesStoredRecord selectedEntity = selectAndAddToWatchList();
        removeSelectedFromTheWatchList(selectedEntity);

        EntitiesStoredRecord actualFromAllEntities = restHelper.entities().request().getEntities(allEntitiesUrl)
                .parallelStream()
                .filter(e -> e.getId().equals(selectedEntity.getId()))
                .findFirst().orElseThrow();

        assertThat(actualFromAllEntities.getTags()).as(allEntitiesUrl + "\n'Watched' tag still present for entity Id=" + actualFromAllEntities.getId()).doesNotContain("watched");
    }

    @Test
    public void add_entity_bulk_to_the_watchlist() {
        final String entityType = "ja3";

        SoftAssertions softly = new SoftAssertions();
        Map.Entry<String, List<EntitiesStoredRecord>> selectedJa3Bulk = largeBulkByAnySeverity(entityType);

        String selectedSeverity = selectedJa3Bulk.getKey();
        List<EntitiesStoredRecord> selectedEntities = selectedJa3Bulk.getValue();
        List<String> expectedAddedIds = getIds(selectedEntities);

        addBulkToWatchList(entityType, selectedSeverity, expectedAddedIds);

        for (String addedIds : expectedAddedIds) {
            PresidioUrl url = restHelper.entities().withId(addedIds).url().withNoParameters();
            List<EntitiesStoredRecord> actualFromCommonRest = restHelper.entities().request().getEntities(url);
            softly.assertThat(actualFromCommonRest).as(url.print() + "\nMultiple or empty entities return for entity Id=" + addedIds).hasSize(1);
            softly.assertThat(actualFromCommonRest.get(0).getTags()).as(url.print() + "\n'Watched' tag is missing for entity Id=" + addedIds).containsOnlyOnce("watched");
        }

        softly.assertAll();
    }

    @Test
    public void add_remove_entity_bulk_to_the_watchlist() {
        final String entityType = "userId";

        SoftAssertions softly = new SoftAssertions();
        Map.Entry<String, List<EntitiesStoredRecord>> selectedJa3Bulk = largeBulkByAnySeverity(entityType);

        String selectedSeverity = selectedJa3Bulk.getKey();
        List<EntitiesStoredRecord> selectedEntities = selectedJa3Bulk.getValue();
        List<String> expectedAddedIds = getIds(selectedEntities);

        addBulkToWatchList(entityType, selectedSeverity, expectedAddedIds);
        removeBulkFromWatchList(entityType, selectedSeverity, expectedAddedIds);

        for (String addedIds : expectedAddedIds) {
            PresidioUrl url = restHelper.entities().withId(addedIds).url().withNoParameters();
            List<EntitiesStoredRecord> actualFromCommonRest = restHelper.entities().request().getEntities(url);
            softly.assertThat(actualFromCommonRest).as(url.print() + "\nMultiple or empty entities return for entity Id=" + addedIds).hasSize(1);
            softly.assertThat(actualFromCommonRest.get(0).getTags()).as(url.print() + "\n'Watched' tag is still present for entity Id=" + addedIds).doesNotContain("watched");
        }
        softly.assertAll();
    }








    private EntitiesStoredRecord selectAndAddToWatchList() {
        EntitiesStoredRecord selectedEntity = allEntities.get(ThreadLocalRandom.current().nextInt(allEntities.size()));
        PresidioUrl watchlistUrl = restHelper.watchlist().url().entityAdd(selectedEntity.getId());
        List<EntitiesStoredRecord> actualWatchlistSelectionResult = restHelper.watchlist().request().getEntities(watchlistUrl);

        assertThat(actualWatchlistSelectionResult)
                .as(watchlistUrl.print()+"\nEmpty or multiple return for entityId=" + selectedEntity.getId() + "\nWatchlist entities:\n" + actualWatchlistSelectionResult)
                .hasSize(1);

        assertThat(actualWatchlistSelectionResult.get(0).getId())
                .as(watchlistUrl.print()+"\nWatchlist and selected entity id mismatch.\nWatchlist entities:\n" + actualWatchlistSelectionResult.get(0))
                .isEqualTo(selectedEntity.getId());

        assertThat(actualWatchlistSelectionResult.get(0).getTags())
                .as(watchlistUrl.print() + "\n'Watched' tag is missing for entity Id=" + selectedEntity.getId() + "\nWatchlist entities:\n" + actualWatchlistSelectionResult.get(0))
                .isNotEmpty().containsOnlyOnce("watched");
        return selectedEntity;
    }

    private void removeSelectedFromTheWatchList(EntitiesStoredRecord selectedEntity) {
        PresidioUrl watchlistUrl = restHelper.watchlist().url().entityRemove(selectedEntity.getId());
        List<EntitiesStoredRecord> actualWatchlistRemovalResult = restHelper.watchlist().request().getEntities(watchlistUrl);

        assertThat(actualWatchlistRemovalResult)
                .as(watchlistUrl.print()+"\nEmpty or multiple return for entityId=" + selectedEntity.getId() + "\nWatchlist entities:\n" + actualWatchlistRemovalResult)
                .hasSize(1);

        assertThat(actualWatchlistRemovalResult.get(0).getId())
                .as(watchlistUrl.print()+"\nWatchlist and selected entity id mismatch.\nWatchlist entities:\n" + actualWatchlistRemovalResult.get(0))
                .isEqualTo(selectedEntity.getId());

        assertThat(actualWatchlistRemovalResult.get(0).getTags())
                .as(watchlistUrl.print() + "\n'Watched' tag still present for entity Id=" + selectedEntity.getId() + "\nWatchlist entities:\n" + actualWatchlistRemovalResult.get(0))
                .doesNotContain("watched");
    }

    private Map.Entry<String, List<EntitiesStoredRecord>> largeBulkByAnySeverity(String entityType) {
        return allEntities.parallelStream()
                .filter(e -> e.getEntityType().equals(entityType))
                .collect(Collectors.groupingBy(EntitiesStoredRecord::getSeverity))
                .entrySet().stream()
                .filter(e -> e.getValue().size() > 10)
                .findAny().orElseThrow(() -> new RuntimeException("Not found 10 entities for any severity for entityType=" + entityType));
    }

    private List<String> getIds(List<EntitiesStoredRecord> entities) {
       return entities.parallelStream().map(EntitiesStoredRecord::getId).collect(Collectors.toList());
    }

    private void addBulkToWatchList(String entityType, String selectedSeverity, List<String> expectedAddedIds) {
        PresidioUrl watchlistUrl = restHelper.watchlist().url().bulkAdd(entityType, selectedSeverity);
        List<EntitiesStoredRecord> addedEntities = restHelper.watchlist().request().getEntities(watchlistUrl);
        List<String> actualAddedIds = getIds(addedEntities);

        assertThat(addedEntities).as(watchlistUrl.print()+"\nWatchlist and selected entity ids count mismatch." +
                "\nselectedSeverity=" + selectedSeverity + " entityType=" + entityType +
                "\nWatchlist entities:\n" + addedEntities)
                .isNotEmpty().hasSize(actualAddedIds.size());

        assertThat(actualAddedIds).as(watchlistUrl.print()+"\nWatchlist and selected entity ids mismatch." +
                "\nselectedSeverity=" + selectedSeverity + " entityType=" + entityType +
                "\nWatchlist entities:\n" + addedEntities)
                .containsExactlyInAnyOrderElementsOf(expectedAddedIds);

        SoftAssertions softly = new SoftAssertions();

        for (EntitiesStoredRecord addedEntity : addedEntities) {
            softly.assertThat(addedEntity.getTags()).as(watchlistUrl.print() + "\n'Watched' tag is missing for entity Id=" +
                    addedEntity.getId() + "\nWatchlist entities:\n" + addedEntity +
                    "\nselectedSeverity=" + selectedSeverity + " entityType=" + entityType)
                    .containsOnlyOnce("watched");
        }

        softly.assertAll();
    }

    private void removeBulkFromWatchList(String entityType, String selectedSeverity, List<String> expectedAddedIds) {
        PresidioUrl watchlistUrl = restHelper.watchlist().url().bulkRemove(entityType, selectedSeverity);
        List<EntitiesStoredRecord> removedEntities = restHelper.watchlist().request().getEntities(watchlistUrl);
        List<String> actualRemovedIds = getIds(removedEntities);

        assertThat(removedEntities)
                .as(watchlistUrl.print()+"\nWatchlist and selected entity ids count mismatch." +
                "\nselectedSeverity=" + selectedSeverity + " entityType=" + entityType +
                "\nWatchlist entities:\n" + removedEntities)
                .isNotEmpty().hasSize(actualRemovedIds.size());

        assertThat(actualRemovedIds)
                .as(watchlistUrl.print()+"\nWatchlist and selected entity ids mismatch." +
                "\nselectedSeverity=" + selectedSeverity + " entityType=" + entityType +
                "\nWatchlist entities:\n" + removedEntities)
                .containsExactlyInAnyOrderElementsOf(expectedAddedIds);

        SoftAssertions softly = new SoftAssertions();

        for (EntitiesStoredRecord removedEntity : removedEntities) {
            softly.assertThat(removedEntity.getTags()).as(watchlistUrl.print() + "\n'Watched' tag still present for entity Id=" +
                    removedEntity.getId() + "\nWatchlist entities:\n" + removedEntity +
                    "\nselectedSeverity=" + selectedSeverity + " entityType=" + entityType)
                    .doesNotContain("watched");
        }

        softly.assertAll();
    }


}
