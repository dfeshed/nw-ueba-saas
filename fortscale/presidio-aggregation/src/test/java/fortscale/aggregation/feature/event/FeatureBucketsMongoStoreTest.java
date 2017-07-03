package fortscale.aggregation.feature.event;

import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketStoreMongoImpl;
import fortscale.utils.time.TimeRange;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class FeatureBucketsMongoStoreTest {


	private static final String COLLECTION_NAME_PREFIX = "aggr_";

	@Mock
	private MongoTemplate mongoTemplate;


	@InjectMocks
	private FeatureBucketStoreMongoImpl featureBucketStoreMongoImpl;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetFeatureBucketsByContextAndTimeRange() throws Exception {

		List<String> dataSources = new ArrayList<>();
		dataSources.add("testDS");

		List<String> contextFieldNames = new ArrayList<>();
		contextFieldNames.add("testContext");

		Map<String, List<String>> featureNamesMap = new HashMap<>();
		List<String> stubList = new ArrayList<>();
		stubList.add("stub");
		featureNamesMap.put("testKey", stubList);

		JSONObject aggrFeatureFuncJson = new JSONObject();
		AggregatedFeatureConf aggregatedFeatureConf = new AggregatedFeatureConf("testAggregatedFeatureConf", featureNamesMap, aggrFeatureFuncJson);


		List<AggregatedFeatureConf> aggrFeatureConfs = new ArrayList<>();
		aggrFeatureConfs.add(aggregatedFeatureConf);


		FeatureBucketConf featureBucketConf = new FeatureBucketConf("testCollection", dataSources, contextFieldNames, "stubStartegy", aggrFeatureConfs);

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


		when(mongoTemplate.find(any(Query.class), eq(FeatureBucket.class), eq(String.format("%s%s", COLLECTION_NAME_PREFIX, featureBucketConf.getName())))).thenReturn(originResult);
		when(mongoTemplate.find(any(Query.class), eq(FeatureBucket.class), eq(collectionBackupName))).thenReturn(backupResult);

		originResult.addAll(backupResult);
		List<FeatureBucket> expectedResult = originResult;
		List<FeatureBucket> actualResult = featureBucketStoreMongoImpl.getFeatureBuckets(featureBucketConf.getName(),
				Collections.singleton(""), new TimeRange(1, 1), 0, 1000);

		assertTrue("Expected 'actualResult' and 'expectedResult' to be equal." +
				"\n  'actualResult'        = " + actualResult +
				"\n  'expectedResult' = " + expectedResult, expectedResult.equals(actualResult));
	}
}
