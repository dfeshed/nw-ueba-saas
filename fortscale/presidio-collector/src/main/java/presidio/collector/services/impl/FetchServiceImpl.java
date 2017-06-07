package presidio.collector.services.impl;

import fortscale.common.general.DataSource;
import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;
import presidio.collector.services.api.FetchService;
import presidio.collector.services.api.Fetcher;

import java.util.List;
import java.util.Map;


public class FetchServiceImpl implements FetchService {
    private final Logger logger = Logger.getLogger(FetchServiceImpl.class);

    private Map<DataSource, Fetcher> fetchers;

    public FetchServiceImpl(Map<DataSource, Fetcher> fetchers) {
        this.fetchers = fetchers;
    }

    @Override
    public List<String[]> fetch(DataSource dataSource, long startTime, long endTime) throws Exception {
        logger.info("fetching datasource {} from start time {}[{}] to end time {}[{}].", dataSource, new DateTime(startTime), startTime, new DateTime(endTime), endTime); //todo: can we have timezone issues?
        final Fetcher fetcher = fetchers.get(dataSource);
        if (fetcher == null) {
            logger.error("There's no fetcher for datasource {}. Supported datasources are {}", fetchers.keySet());
            throw new Exception("Unsupported datasource: " + dataSource);
        }

        try {
            return fetcher.fetch(dataSource, startTime, endTime);
        } catch (Exception e) {
            logger.warn("fetch failed and we don't retry for now");
            //todo: how do we handle? maybe retry?
            throw e;
        }

    }


}


