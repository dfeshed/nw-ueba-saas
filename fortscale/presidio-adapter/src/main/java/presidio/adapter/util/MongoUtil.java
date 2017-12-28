package presidio.adapter.util;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.List;

public class MongoUtil {

    public static int deleteOlderThan(MongoTemplate template, String collectionName, String timestampField,
                                      Instant startDate) {
        Query query = new Query(); // Build query

        final List<Object> removedDocuments = template.findAllAndRemove(query, collectionName);

        if (removedDocuments != null) {
            return removedDocuments.size();
        }

        return 0;
    }
}
