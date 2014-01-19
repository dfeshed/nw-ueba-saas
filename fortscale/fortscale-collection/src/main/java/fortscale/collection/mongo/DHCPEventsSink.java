package fortscale.collection.mongo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.kitesdk.morphline.api.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;


@Component
public class DHCPEventsSink {

	private static Logger logger = LoggerFactory.getLogger(DHCPEventsSink.class);
	
	@Value("${mongo.host.name}")
	private String host;
	@Value("${mongo.host.port}")
	private int port;
	@Value("${mongo.db.name}")
	private String dbName;
	
	private final String collectionName = "dhcp_log";
	
	private MongoClient mongoClient = null;
	private DBCollection dbCollection = null;
	
	
	public void connect() throws Exception {
		if (mongoClient == null) {
			try {
				// Connect to the mongo server
				mongoClient = new MongoClient(host, port);
				// Get the fortscale db
				DB db = mongoClient.getDB(dbName);
				// Test the connection to the mongo server
				db.getCollectionNames();
				// If the connection works, Get the DHCP collection
				dbCollection = db.getCollection(collectionName);

			} catch (Exception e) {
				String message = String.format("Error connecting to mongodb at %s:%s/%s", host, port, dbName);
				logger.error(message, e);
				throw e;
			}
		}
	}
	
	public void writeToMongo(Record record) throws Exception {
		if (mongoClient != null && dbCollection != null) {
			// create db object to contain the record fields
			DBObject object =  new BasicDBObject();
			
			object.put("datetime", getStringValue(record, "date_time"));
			object.put("timestampepoch", getLongValue(record, "date_time_epoch"));
			object.put("ip_address", getStringValue(record, "ip"));
			object.put("hostname", getStringValue(record, "hostname"));
			object.put("MAC_address", getStringValue(record, "mac_address"));
			 
			dbCollection.insert(object);
		}
	}
	
	public void postProcessIndexes() throws Exception {
		
		InputStream stream = DHCPEventsSink.class.getResourceAsStream("/META-INF/mongodb/index_dhcp_log.js");
		String javascript = getStringFromInputStream(stream);
		
		mongoClient.getDB(dbName).eval(javascript);
	}
	
	public void close() {
		if (mongoClient!=null) {
			mongoClient.close();
		}
	}
	
	private String getStringFromInputStream(InputStream is) throws IOException {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (IOException e) {
			logger.error("error reading js file", e);
			throw e;
		} finally {
			if (br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.warn("error closing js file", e);
				}
			}
		}
		return sb.toString();
	}

	private String getStringValue(Record record, String field) throws IllegalArgumentException {
		Object value = record.getFirstValue(field);
		if (value!=null && value instanceof String) {
			return (String)value;
		} else {
			logger.warn(String.format("field %s is missing from morphline record %s", field, record.toString()));
			throw new IllegalArgumentException("field " + field + " is missing from morphlines record");
		}
	}
	
	private Long getLongValue(Record record, String field) throws IllegalArgumentException  {
		Object value = record.getFirstValue(field);
		if (value!=null && value instanceof Long) {
			return (Long)value;
		} else {
			logger.warn(String.format("field %s is missing from morphline record %s", field, record.toString()));
			throw new IllegalArgumentException("field " + field + " is missing from morphlines record");
		}
	}
	

}
