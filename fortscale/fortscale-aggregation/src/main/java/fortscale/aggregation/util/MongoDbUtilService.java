package fortscale.aggregation.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashSet;
import java.util.Set;


public class MongoDbUtilService implements InitializingBean {
    @Autowired
    private MongoTemplate mongoTemplate;

    private Set<String> collectionNames;

    @Override
    public void afterPropertiesSet() throws Exception {
        collectionNames = new HashSet<>(mongoTemplate.getCollectionNames());
    }

    public boolean collectionExists(String collectionName) {
        return collectionNames.contains(collectionName);
    }

    public void createCollection(String collectionName) {
        mongoTemplate.createCollection(collectionName);
        collectionNames.add(collectionName);
    }

    public Set<String> getCollections(){
        return collectionNames;
    }

}
