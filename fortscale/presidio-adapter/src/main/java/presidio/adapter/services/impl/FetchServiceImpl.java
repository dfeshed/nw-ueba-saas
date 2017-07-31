package presidio.adapter.services.impl;

import fortscale.common.general.PresidioSchemas;
import fortscale.utils.logging.Logger;
import presidio.adapter.services.api.FetchService;
import presidio.adapter.services.api.Fetcher;

import java.time.Instant;
import java.util.List;
import java.util.Map;


public class FetchServiceImpl implements FetchService {
    private final Logger logger = Logger.getLogger(FetchServiceImpl.class);

    private Map<PresidioSchemas, Fetcher> fetchers;

    public FetchServiceImpl(Map<PresidioSchemas, Fetcher> fetchers) {
        this.fetchers = fetchers;
    }

    @Override
    public List<String[]> fetch(PresidioSchemas presidioSchemas, Instant startTime, Instant endTime) throws Exception {
        logger.info("fetching presidioSchemas {} from start time {} to end time {}.", presidioSchemas, startTime, endTime); //todo: can we have timezone issues?
        final Fetcher fetcher = fetchers.get(presidioSchemas);
        if (fetcher == null) {
            logger.error("There's no fetcher for presidioSchemas {}. Supported datasources are {}", fetchers.keySet());
            throw new Exception("Unsupported presidioSchemas: " + presidioSchemas);
        }

        try {
            return fetcher.fetch(presidioSchemas, startTime, endTime);
        } catch (Exception e) {
            logger.warn("fetch failed and we don't retry for now");
            //todo: how do we handle? maybe retry?
            throw e;
        }

    }
}


