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

    // TODO use a static map in the bean
    private static final String SUPPORTING_INFORMATION_DATA_COUNT_POPULATOR_BEAN = "supportingInformationCountPopulator";
    private static final String SUPPORTING_INFORMATION_DATA_HOURLY_COUNT_GROUPBY_DAY_OF_WEEK_POPULATOR_BEAN = "supportingInformationHourlyCountGroupByDayOfWeekPopulator";

    private ApplicationContext applicationContext;

    public SupportingInformationDataPopulator createSupportingInformationPopulator(String contextType, String dataEntity, String featureName, String aggregationFunction) {
        if (SupportingInformationAggrFunc.Count.name().equalsIgnoreCase(aggregationFunction.toLowerCase())) {
            SupportingInformationCountPopulator supportingInformationDataCountPopulator = (SupportingInformationCountPopulator) applicationContext.getBean(SUPPORTING_INFORMATION_DATA_COUNT_POPULATOR_BEAN, contextType, dataEntity, featureName);

            return supportingInformationDataCountPopulator;
        }
        else if (SupportingInformationAggrFunc.HourlyCountGroupByDayOfWeek.name().equalsIgnoreCase(aggregationFunction)) {
            SupportingInformationHourlyCountGroupByDayOfWeekPopulator supportingInformationDataHourlyCountGroupByDayOfWeekPopulator = (SupportingInformationHourlyCountGroupByDayOfWeekPopulator) applicationContext.getBean(SUPPORTING_INFORMATION_DATA_HOURLY_COUNT_GROUPBY_DAY_OF_WEEK_POPULATOR_BEAN, contextType, dataEntity, featureName);

            return supportingInformationDataHourlyCountGroupByDayOfWeekPopulator;
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
