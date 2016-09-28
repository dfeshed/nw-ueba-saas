package fortscale.aggregation.feature.event;

import fortscale.aggregation.feature.bucket.FeatureBucket;
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


public class FeatureBucketQueryMongoServiceTest {

	@Mock
	private MongoTemplate mongoTemplate;

	@InjectMocks
	private FeatureBucketQueryMongoService featureBucketQueryMongoService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetFeatureBucketDataFromSeveralCollections() throws Exception {

		String collectionName = "testCollection";
		String collectionBackupName = "backup_testCollection";

		FeatureBucket featureBucket1 = new FeatureBucket();
		featureBucket1.setBucketId("testCollection_id1");
		FeatureBucket featureBucket2 = new FeatureBucket();
		featureBucket2.setBucketId("testCollection_id2");

		List<FeatureBucket> originResult = new ArrayList<>();
		originResult.add(featureBucket1);
		originResult.add(featureBucket2);

		FeatureBucket featureBucket3 = new FeatureBucket();
		featureBucket3.setBucketId("backup_testCollection_id3");
		FeatureBucket featureBucket4 = new FeatureBucket();
		featureBucket4.setBucketId("backup_testCollection_id4");

		List<FeatureBucket> backupResult = new ArrayList<>();
		backupResult.add(featureBucket3);
		backupResult.add(featureBucket4);


		when(mongoTemplate.find(any(Query.class), eq(FeatureBucket.class), eq(collectionName))).thenReturn(originResult);
		when(mongoTemplate.find(any(Query.class), eq(FeatureBucket.class), eq(collectionBackupName))).thenReturn(backupResult);
		ReflectionTestUtils.setField(featureBucketQueryMongoService, "collectionsBackupPrefixListAsString", "backup_");

		originResult.addAll(backupResult);
		List<FeatureBucket> expectedResult = originResult;
		List<FeatureBucket> actualResult = featureBucketQueryMongoService.getFeatureBucketsByContextAndTimeRange(collectionName,"","",0l,0l);

		assertTrue("Expected 'actualResult' and 'expectedResult' to be equal." +
				"\n  'actualResult'        = " + actualResult +
				"\n  'expectedResult' = " +expectedResult ,expectedResult.equals(actualResult));




	}
}
