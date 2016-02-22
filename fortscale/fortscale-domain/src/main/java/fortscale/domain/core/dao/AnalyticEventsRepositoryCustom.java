package fortscale.domain.core.dao;

import fortscale.domain.core.AnalyticEvent;

import java.util.List;

/**
 * Created by avivs on 22/02/16.
 */
public interface AnalyticEventsRepositoryCustom {

    /**
     * Stores all analytic events
     *
     * @param analyticEvents A list of analytic events
     */
    public void insertAnalyticEvents(List<AnalyticEvent> analyticEvents);
}
