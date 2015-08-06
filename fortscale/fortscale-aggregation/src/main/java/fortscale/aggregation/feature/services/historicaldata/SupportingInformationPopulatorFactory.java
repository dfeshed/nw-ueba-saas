package fortscale.aggregation.feature.services.historicaldata;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author gils
 * Date: 05/08/2015
 */
@Service
public class SupportingInformationPopulatorFactory implements ApplicationContextAware {

    private static final String COUNT_AGGREGATION_FUNC = "Count";
    private static final String HOURLY_COUNT_BY_GROUP_BY_DAY_OF_WEEK_AGGREGATION_FUNC = "hourlyCountGroupByDayOfWeek";

    private ApplicationContext applicationContext;

    public SupportingInformationDataPopulator createSupportingInformationPopulator(String contextType, String dataEntity, String featureName, String aggregationFunction) {
        if (COUNT_AGGREGATION_FUNC.equalsIgnoreCase(aggregationFunction.toLowerCase())) {
            SupportingInformationDataCountPopulator supportingInformationDataCountPopulator = (SupportingInformationDataCountPopulator) applicationContext.getBean("supportingInformationDataCountPopulator", contextType, dataEntity, featureName);

            return supportingInformationDataCountPopulator;
        }
        else if (HOURLY_COUNT_BY_GROUP_BY_DAY_OF_WEEK_AGGREGATION_FUNC.equalsIgnoreCase(aggregationFunction)) {
            SupportingInformationHeatMapDataPopulator supportingInformationHeatMapDataPopulator = (SupportingInformationHeatMapDataPopulator) applicationContext.getBean("supportingInformationHeatMapDataPopulator", contextType, dataEntity, featureName);

            return supportingInformationHeatMapDataPopulator;
        }
        else {
            throw new UnsupportedOperationException("Aggregation function " + aggregationFunction + " is not supported");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
