package fortscale.aggregation.feature.services;

/**
 * @author gils
 * Date: 05/08/2015
 */
public class SupportingInformationPopulatorFactory {

    private static final String COUNT_AGGREGATION_FUNC = "Count";
    private static final String HOURLY_COUNT_BY_GROUP_BY_DAY_OF_WEEK_AGGREGATION_FUNC = "hourlyCountGroupByDayOfWeek";

    public static SupportingInformationPopulator createSupportingInformationPopulator(String aggregationFunction) {
        if (COUNT_AGGREGATION_FUNC.equalsIgnoreCase(aggregationFunction.toLowerCase())) {
            return new SupportingInformationBasicPopulator();
        }
        else if (HOURLY_COUNT_BY_GROUP_BY_DAY_OF_WEEK_AGGREGATION_FUNC.equalsIgnoreCase(aggregationFunction)) {
            return new SupportingInformationHeatMapPopulator();
        }
        else {
            throw new UnsupportedOperationException("Aggregation function " + aggregationFunction + " is not supported");
        }
    }
}
