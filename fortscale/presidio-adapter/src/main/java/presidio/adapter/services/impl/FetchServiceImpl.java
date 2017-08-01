package presidio.adapter.services.impl;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import presidio.adapter.services.api.FetchService;
import presidio.adapter.services.api.Fetcher;

import java.time.Instant;
import java.util.List;
import java.util.Map;


public class FetchServiceImpl implements FetchService {
    private final Logger logger = Logger.getLogger(FetchServiceImpl.class);

    private Map<Schema, Fetcher> fetchers;

    public FetchServiceImpl(Map<Schema, Fetcher> fetchers) {
        this.fetchers = fetchers;
    }

    @Override
    public List<String[]> fetch(Schema schema, Instant startTime, Instant endTime) throws Exception {
        logger.info("fetching schema {} from start time {} to end time {}.", schema, startTime, endTime); //todo: can we have timezone issues?
        final Fetcher fetcher = fetchers.get(schema);
        if (fetcher == null) {
            logger.error("There's no fetcher for schema {}. Supported datasources are {}", fetchers.keySet());
            throw new Exception("Unsupported schema: " + schema);
        }

        try {
            return fetcher.fetch(schema, startTime, endTime);
        } catch (Exception e) {
            logger.warn("fetch failed and we don't retry for now");
            //todo: how do we handle? maybe retry?
            throw e;
        }

    }
}


