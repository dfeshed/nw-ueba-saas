package fortscale.aggregation.feature.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by idanp on 9/28/2016.
 */
public class AggregatedEventQueryMongoServiceTest {

	private static final String SCORED_AGGR_EVENT_COLLECTION_PREFIX = "scored___aggr_event__";


	@Mock
	private MongoTemplate mongoTemplate;

	@InjectMocks
	private AggregatedEventQueryMongoService aggregatedEventQueryMongoService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetFeatureBucketDataFromSeveralCollections() throws Exception {

		String collectionName = "testCollection";
		String collectionBackupName = "backup_scored___aggr_event__testCollection";

		AggrEvent aggrEvent1 = new AggrEvent();
		aggrEvent1.contextId = "testCollection_id1";
		AggrEvent aggrEvent2 = new AggrEvent();
		aggrEvent2.contextId = "testCollection_id2";

		List<AggrEvent> originResult = new ArrayList<>();
		originResult.add(aggrEvent1);
		originResult.add(aggrEvent2);

		AggrEvent aggrEvent3 = new AggrEvent();
		aggrEvent3.contextId = "backup_testCollection_id3";
		AggrEvent aggrEvent4 = new AggrEvent();
		aggrEvent4.contextId = "backup_testCollection_id4";

		List<AggrEvent> backupResult = new ArrayList<>();
		backupResult.add(aggrEvent3);
		backupResult.add(aggrEvent4);


		when(mongoTemplate.find(any(Query.class), eq(AggrEvent.class), eq(SCORED_AGGR_EVENT_COLLECTION_PREFIX+collectionName))).thenReturn(originResult);
		when(mongoTemplate.find(any(Query.class), eq(AggrEvent.class), eq(collectionBackupName))).thenReturn(backupResult);

		ReflectionTestUtils.setField(aggregatedEventQueryMongoService, "collectionsBackupPrefixListAsString", "backup_");

		originResult.addAll(backupResult);
		List<AggrEvent> expectedResult = originResult;
		List<AggrEvent> actualResult = aggregatedEventQueryMongoService.getAggregatedEventsByContextAndTimeRange(collectionName,"","",0l,0l);

		assertTrue("Expected 'actualResult' and 'expectedResult' to be equal." +
				"\n  'actualResult'        = " + actualResult +
				"\n  'expectedResult' = " +expectedResult ,expectedResult.equals(actualResult));




	}

}
