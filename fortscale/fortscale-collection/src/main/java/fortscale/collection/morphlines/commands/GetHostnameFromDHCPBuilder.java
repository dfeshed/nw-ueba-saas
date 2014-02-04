package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordExtensions;
import fortscale.utils.logging.Logger;

public class GetHostnameFromDHCPBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("GetHostnameFromDHCP");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new GetHostnameFromDHCP(this, config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	private static final class GetHostnameFromDHCP extends AbstractCommand {

		private static final String STRING_EMPTY = "";
		private final String ipAddress;
		private final String timeStamp;
		private final String outputRecordName;
		private final String host;
		private final int port;
		private final String mongoDB;
		private final String mongoCollection;
		private int leaseTimeInMins = 0;

		private static MongoClient mongoClient = null;
		private static DBCollection dbCollection = null;

		private static final Logger logger = Logger.getLogger(GetHostnameFromDHCP.class);
		
		public GetHostnameFromDHCP(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.ipAddress = getConfigs().getString(config, "ipAddress");
			this.timeStamp = getConfigs().getString(config, "timeStamp");
			this.host = getConfigs().getString(config, "host");
			this.port = getConfigs().getInt(config, "port");
			this.outputRecordName = getConfigs().getString(config, "outputRecordName");
			this.mongoDB = getConfigs().getString(config, "db");
			this.mongoCollection = getConfigs().getString(config, "collection");
			this.leaseTimeInMins = getConfigs().getInt(config, "leaseTimeInMins");

			validateArguments();

			connectToMongo(this.host, this.port, this.mongoDB, this.mongoCollection);
		}

		private static void connectToMongo(String host, int port, String mongoDB,
				String collectioName) {
			if (mongoClient == null) {
				try {
					// Connect to the mongo server
					mongoClient = new MongoClient(host, port);
					// Get the fortscale db
					DB db = mongoClient.getDB(mongoDB);
					// Test the connection to the mongo server
					db.getCollectionNames();
					// If the connection works, Get the DHCP collection
					dbCollection = db.getCollection(collectioName);

				} catch (Exception e) {
					logger.error("Error Connecting to Mongo Server", e);
				}
			}
		}

		@Override
		protected boolean doProcess(Record inputRecord) {
			// If we weren't able to connect or access the collection,
			// return an empty string
			if (mongoClient != null && dbCollection != null) {		
				try {
					
					String ip = RecordExtensions.getStringValue(inputRecord, this.ipAddress);
					Long ts = RecordExtensions.getLongValue(inputRecord, this.timeStamp);
					
					// Try and get a hostname to the IP
					inputRecord.put(this.outputRecordName, getHostname(ip, ts));
					
				} catch (IllegalArgumentException e) {
					// did not found ip or ts fields in input record
					inputRecord.put(this.outputRecordName, STRING_EMPTY);
				}
			} else {
				inputRecord.put(this.outputRecordName, STRING_EMPTY);
			}

			return super.doProcess(inputRecord);

		}


		private String getHostname(String ip, long ts) {
			if (ip==null)
				return STRING_EMPTY;
			
			// Create a query for the IP address
			BasicDBObject query = new BasicDBObject("ip_address", ip);

			// If we get a timestamp specification, that means we need to find
			// the closest prior DHCP log, but not earlier that the specified
			// lease time

			if (ts != 0) {
				BasicDBObject val = new BasicDBObject("$lte", ts);
				if (leaseTimeInMins > 0) {
					val.append("$gte", ts - leaseTimeInMins * 60);
				}
				query.append("timestampepoch", val);
			}

			// get all dhcp records in the time slot, sort them according to
			// descending order and return the hostname of the most recent dhcp 
			// ip lease event
			DBCursor cursor = dbCollection.find(query);
			if (cursor!=null) {
				try {
					cursor.sort(new BasicDBObject("timestampepoch", -1));
					
					DBObject doc = cursor.next();
					if (doc!=null) {
						// Get the hostname, cache it and return it
						String mongoHostname = String.valueOf(doc.get("hostname"));
						if (mongoHostname != null && !mongoHostname.isEmpty()) {
							return mongoHostname;
						}
					}
					
				} finally {
					cursor.close();
				}
			}
			
			// We didn't find a hostname for the IP
			return STRING_EMPTY;
		}
	}
}
