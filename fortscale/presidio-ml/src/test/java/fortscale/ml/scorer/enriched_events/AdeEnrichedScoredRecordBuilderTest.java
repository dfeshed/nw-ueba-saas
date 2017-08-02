package fortscale.ml.scorer.enriched_events;


import static org.mockito.Mockito.*;

import fortscale.domain.feature.score.FeatureScore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.AdeEventTypeToAdeScoredEnrichedRecordClassResolver;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YaronDL on 6/18/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class AdeEnrichedScoredRecordBuilderTest {

    @Configuration
    public static class AdeEnrichedScoredRecordBuilderTestSpringConfig{
        @MockBean
        private AdeEventTypeToAdeScoredEnrichedRecordClassResolver dataSourceToAdeScoredEnrichedRecordClassResolver;

        @Bean
        public AdeEnrichedScoredRecordBuilder enrichedScoredRecordBuilder(){
            return new AdeEnrichedScoredRecordBuilder(dataSourceToAdeScoredEnrichedRecordClassResolver);
        }
    }

    @Autowired
    private AdeEventTypeToAdeScoredEnrichedRecordClassResolver dataSourceToAdeScoredEnrichedRecordClassResolver;

    @Autowired
    private AdeEnrichedScoredRecordBuilder enrichedScoredRecordBuilder;


    @Test
    public void testOneFeatureScoreForOneEnrichedRecord(){
        List<AdeScoredEnrichedRecord> scoredRecordList = new ArrayList<>();
        EnrichedRecord enrichedRecord = mock(EnrichedRecord.class);
        Instant enrichedRecordTime = Instant.now();
        when(enrichedRecord.getStartInstant()).thenReturn(enrichedRecordTime);
        List<FeatureScore> rootFeatureScoreList = new ArrayList<>();
        FeatureScore eventfeatureScore = mock(FeatureScore.class);
        rootFeatureScoreList.add(eventfeatureScore);
        List<FeatureScore> eventFeatureScoreList = new ArrayList<>();
        when(eventfeatureScore.getFeatureScores()).thenReturn(eventFeatureScoreList);
        FeatureScore testFeature = mock(FeatureScore.class);
        eventFeatureScoreList.add(testFeature);
        String testFeatureName = "testF";
        Double testFeatureScore = 51D;
        List<FeatureScore> testFeatureFeatureScoreList = new ArrayList<>();
        when(testFeature.getName()).thenReturn(testFeatureName);
        when(testFeature.getScore()).thenReturn(testFeatureScore);
        when(testFeature.getFeatureScores()).thenReturn(testFeatureFeatureScoreList);

        //Class<? extends AdeScoredEnrichedRecord> pojoClass = dataSourceToAdeScoredEnrichedRecordClassResolver.getClass(enrichedRecord.getDataSource());
        String testDataSource = "testDs";
        when(enrichedRecord.getDataSource()).thenReturn(testDataSource);
        Class<? extends AdeScoredEnrichedRecord> pojoClass = AdeScoredEnrichedTestingRecord.class;
        doReturn(pojoClass).when(dataSourceToAdeScoredEnrichedRecordClassResolver).getClass(testDataSource);

        enrichedScoredRecordBuilder.fill(scoredRecordList, enrichedRecord, rootFeatureScoreList);

        Assert.assertEquals("only one feature was scored", 1, scoredRecordList.size());
        AdeScoredEnrichedRecord adeScoredEnrichedRecord = scoredRecordList.get(0);
        Assert.assertTrue(adeScoredEnrichedRecord instanceof AdeScoredEnrichedTestingRecord);
        AdeScoredEnrichedTestingRecord adeScoredEnrichedTestingRecord = (AdeScoredEnrichedTestingRecord) adeScoredEnrichedRecord;
        Assert.assertEquals(enrichedRecordTime, adeScoredEnrichedTestingRecord.getStartInstant());
        Assert.assertEquals(testFeatureName, adeScoredEnrichedTestingRecord.getFeatureName());
        Assert.assertEquals(testFeatureScore, adeScoredEnrichedRecord.getScore());
        Assert.assertEquals(testFeatureFeatureScoreList, adeScoredEnrichedRecord.getFeatureScoreList());
    }

}
