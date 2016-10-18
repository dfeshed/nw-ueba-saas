package fortscale.accumulator.util;

import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Period;
import java.util.Set;
import java.util.stream.Collectors;

import static fortscale.accumulator.translator.BaseAccumulatedFeatureTranslator.DAILY_COLLECTION_SUFFIX;
import static fortscale.accumulator.translator.BaseAccumulatedFeatureTranslator.HOURLY_COLLECTION_SUFFIX;

/**
 * Created by barak_schuster on 10/9/16.
 */
public class AccumulatorStoreUtil {
    public static Set<String> getACMExistingCollections(MongoTemplate mongoTemplate,String collectionNameRegex) {
        return mongoTemplate.getCollectionNames().stream().filter(x -> x.matches(collectionNameRegex)).collect(Collectors.toSet());
    }

    public static long getRetentionTimeInDays(String featureName, Period acmDailyEventRetentionDuration, Period acmHourlyRetentionDuration) {
        long retentionTimeInDays;
        if(featureName.endsWith(DAILY_COLLECTION_SUFFIX))
        {
            retentionTimeInDays = acmDailyEventRetentionDuration.getDays();
        }
        else if (featureName.endsWith(HOURLY_COLLECTION_SUFFIX))
        {
            retentionTimeInDays = acmHourlyRetentionDuration.getDays();
        }
        else
        {
            throw new RuntimeException("unsupported feature name, should end with hourly/daily");
        }
        return retentionTimeInDays;
    }
}
