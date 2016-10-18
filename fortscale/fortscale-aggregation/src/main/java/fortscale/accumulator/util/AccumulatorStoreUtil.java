package fortscale.accumulator.util;

import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Period;
import java.util.Set;
import java.util.stream.Collectors;

import static fortscale.accumulator.translator.BaseAccumulatedFeatureTranslator.DAILY_FEATURE_SUFFIX;
import static fortscale.accumulator.translator.BaseAccumulatedFeatureTranslator.HOURLY_FEATURE_SUFFIX;
import static fortscale.utils.time.TimeUtils.getAmountOfDaysInPeriod;

/**
 * Created by barak_schuster on 10/9/16.
 */
public class AccumulatorStoreUtil {
    public static Set<String> getACMExistingCollections(MongoTemplate mongoTemplate,String collectionNameRegex) {
        return mongoTemplate.getCollectionNames().stream().filter(x -> x.matches(collectionNameRegex)).collect(Collectors.toSet());
    }

    public static long getRetentionTimeInDays(String featureName, Period acmDailyEventRetentionDuration, Period acmHourlyRetentionDuration) {
        long retentionTimeInDays;
        if(featureName.endsWith(DAILY_FEATURE_SUFFIX))
        {
            retentionTimeInDays = getAmountOfDaysInPeriod(acmDailyEventRetentionDuration);
        }
        else if (featureName.endsWith(HOURLY_FEATURE_SUFFIX))
        {
            retentionTimeInDays = getAmountOfDaysInPeriod(acmHourlyRetentionDuration);
        }
        else
        {
            throw new RuntimeException("unsupported feature name, should end with hourly/daily");
        }
        return retentionTimeInDays;
    }
}
