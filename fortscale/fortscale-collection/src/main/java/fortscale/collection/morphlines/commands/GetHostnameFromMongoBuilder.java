package fortscale.collection.morphlines.commands;

import java.net.UnknownHostException;
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
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.typesafe.config.Config;

public class GetHostnameFromMongoBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("GetHostnameFromMongo");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new GetHostnameFromMongo(config, parent, child, context);
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Nested classes:
	// /////////////////////////////////////////////////////////////////////////////
	private static final class GetHostnameFromMongo extends AbstractCommand {

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

		public GetHostnameFromMongo(Config config, Command parent, Command child,
				MorphlineContext context) {
			super(config, parent, child, context);
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
					// Get the DHCP collection
					dbCollection = db.getCollection(collectioName);

				} catch (UnknownHostException e) {
					System.err.println("Error connecting to Mongo: " + e.getMessage());
				}
			}
		}

		@Override
		protected boolean doProcess(Record inputRecord) {
			List<?> ipValue = inputRecord.get(this.ipAddress);
			List<?> timeStamp = inputRecord.get(this.timeStamp);

			// If we weren't able to connect or access the collection,
			// return an empty string
			if (mongoClient != null && dbCollection != null) {

				String ip = null;
				int ts = 0;

				if (ipValue != null && ipValue.size() > 0) {
					ip = (String) ipValue.get(0);
				}

				if (timeStamp != null && timeStamp.size() > 0) {
					Object timeStampObject = timeStamp.get(0);
					try {
						if (timeStampObject != null) {
							ts = Integer.valueOf((String) timeStampObject);
						}
					} catch (ClassCastException e) {
						System.err.println("Error converting timeStamp to integer");
					}
				}

				if (ip != null) {
					// Try and get a hostname to the IP
					inputRecord.put(this.outputRecordName, getHostname(ip, ts));
				}
			}

			return super.doProcess(inputRecord);

		}

		private String getHostname(String ip) {
			return getHostname(ip, 0);
		}

		private String getHostname(String ip, int ts) {
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

			// Get one document
			DBObject docs = dbCollection.findOne(query);
			if (docs != null) {
				// Get the hostname, cache it and return it
				String mongoHostname = String.valueOf(docs.get("hostname"));
				if (mongoHostname != null && mongoHostname.length() != 0) {
					return mongoHostname;
				}
			}

			// We didn't find a hostname for the IP
			return STRING_EMPTY;
		}
	}
}
