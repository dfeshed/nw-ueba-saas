package fortscale.aggregation.feature.bucket;

import com.mongodb.*;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;
import org.bson.BsonDocument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.*;

public class FeatureBucketStoreMongoTest {
	private MongoTemplate mongoTemplate;
	private MongoDbBulkOpUtil mongoDbBulkOpUtil;
	private FeatureBucketStoreMongoImpl store;

	@Before
	public void before() {
		mongoTemplate = mock(MongoTemplate.class);
		mongoDbBulkOpUtil = mock(MongoDbBulkOpUtil.class);
		store = new FeatureBucketStoreMongoImpl(mongoTemplate, mongoDbBulkOpUtil, 50000);
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
		verifyNoMoreInteractions(mongoTemplate, mongoDbBulkOpUtil, featureBucketConf, dbCollection);
	}

	@Test
	public void test_get_distinct_context_ids_after_catch() {
		FeatureBucketConf featureBucketConf = mock(FeatureBucketConf.class);
		AggregationResults aggregationResults = mock(AggregationResults.class);
		when(featureBucketConf.getName()).thenReturn("testFeatureBucketConf");
		DBCollection dbCollection = mock(DBCollection.class);
		when(mongoTemplate.getCollection(anyString())).thenReturn(dbCollection);

		when(dbCollection.distinct(eq(FeatureBucket.CONTEXT_ID_FIELD), any(DBObject.class))).thenThrow(new MongoCommandException(new BsonDocument(), new ServerAddress()));

		List<DBObject> expected = getListOfContextIdsAsDBObject();
		when(mongoTemplate.aggregate(any(Aggregation.class), any(String.class), eq(DBObject.class))).thenReturn(aggregationResults);
		when(aggregationResults.getMappedResults()).thenReturn(expected);

		Set<String> actual = store.getDistinctContextIds(featureBucketConf, new TimeRange(0, 0));

		Assert.assertEquals(expected.stream().map(result -> (String) result.get(FeatureBucket.CONTEXT_ID_FIELD)).collect(Collectors.toSet()), actual);
		verify(featureBucketConf, times(1)).getName();
		verify(mongoTemplate, times(1)).aggregate(any(Aggregation.class), any(String.class), eq(DBObject.class));
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
		verifyNoMoreInteractions(mongoTemplate, mongoDbBulkOpUtil);
	}

	@Test
	public void test_store_feature_bucket() {
		FeatureBucketConf featureBucketConf = mock(FeatureBucketConf.class);
		when(featureBucketConf.getName()).thenReturn("testFeatureBucketConf");
		FeatureBucket featureBucket = new FeatureBucket();
		store.storeFeatureBucket(featureBucketConf, featureBucket, new StoreMetadataProperties());

		verify(featureBucketConf, times(1)).getName();
		verify(mongoDbBulkOpUtil, times(1)).insertUnordered(anyList(),anyString());
		verifyNoMoreInteractions(mongoTemplate, mongoDbBulkOpUtil, featureBucketConf);
	}

	private static List<String> getListOfContextIds() {
		List<String> contextIds = new LinkedList<>();
		contextIds.add("contextId1");
		contextIds.add("contextId2");
		contextIds.add("contextId3");
		return contextIds;
	}

	private static List<DBObject> getListOfContextIdsAsDBObject() {
		List<DBObject> contextIds = new LinkedList<>();
		for (int i=1; i<4; i++){
			DBObject dbObject = new BasicDBObject();
			dbObject.put(FeatureBucket.CONTEXT_ID_FIELD, "contextId" + i);
			contextIds.add(dbObject);
		}
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
