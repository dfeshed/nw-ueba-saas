package fortscale.ml.scorer.enriched_events;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.scorer.ScoringService;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import fortscale.utils.time.TimeRange;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class EnrichedEventsScoringServiceImplTest {
    @Configuration
    public static class EnrichedEventsScoringServiceImplTestSpringConfig {
        @MockBean
        private RecordReaderFactoryService recordReaderFactoryService;
        @MockBean
        private ScoringService scoringService;
        @MockBean
        private ScoredEnrichedDataStore scoredEnrichedDataStore;
        @MockBean
        private AdeEnrichedScoredRecordBuilder adeEnrichedScoredRecordBuilder;

        @Bean
        public EnrichedEventsScoringService enrichedEventsScoringService() {
            return new EnrichedEventsScoringServiceImpl(
                    recordReaderFactoryService,
                    scoringService,
                    scoredEnrichedDataStore,
                    adeEnrichedScoredRecordBuilder);
        }
    }

    private TimeRange timeRange = new TimeRange(Instant.parse("2017-12-03T10:00:00.00Z"),Instant.parse("2017-12-03T11:00:00.00Z"));

    @Autowired
    private RecordReaderFactoryService recordReaderFactoryService;
    @Autowired
    private ScoringService scoringService;
    @Autowired
    private ScoredEnrichedDataStore scoredEnrichedDataStore;
    @Autowired
    private AdeEnrichedScoredRecordBuilder adeEnrichedScoredRecordBuilder;
    @Autowired
    private EnrichedEventsScoringService enrichedEventsScoringService;

    @Test
    public void testEmptyEnrichedRecordListAsInput() {
        List<EnrichedRecord> enrichedRecordList = new ArrayList<>();
        List<AdeScoredEnrichedRecord> ret = enrichedEventsScoringService.scoreAndStoreEvents(enrichedRecordList, true, timeRange);
        Assert.assertEquals(0, ret.size());
        verify(scoringService, times(0)).score(any(), eq(timeRange));
        verify(adeEnrichedScoredRecordBuilder, times(0)).fill(any(), any(), any());
        verify(scoredEnrichedDataStore, times(0)).store(any());
    }

    @Test
    public void testEnrichedRecordListOfSizeOneAsInput() {
        List<EnrichedRecord> enrichedRecordList = new ArrayList<>();
        EnrichedRecord mockedEnrichedRecord = mock(EnrichedRecord.class);
        AdeRecordReader mockedAdeRecordReader = mock(AdeRecordReader.class);
        enrichedRecordList.add(mockedEnrichedRecord);
        List<FeatureScore> featureScoreList = mock(List.class);
        when(recordReaderFactoryService.getRecordReader(eq(mockedEnrichedRecord))).thenReturn(mockedAdeRecordReader);
        when(scoringService.score(eq(mockedAdeRecordReader), eq(timeRange))).thenReturn(featureScoreList);
        enrichedEventsScoringService.scoreAndStoreEvents(enrichedRecordList, true, timeRange);
        verify(scoringService, times(1)).score(eq(mockedAdeRecordReader), eq(timeRange));
        verify(adeEnrichedScoredRecordBuilder, times(1)).fill(any(), eq(mockedEnrichedRecord), eq(featureScoreList));
        verify(scoredEnrichedDataStore, times(1)).store(any());
    }

    @Test
    public void testEnrichedRecordListOfSizeTwoAsInput() {
        List<EnrichedRecord> enrichedRecordList = new ArrayList<>();

        //first enriched record
        EnrichedRecord mockedEnrichedRecord1 = mock(EnrichedRecord.class);
        AdeRecordReader mockedAdeRecordReader1 = mock(AdeRecordReader.class);
        enrichedRecordList.add(mockedEnrichedRecord1);
        List<FeatureScore> featureScoreList1 = mock(List.class);
        when(recordReaderFactoryService.getRecordReader(eq(mockedEnrichedRecord1))).thenReturn(mockedAdeRecordReader1);
        when(scoringService.score(eq(mockedAdeRecordReader1), eq(timeRange))).thenReturn(featureScoreList1);

        //second enriched record
        EnrichedRecord mockedEnrichedRecord2 = mock(EnrichedRecord.class);
        AdeRecordReader mockedAdeRecordReader2 = mock(AdeRecordReader.class);
        enrichedRecordList.add(mockedEnrichedRecord2);
        List<FeatureScore> featureScoreList2 = mock(List.class);
        when(recordReaderFactoryService.getRecordReader(eq(mockedEnrichedRecord2))).thenReturn(mockedAdeRecordReader2);
        when(scoringService.score(eq(mockedAdeRecordReader2), eq(timeRange))).thenReturn(featureScoreList2);
        enrichedEventsScoringService.scoreAndStoreEvents(enrichedRecordList, true, timeRange);

        //verifying first enrich record processed
        verify(scoringService, times(1)).score(eq(mockedAdeRecordReader1), eq(timeRange));
        verify(adeEnrichedScoredRecordBuilder, times(1)).fill(any(), eq(mockedEnrichedRecord1), eq(featureScoreList1));

        //verifying first enrich record processed
        verify(scoringService, times(1)).score(eq(mockedAdeRecordReader2), eq(timeRange));
        verify(adeEnrichedScoredRecordBuilder, times(1)).fill(any(), eq(mockedEnrichedRecord2), eq(featureScoreList2));

        //verifying store was called.
        verify(scoredEnrichedDataStore, times(1)).store(any());
    }
}
