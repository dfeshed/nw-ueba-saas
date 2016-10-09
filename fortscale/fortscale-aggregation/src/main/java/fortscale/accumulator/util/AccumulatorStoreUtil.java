package fortscale.accumulator.util;

import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by barak_schuster on 10/9/16.
 */
public class AccumulatorStoreUtil {
    public static Set<String> getACMExistingCollections(MongoTemplate mongoTemplate,String collectionNameRegex) {
        return mongoTemplate.getCollectionNames().stream().filter(x -> x.matches(collectionNameRegex)).collect(Collectors.toSet());
    }
}
