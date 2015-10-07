package fortscale.aggregation.feature.services.historicaldata.populators;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationException;
import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.domain.core.Evidence;
import fortscale.domain.historical.data.SupportingInformationKey;
import fortscale.domain.historical.data.SupportingInformationSingleKey;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryField;
import fortscale.services.dataqueries.querydto.Term;
import fortscale.services.dataqueries.querygenerators.DataQueryRunner;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Supporting information populator class for count-based aggregations
 *
 * @author gils
 * Date: 05/08/2015
 */

@Component
@Scope("prototype")
public class SupportingInformationCountPopulator extends SupportingInformationHistogramBySingleEventsPopulator {

    private static Logger logger = Logger.getLogger(SupportingInformationCountPopulator.class);

    private static final String FEATURE_HISTOGRAM_SUFFIX = "histogram";

    private static final String VPN_GEO_HOPPING_ANOMALY_TYPE = "vpn_geo_hopping";

    public SupportingInformationCountPopulator(String contextType, String dataEntity, String featureName) {
        super(contextType, dataEntity, featureName);
    }

    @Override
    protected Map<SupportingInformationKey, Double> createSupportingInformationHistogram(String contextValue, long evidenceEndTime, Integer timePeriodInDays) {
        List<FeatureBucket> featureBuckets = fetchRelevantFeatureBuckets(contextValue, evidenceEndTime, timePeriodInDays);

        if (featureBuckets.isEmpty()) {
            throw new SupportingInformationException("Could not find any relevant bucket for histogram creation");
        }

        Map<SupportingInformationKey, Double> lastDayMap = createLastDayBucket(getNormalizedContextType(contextType), contextValue, evidenceEndTime, dataEntity);

        return createSupportingInformationHistogram(featureBuckets, lastDayMap);
    }

    protected Map<SupportingInformationKey, Double> createSupportingInformationHistogram(List<FeatureBucket> featureBuckets, Map<SupportingInformationKey, Double> lastDayMap) {
        Map<SupportingInformationKey, Double> histogramKeyObjectMap = new HashMap<>();

        for (FeatureBucket featureBucket : featureBuckets) {
            String normalizedFeatureName = getNormalizedFeatureName(featureName);

            Feature feature = featureBucket.getAggregatedFeatures().get(normalizedFeatureName);

            if (feature == null) {
                logger.warn("Could not find feature with name {} in bucket with ID {}", normalizedFeatureName, featureBucket.getBucketId());
                continue;
            }

            Object featureValue = feature.getValue();

            if (featureValue instanceof GenericHistogram) {
                Map<String, Double> histogramMap = ((GenericHistogram) featureValue).getHistogramMap();

                for (Map.Entry<String, Double> histogramEntry : histogramMap.entrySet()) {
                    Double currValue = histogramEntry.getValue();

                    SupportingInformationKey supportingInformationKey = new SupportingInformationSingleKey(histogramEntry.getKey());

                    Double currHistogramValue = (histogramKeyObjectMap.get(supportingInformationKey) != null ? histogramKeyObjectMap.get(supportingInformationKey) : 0);

                    histogramKeyObjectMap.put(supportingInformationKey, currHistogramValue + currValue);
                }
            } else {
                logger.error("Cannot find histogram data for feature {} in bucket id {}", normalizedFeatureName, featureBucket.getBucketId());
            }
        }
        histogramKeyObjectMap.putAll(lastDayMap);
        return histogramKeyObjectMap;
    }

    /**
     *
     * @param normalizedContextType - The entity normalized field - i.e normalized_username
     * @param contextValue - The value of the normalized context field - i.e test@somebigcomapny.com
     * @param endTime - The time of the anomaly
     * @param dataEntity - The data entity - i.e kerberos_logins
     * @return
     */
    private Map<SupportingInformationKey, Double> createLastDayBucket(String normalizedContextType, String contextValue, long endTime, String dataEntity) {
        //TODO: remove last day and create a new FeatureBucket from Impala
        //example of query for destination_machine in authentication_score table:
        //select normalized_username, normalized_dst_machine, count( normalized_dst_machine)  from authenticationscores where normalized_username = 'mac83a@somebigcompany.com' group by normalized_dst_machine,normalized_username;

//        FeatureBucket featureBucket = new FeatureBucket();

        String QueryFieldsAsCSV = normalizedContextType.concat(",").concat(featureName);

        //add conditions
        List<Term> termsMap = new ArrayList<>();
        Term contextTerm = getTheContextTerm(normalizedContextType,contextValue);
        if (contextTerm != null) {
            termsMap.add(contextTerm);
        }
        Term dateRangeTerm = getDateRangeTerm(TimestampUtils.toStartOfDay(endTime), endTime);
        if (dateRangeTerm != null) {
            termsMap.add(dateRangeTerm);
        }


        DataQueryDTO dataQueryObject = dataQueryHelper.createDataQuery(dataEntity, QueryFieldsAsCSV, termsMap, null, -1);

        //Create the Group By clause without alias
        List<DataQueryField> groupByFields = dataQueryHelper.createGrouByClause(QueryFieldsAsCSV,dataEntity,false);
        dataQueryHelper.setGroupByClause(groupByFields,dataQueryObject);

        // Create the count(*) field:
        HashMap<String, String> countParams = new HashMap<>();
        countParams.put("all", "true");
        DataQueryField countField = dataQueryHelper.createCountFunc ("countField",countParams);
        dataQueryHelper.setFuncFieldToQuery(countField,dataQueryObject);

        List<Map<String, Object>> queryList;
        try {
            DataQueryRunner dataQueryRunner = dataQueryRunnerFactory.getDataQueryRunner(dataQueryObject);
            // Generates query
            String query = dataQueryRunner.generateQuery(dataQueryObject);
            logger.debug("Running the query: {}", query);
            // execute Query
            queryList = dataQueryRunner.executeQuery(query);
            logger.debug(queryList.toString());
            return buildLastDayMap(queryList);
        } catch (InvalidQueryException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    private Map<SupportingInformationKey, Double> buildLastDayMap(List<Map<String, Object>> queryList) {
        Map<SupportingInformationKey, Double> lastDayMap = new HashMap<>();
        for (Map<String, Object> bucketMap : queryList){
            lastDayMap.put(new SupportingInformationSingleKey(""), ((Long)bucketMap.get("countField")).doubleValue());
        }
        return lastDayMap;
    }

    private Term getDateRangeTerm(long startTime, long endTime) {
        return dataQueryHelper.createDateRangeTerm(dataEntity, TimestampUtils.convertToSeconds(startTime), TimestampUtils.convertToSeconds(endTime));
    }

    private Term getTheContextTerm(String normalizedContextType, String contextValue)
    {
        Term term = null;
		switch (normalizedContextType)
		{
			case "normalized_username" :
      			term = dataQueryHelper.createUserTerm(dataEntity, contextValue);
				break;
			default:
				break;

		}
        return term;

    }

    @Override
    protected String getNormalizedContextType(String contextType) {
        return contextType;
    }

    @Override
    String getNormalizedFeatureName(String featureName) {
        return String.format("%s_%s", featureName, FEATURE_HISTOGRAM_SUFFIX);
    }

    @Override
    SupportingInformationKey createAnomalyHistogramKey(Evidence evidence, String featureName) {
        String anomalyValue = extractAnomalyValue(evidence, featureName);

        return new SupportingInformationSingleKey(anomalyValue);
    }

    @Override
    protected boolean isAnomalyIndicationRequired(Evidence evidence) {
        return !VPN_GEO_HOPPING_ANOMALY_TYPE.equals(evidence.getAnomalyTypeFieldName());
    }
}
