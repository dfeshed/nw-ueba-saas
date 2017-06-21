package fortscale.ml.scorer.enriched_events;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.scorer.ScoringService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.scored.enriched_scored.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.scored.enriched_scored.DataSourceToAdeScoredEnrichedRecordClassResolver;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YaronDL on 6/15/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class EnrichedEventsScoringServiceImplTest {

    @Configuration
    public static class EnrichedEventsScoringServiceImplTestSpringConfig{
        @MockBean
        private ScoringService<EnrichedRecord> scoringService;
        @MockBean
        private ScoredEnrichedDataStore scoredEnrichedDataStore;
        @MockBean
        private AdeEnrichedScoredRecordBuilder adeEnrichedScoredRecordBuilder;
        @Bean
        public EnrichedEventsScoringService enrichedEventsScoringService(){
            return new EnrichedEventsScoringServiceImpl(scoringService, scoredEnrichedDataStore, adeEnrichedScoredRecordBuilder);
        }
    }

    @Autowired
    private ScoringService<EnrichedRecord> scoringService;
    @Autowired
    private ScoredEnrichedDataStore scoredEnrichedDataStore;
    @Autowired
    private AdeEnrichedScoredRecordBuilder adeEnrichedScoredRecordBuilder;

    @Autowired
    private EnrichedEventsScoringService enrichedEventsScoringService;


    @Test
    public void testEmptyEnrichedRecordListAsInput(){
        List<EnrichedRecord> enrichedRecordList = new ArrayList<>();

        List<AdeScoredEnrichedRecord> ret = enrichedEventsScoringService.scoreAndStoreEvents(enrichedRecordList);
        Assert.assertEquals(0,ret.size());

        verify(scoringService,times(0)).score(any());
        verify(adeEnrichedScoredRecordBuilder,times(0)).fill(any(),any(), any());
        verify(scoredEnrichedDataStore,times(0)).store(any());
    }


    @Test
    public void testEnrichedRecordListOfSizeOneAsInput(){
        List<EnrichedRecord> enrichedRecordList = new ArrayList<>();
        EnrichedRecord mockedEnrichedRecord = mock(EnrichedRecord.class);
        enrichedRecordList.add(mockedEnrichedRecord);

        List<FeatureScore> featureScoreList = mock(List.class);
        when(scoringService.score(mockedEnrichedRecord)).thenReturn(featureScoreList);

        enrichedEventsScoringService.scoreAndStoreEvents(enrichedRecordList);

        verify(scoringService,times(1)).score(mockedEnrichedRecord);
        verify(adeEnrichedScoredRecordBuilder,times(1)).fill(any(),eq(mockedEnrichedRecord), eq(featureScoreList));
        verify(scoredEnrichedDataStore,times(1)).store(any());
    }

    @Test
    public void testEnrichedRecordListOfSizeTwoAsInput(){
        List<EnrichedRecord> enrichedRecordList = new ArrayList<>();
        //first enriched record
        EnrichedRecord mockedEnrichedRecord1 = mock(EnrichedRecord.class);
        enrichedRecordList.add(mockedEnrichedRecord1);
        List<FeatureScore> featureScoreList1 = mock(List.class);
        when(scoringService.score(mockedEnrichedRecord1)).thenReturn(featureScoreList1);
        //second enriched record
        EnrichedRecord mockedEnrichedRecord2 = mock(EnrichedRecord.class);
        enrichedRecordList.add(mockedEnrichedRecord2);
        List<FeatureScore> featureScoreList2 = mock(List.class);
        when(scoringService.score(mockedEnrichedRecord2)).thenReturn(featureScoreList2);

        enrichedEventsScoringService.scoreAndStoreEvents(enrichedRecordList);

        //verifying first enrich record processed
        verify(scoringService,times(1)).score(mockedEnrichedRecord1);
        verify(adeEnrichedScoredRecordBuilder,times(1)).fill(any(),eq(mockedEnrichedRecord1), eq(featureScoreList1));

        //verifying first enrich record processed
        verify(scoringService,times(1)).score(mockedEnrichedRecord2);
        verify(adeEnrichedScoredRecordBuilder,times(1)).fill(any(),eq(mockedEnrichedRecord2), eq(featureScoreList2));

        //verifying store was called.
        verify(scoredEnrichedDataStore,times(1)).store(any());
    }


}
