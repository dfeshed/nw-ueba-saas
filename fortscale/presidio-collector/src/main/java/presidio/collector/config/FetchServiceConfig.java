package presidio.collector.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.collector.Datasource;
import presidio.collector.services.api.CsvFileFetcher;
import presidio.collector.services.api.FetchService;
import presidio.collector.services.api.FetchServiceImpl;
import presidio.collector.services.api.Fetcher;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class FetchServiceConfig {

    @Value("${fortscale.collector.csvfetcher.csvfilesfolderpath}")
    private String csvFilesFolderPath;

    @Bean
    FetchService fetchService() {
        Map<Datasource, Fetcher> fetchers = new HashMap<>();
        fetchers.put(Datasource.DLPFILE, new CsvFileFetcher(csvFilesFolderPath));
        return new FetchServiceImpl(fetchers);
    }
}
