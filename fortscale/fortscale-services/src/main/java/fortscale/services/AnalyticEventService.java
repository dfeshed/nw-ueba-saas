package fortscale.services;


import fortscale.domain.core.AnalyticEvent;

import java.security.InvalidParameterException;
import java.util.List;


public interface AnalyticEventService {
    /**
     * Takes the body from the POST request, parses it into a list of AnalyticEvent objects,
     * and inserts it into analytics repository
     *
     * @param body The received post body
     * @throws InvalidParameterException
     */
    public void insertAnalyticEvents(String body) throws InvalidParameterException;

    /**
     * Takes a list of AnalyticEvent instances and inserts them into analytics repository
     *
     * @param analyticEvents A list of AnalyticEvent instances
     * @throws InvalidParameterException
     */
    public void insertAnalyticEvents(List<AnalyticEvent> analyticEvents) throws InvalidParameterException;
}
