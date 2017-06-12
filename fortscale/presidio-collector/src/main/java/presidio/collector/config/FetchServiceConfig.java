package presidio.collector.config;


import fortscale.common.general.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.collector.services.api.FetchService;
import presidio.collector.services.api.Fetcher;
import presidio.collector.services.impl.CsvFileFetcher;
import presidio.collector.services.impl.FetchServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class FetchServiceConfig {

    @Value("${fortscale.collector.csvfetcher.csvfilesfolderpath}")
    private String csvFilesFolderPath;

    @Bean
    public FetchService fetchService() {
        Map<DataSource, Fetcher> fetchers = new HashMap<>();
        fetchers.put(DataSource.DLPFILE, new CsvFileFetcher(csvFilesFolderPath, StandardCharsets.UTF_8, ','));
        return new FetchServiceImpl(fetchers);
    }
}
