package fortscale.aggregation.feature.services.historicaldata;

import fortscale.aggregation.feature.services.historicaldata.populators.*;
import fortscale.common.event.NotificationAnomalyType;
import fortscale.domain.core.EvidenceType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * Factory class to create supporting information populators.
 * The creation of populator is based mostly on the aggregation function but can also take into consideration
 * the context type (i.e. user, source machine etc.), feature and data entity (ssh, kerberos etc.) to allow
 * different populators with different characteristics
 *
 *
 * @author gils
 * Date: 05/08/2015
 */
@Service
public class SupportingInformationPopulatorFactory implements ApplicationContextAware {


    private static final String SUPPORTING_INFORMATION_DATA_COUNT_POPULATOR_BEAN = "supportingInformationCountPopulator";
    private static final String SUPPORTING_INFORMATION_DATA_HOURLY_COUNT_GROUPBY_DAY_OF_WEEK_POPULATOR_BEAN = "supportingInformationHourlyCountGroupByDayOfWeekPopulator";
    private static final String SUPPORTING_INFORMATION_DISTINCT_EVENTS_BY_TIME_POPULATOR = "supportingInformationDistinctEventsByTimePopulator";
    private static final String SUPPORTING_INFORMATION_COUNT_BY_TIME_POPULATOR = "supportingInformationCountByTimePopulator";
    private static final String SUPPORTING_INFORMATION_QUERY_VPN_SESSION_POPULATOR_BEAN = "supportingInformationVPNSessionPopulator";
    private static final String SUPPORTING_INFORMATION_QUERY_VPN_OVERLAPPING_SESSION_POPULATOR_BEAN = "supportingInformationVPNOverlappingSessionPopulator";
	private static final String SUPPORTING_INFORMATION_QUERY_VPN_LATERAL_MOVEMENT_POPULATOR_BEAN = "supportingInformationVPNLateralMovementPopulator";

    private static final String VPN_OVERLAPPING_SESSION = NotificationAnomalyType.VPN_USER_CREDS_SHARE.getType();
	private static final String VPN_LATERAL_MOVEMENT = NotificationAnomalyType.VPN_LATERAL_MOVEMENT.getType();

    private ApplicationContext applicationContext;

    public SupportingInformationDataPopulator createSupportingInformationPopulator(EvidenceType evidenceType, String contextType, String dataEntity, String featureName, String aggregationFunction) {
        if (EvidenceType.AnomalySingleEvent == evidenceType || EvidenceType.Notification == evidenceType) {
            return createSingleEventPopulator(contextType, dataEntity, featureName, aggregationFunction);
        }
        else if (EvidenceType.AnomalyAggregatedEvent == evidenceType) {
            return createAggregatedEventPopulator(contextType, dataEntity, featureName, aggregationFunction);
        }
        else {
            throw new UnsupportedOperationException("Evidence type " + evidenceType + " is not supported ");
        }
    }

    public SupportingInformationCountPopulator createSupportingInformationPopulator(String contextType, String dataEntity, String featureName, String aggregationFunction) {
        return (SupportingInformationCountPopulator) applicationContext.getBean(SUPPORTING_INFORMATION_DATA_COUNT_POPULATOR_BEAN, contextType, dataEntity, featureName);
    }

    private SupportingInformationDataPopulator createAggregatedEventPopulator(String contextType, String dataEntity, String featureName, String aggregationFunction) {
        if (SupportingInformationAggrFunc.DistinctEventsByTime.name().equalsIgnoreCase(aggregationFunction)) {
            return (SupportingInformationDistinctEventsByTimePopulator) applicationContext.getBean(SUPPORTING_INFORMATION_DISTINCT_EVENTS_BY_TIME_POPULATOR, contextType, dataEntity, featureName);
        }
        else {
            throw new UnsupportedOperationException("Aggregation function " + aggregationFunction + " is not supported");
        }
    }

    private SupportingInformationDataPopulator createSingleEventPopulator(String contextType, String dataEntity, String featureName, String aggregationFunction) {
        if (SupportingInformationAggrFunc.Count.name().equalsIgnoreCase(aggregationFunction)) {
            return (SupportingInformationCountPopulator) applicationContext.getBean(SUPPORTING_INFORMATION_DATA_COUNT_POPULATOR_BEAN, contextType, dataEntity, featureName);
        } else if (SupportingInformationAggrFunc.HourlyCountGroupByDayOfWeek.name().equalsIgnoreCase(aggregationFunction)) {
            return (SupportingInformationHourlyCountGroupByDayOfWeekPopulator) applicationContext.getBean(SUPPORTING_INFORMATION_DATA_HOURLY_COUNT_GROUPBY_DAY_OF_WEEK_POPULATOR_BEAN, contextType, dataEntity, featureName);
        }
        // TODO shouldn't we use the feature name instead?
        else if (SupportingInformationAggrFunc.VPNSession.name().equalsIgnoreCase(aggregationFunction)) {
            return (SupportingInformationVPNSessionPopulator)applicationContext.getBean(SUPPORTING_INFORMATION_QUERY_VPN_SESSION_POPULATOR_BEAN);
        }
        else if (SupportingInformationAggrFunc.TimeIntervals.name().equalsIgnoreCase(aggregationFunction)) {
            if (VPN_OVERLAPPING_SESSION.equalsIgnoreCase(featureName)) {
                return (SupportingInformationVPNOverlappingSessionPopulator)applicationContext.
						getBean(SUPPORTING_INFORMATION_QUERY_VPN_OVERLAPPING_SESSION_POPULATOR_BEAN);
            } else if (VPN_LATERAL_MOVEMENT.equalsIgnoreCase(featureName)) {
				return (SupportingInformationVPNLateralMovementPopulator) applicationContext.
						getBean(SUPPORTING_INFORMATION_QUERY_VPN_LATERAL_MOVEMENT_POPULATOR_BEAN);
			}
        }
        throw new UnsupportedOperationException("Could not find supporting information populator for feature name " + featureName + " with aggregation function " + aggregationFunction);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
