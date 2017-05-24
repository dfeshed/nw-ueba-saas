package presidio.collector.services.api;

import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;
import presidio.collector.Datasource;

import java.util.List;
import java.util.Map;


public class FetchServiceImpl implements FetchService {
    private final Logger logger = Logger.getLogger(FetchServiceImpl.class);

    private Map<Datasource, Fetcher> fetchers;

    public FetchServiceImpl(Map<Datasource, Fetcher> fetchers) {
        this.fetchers = fetchers;
    }

    @Override
    public List<String[]> fetch(Datasource datasource, long startTime, long endTime) throws Exception {
        logger.info("fetching datasource {} from start time {}[{}] to end time {}[{}].", datasource, new DateTime(startTime), startTime, new DateTime(endTime), endTime); //todo: can we have timezone issues?
        final Fetcher fetcher = fetchers.get(datasource);
        if (fetcher == null) {
            logger.error("There's no fetcher for datasource {}. Supported datasources are {}", fetchers.keySet());
            throw new Exception("Unsupported datasource: " + datasource);
        }

        try {
            return fetcher.fetch(datasource, startTime, endTime);
        } catch (Exception e) {
            logger.warn("fetch failed and we don't retry for now");
            //todo: how do we handle? maybe retry?
            throw e;
        }

    }


}


