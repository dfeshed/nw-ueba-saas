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
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

public class AddToWatchListTests extends AbstractTestNGSpringContextTests {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(AddToWatchListTests.class);

    private RestHelper restHelper = new RestHelper();
    private ImmutableList<EntitiesStoredRecord> allEntities;
    private PresidioUrl allEntitiesUrl;
    private ImmutableList<String> entityTypes = ImmutableList.of("sslSubject", "ja3", "userId");


    @BeforeClass
    public void prepareData() throws JSONException {
        allEntitiesUrl = restHelper.entities().url().withMaxSizeParameters();
        allEntities = ImmutableList.copyOf(restHelper.entities().request().getEntities(allEntitiesUrl));
        assertThat(allEntities).isNotNull().isNotEmpty();
    }

    @Test
    public void adding_a_single_entity_to_the_watchlist() {
        SoftAssertions softly = new SoftAssertions();

        EntitiesStoredRecord entityToWatch = allEntities.get(ThreadLocalRandom.current().nextInt(allEntities.size()));
        PresidioUrl singleEntityAddUrl = restHelper.entitiesWatchList().url().singleEntityAdd(entityToWatch.getId());
        List<EntitiesStoredRecord> addedEntities = restHelper.entitiesWatchList().request().getEntities(singleEntityAddUrl);

        List<EntitiesStoredRecord> allEntities = restHelper.entities().request().getEntities(allEntitiesUrl);

        softly.assertAll();
    }

}
