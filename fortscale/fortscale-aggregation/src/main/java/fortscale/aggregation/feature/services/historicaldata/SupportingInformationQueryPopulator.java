package fortscale.aggregation.feature.services.historicaldata;

import fortscale.domain.core.SupportingInformationData;
import fortscale.domain.events.VpnSession;
import fortscale.domain.histogram.HistogramKey;
import fortscale.domain.histogram.HistogramSingleKey;
import fortscale.services.event.VpnService;
import fortscale.utils.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for Mongo queries populator
 *
 * @author Amir Keren
 * Date: 18/08/2015
 */
public abstract class SupportingInformationQueryPopulator implements SupportingInformationDataPopulator {

    @Override
    public SupportingInformationData createSupportingInformationData(String contextValue, long evidenceEndTime,
                                                                     int timePeriodInDays) {
        return createSupportingInformationData(contextValue, evidenceEndTime, timePeriodInDays, null);
    }

}