package presidio.adapter.util;

import com.mongodb.WriteResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;

public class MongoUtil {

    public static int deleteOlderThan(MongoTemplate template, String collectionName, String timestampField,
                                      Instant startDate) {
        Query query = new Query(); // Build query
        query.addCriteria(Criteria.where(timestampField).lt(startDate));

        final WriteResult writeResult = template.remove(query, collectionName);

        return writeResult.getN();
    }
}
