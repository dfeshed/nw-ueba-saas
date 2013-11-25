package fortscale.services.domain.jdbc;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.DbCallback;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.geo.GeoResults;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class MongoTemplateDummy implements MongoOperations {

	@Override
	public String getCollectionName(Class<?> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommandResult executeCommand(String jsonCommand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommandResult executeCommand(DBObject command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommandResult executeCommand(DBObject command, int options) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeQuery(Query query, String collectionName,
			DocumentCallbackHandler dch) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T execute(DbCallback<T> action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T execute(Class<?> entityClass, CollectionCallback<T> action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T execute(String collectionName, CollectionCallback<T> action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T executeInSession(DbCallback<T> action) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> DBCollection createCollection(Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> DBCollection createCollection(Class<T> entityClass,
			CollectionOptions collectionOptions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DBCollection createCollection(String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DBCollection createCollection(String collectionName,
			CollectionOptions collectionOptions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getCollectionNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DBCollection getCollection(String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> boolean collectionExists(Class<T> entityClass) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean collectionExists(String collectionName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> void dropCollection(Class<T> entityClass) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropCollection(String collectionName) {
		// TODO Auto-generated method stub

	}

	@Override
	public IndexOperations indexOps(String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IndexOperations indexOps(Class<?> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> findAll(Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> findAll(Class<T> entityClass, String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> GroupByResults<T> group(String inputCollectionName,
			GroupBy groupBy, Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> GroupByResults<T> group(Criteria criteria,
			String inputCollectionName, GroupBy groupBy, Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation,
			String collectionName, Class<O> outputType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation,
			Class<O> outputType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <O> AggregationResults<O> aggregate(Aggregation aggregation,
			Class<?> inputType, Class<O> outputType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <O> AggregationResults<O> aggregate(Aggregation aggregation,
			String collectionName, Class<O> outputType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> MapReduceResults<T> mapReduce(String inputCollectionName,
			String mapFunction, String reduceFunction, Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> MapReduceResults<T> mapReduce(String inputCollectionName,
			String mapFunction, String reduceFunction,
			MapReduceOptions mapReduceOptions, Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> MapReduceResults<T> mapReduce(Query query,
			String inputCollectionName, String mapFunction,
			String reduceFunction, Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> MapReduceResults<T> mapReduce(Query query,
			String inputCollectionName, String mapFunction,
			String reduceFunction, MapReduceOptions mapReduceOptions,
			Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> GeoResults<T> geoNear(NearQuery near, Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> GeoResults<T> geoNear(NearQuery near, Class<T> entityClass,
			String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T findOne(Query query, Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T findOne(Query query, Class<T> entityClass,
			String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(Query query, String collectionName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean exists(Query query, Class<?> entityClass) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean exists(Query query, Class<?> entityClass,
			String collectionName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> List<T> find(Query query, Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> find(Query query, Class<T> entityClass,
			String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T findById(Object id, Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T findById(Object id, Class<T> entityClass, String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T findAndModify(Query query, Update update, Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T findAndModify(Query query, Update update,
			Class<T> entityClass, String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T findAndModify(Query query, Update update,
			FindAndModifyOptions options, Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T findAndModify(Query query, Update update,
			FindAndModifyOptions options, Class<T> entityClass,
			String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T findAndRemove(Query query, Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T findAndRemove(Query query, Class<T> entityClass,
			String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count(Query query, Class<?> entityClass) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long count(Query query, String collectionName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void insert(Object objectToSave) {
		// TODO Auto-generated method stub

	}

	@Override
	public void insert(Object objectToSave, String collectionName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void insert(Collection<? extends Object> batchToSave,
			Class<?> entityClass) {
		// TODO Auto-generated method stub

	}

	@Override
	public void insert(Collection<? extends Object> batchToSave,
			String collectionName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void insertAll(Collection<? extends Object> objectsToSave) {
		// TODO Auto-generated method stub

	}

	@Override
	public void save(Object objectToSave) {
		// TODO Auto-generated method stub

	}

	@Override
	public void save(Object objectToSave, String collectionName) {
		// TODO Auto-generated method stub

	}

	@Override
	public WriteResult upsert(Query query, Update update, Class<?> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WriteResult upsert(Query query, Update update, String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WriteResult upsert(Query query, Update update, Class<?> entityClass,
			String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WriteResult updateFirst(Query query, Update update,
			Class<?> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WriteResult updateFirst(Query query, Update update,
			String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WriteResult updateFirst(Query query, Update update,
			Class<?> entityClass, String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WriteResult updateMulti(Query query, Update update,
			Class<?> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WriteResult updateMulti(Query query, Update update,
			String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WriteResult updateMulti(Query query, Update update,
			Class<?> entityClass, String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(Object object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Object object, String collection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Query query, Class<?> entityClass) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Query query, Class<?> entityClass, String collectionName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Query query, String collectionName) {
		// TODO Auto-generated method stub

	}

	@Override
	public MongoConverter getConverter() {
		// TODO Auto-generated method stub
		return null;
	}

}
