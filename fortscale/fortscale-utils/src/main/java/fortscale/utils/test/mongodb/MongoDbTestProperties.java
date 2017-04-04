package fortscale.utils.test.mongodb;

import java.util.Properties;

/**
 * Created by barak_schuster on 4/4/17.
 */
public class MongoDbTestProperties {
    public static Properties getProperties()
    {
        Properties properties = new Properties();

        properties.put("mongo.host.name","localhost");
        properties.put("mongo.host.port",27017);
        properties.put("mongo.db.name","fortscale");
        properties.put("mongo.db.user","");
        properties.put("mongo.db.password","");
        properties.put("mongo.map.dot.replacement","#dot#");
        properties.put("mongo.map.dollar.replacement","#dlr#");

        return properties;
    }
}
