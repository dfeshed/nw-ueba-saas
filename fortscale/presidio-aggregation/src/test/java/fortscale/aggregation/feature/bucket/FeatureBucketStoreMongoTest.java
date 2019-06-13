package fortscale.aggregation.feature.bucket;

import com.mongodb.*;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Collation;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

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


	@Ignore
	@Test
	public void test_get_distinct_context_ids() {
		FeatureBucketConf featureBucketConf = mock(FeatureBucketConf.class);
		when(featureBucketConf.getName()).thenReturn("testFeatureBucketConf");
		MongoCollection dbCollection = mock(MongoCollection.class);
		when(mongoTemplate.getCollection(anyString())).thenReturn(dbCollection);
		List<String> expected = getListOfContextIds();
		DistinctIterable distinctIterable = new DistinctIterable() {
            @Override
            public DistinctIterable filter(Bson filter) {
                return null;
            }

            @Override
            public DistinctIterable maxTime(long maxTime, TimeUnit timeUnit) {
                return null;
            }

            @Override
            public DistinctIterable batchSize(int batchSize) {
                return null;
            }

            @Override
            public DistinctIterable collation(Collation collation) {
                return null;
            }

            @Override
            public MongoCursor iterator() {
                return null;
            }

            @Override
            public Object first() {
                return null;
            }

            @Override
            public MongoIterable map(Function mapper) {
                return null;
            }

            @Override
            public void forEach(Block block) {

            }

            @Override
            public Collection into(Collection target) {
                target.addAll(expected);
                return target;
            }
        };
		when(dbCollection.distinct(eq(FeatureBucket.CONTEXT_ID_FIELD),any(Document.class), eq(String.class))).thenReturn(distinctIterable);
		Set<String> actual = store.getDistinctContextIds(featureBucketConf, new TimeRange(0, 0));
		Assert.assertEquals(expected.stream().collect(Collectors.toSet()), actual);
		verify(featureBucketConf, times(1)).getName();
		verify(mongoTemplate, times(1)).getCollection(anyString());
		verify(dbCollection, times(1)).distinct(eq(FeatureBucket.CONTEXT_ID_FIELD), any(Document.class), eq(String.class));
		verifyNoMoreInteractions(mongoTemplate, mongoDbBulkOpUtil, featureBucketConf, dbCollection);
	}

	@Test
	public void test_get_distinct_context_ids_after_catch() {
		FeatureBucketConf featureBucketConf = mock(FeatureBucketConf.class);
		AggregationResults aggregationResults = mock(AggregationResults.class);
		when(featureBucketConf.getName()).thenReturn("testFeatureBucketConf");
		MongoCollection dbCollection = mock(MongoCollection.class);
		when(mongoTemplate.getCollection(anyString())).thenReturn(dbCollection);

		when(dbCollection.distinct(eq(FeatureBucket.CONTEXT_ID_FIELD), any(Document.class), eq(String.class))).thenThrow(new MongoCommandException(new BsonDocument(), new ServerAddress()));

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
