package fortscale.aggregation.feature.bucket;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fortscale.utils.mongodb.util.MongoDbUtilService;
import fortscale.utils.time.TimeRange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class FeatureBucketStoreMongoTest {
	private static final long DEFAULT_EXPIRE_AFTER_SECONDS = TimeUnit.DAYS.toSeconds(90);

	private MongoTemplate mongoTemplate;
	private MongoDbUtilService mongoDbUtilService;
	private FeatureBucketStoreMongoImpl store;

	@Before
	public void before() {
		mongoTemplate = mock(MongoTemplate.class);
		mongoDbUtilService = mock(MongoDbUtilService.class);
		store = new FeatureBucketStoreMongoImpl(mongoTemplate, mongoDbUtilService, DEFAULT_EXPIRE_AFTER_SECONDS);
	}

	@Test
	public void test_get_distinct_context_ids() {
		FeatureBucketConf featureBucketConf = mock(FeatureBucketConf.class);
		when(featureBucketConf.getName()).thenReturn("testFeatureBucketConf");
		DBCollection dbCollection = mock(DBCollection.class);
		when(mongoTemplate.getCollection(anyString())).thenReturn(dbCollection);
		List<String> expected = getListOfContextIds();
		when(dbCollection.distinct(eq(FeatureBucket.CONTEXT_ID_FIELD), any(DBObject.class))).thenReturn(expected);
		Set<String> actual = store.getDistinctContextIds(featureBucketConf, new TimeRange(0, 0));

		Assert.assertEquals(expected.stream().collect(Collectors.toSet()), actual);
		verify(featureBucketConf, times(1)).getName();
		verify(mongoTemplate, times(1)).getCollection(anyString());
		verify(dbCollection, times(1)).distinct(eq(FeatureBucket.CONTEXT_ID_FIELD), any(DBObject.class));
		verifyNoMoreInteractions(mongoTemplate, mongoDbUtilService, featureBucketConf, dbCollection);
	}

	@Test
	public void test_get_feature_buckets() {
		String featureBucketConfName = "testFeatureBucketConf";
		Set<String> contextIds = getSetOfContextIds();
		List<FeatureBucket> expected = getListOfFeatureBuckets();
		when(mongoTemplate.find(any(Query.class), eq(FeatureBucket.class), anyString())).thenReturn(expected);
		List<FeatureBucket> actual = store.getFeatureBuckets(featureBucketConfName, contextIds, new TimeRange(0, 0));

		Assert.assertEquals(expected, actual);
		verify(mongoTemplate, times(1)).find(any(Query.class), eq(FeatureBucket.class), anyString());
		verifyNoMoreInteractions(mongoTemplate, mongoDbUtilService);
	}

	@Test
	public void test_store_feature_bucket() {
		FeatureBucketConf featureBucketConf = mock(FeatureBucketConf.class);
		when(featureBucketConf.getName()).thenReturn("testFeatureBucketConf");
		when(mongoDbUtilService.collectionExists(anyString())).thenReturn(true);
		FeatureBucket featureBucket = new FeatureBucket();
		store.storeFeatureBucket(featureBucketConf, featureBucket);

		verify(featureBucketConf, times(1)).getName();
		verify(mongoDbUtilService, times(1)).collectionExists(anyString());
		verify(mongoTemplate, times(1)).save(eq(featureBucket), anyString());
		verifyNoMoreInteractions(mongoTemplate, mongoDbUtilService, featureBucketConf);
	}

	private static List<String> getListOfContextIds() {
		List<String> contextIds = new LinkedList<>();
		contextIds.add("contextId1");
		contextIds.add("contextId2");
		contextIds.add("contextId3");
		return contextIds;
	}

	private static Set<String> getSetOfContextIds() {
		Set<String> contextIds = new HashSet<>();
		contextIds.add("contextId1");
		contextIds.add("contextId2");
		contextIds.add("contextId3");
		return contextIds;
	}

	private static List<FeatureBucket> getListOfFeatureBuckets() {
		List<FeatureBucket> featureBuckets = new LinkedList<>();
		featureBuckets.add(new FeatureBucket());
		featureBuckets.add(new FeatureBucket());
		featureBuckets.add(new FeatureBucket());
		return featureBuckets;
	}
}
