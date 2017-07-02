package presidio.adapter.config;


import fortscale.common.general.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.adapter.services.api.FetchService;
import presidio.adapter.services.api.Fetcher;
import presidio.adapter.services.impl.CsvFileFetcher;
import presidio.adapter.services.impl.FetchServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class FetchServiceConfig {

    @Value("${fortscale.adapter.csvfetcher.csvfilesfolderpath}")
    private String csvFilesFolderPath;

    @Bean
    public FetchService fetchService() {
        Map<DataSource, Fetcher> fetchers = new HashMap<>();
        fetchers.put(DataSource.DLPFILE, new CsvFileFetcher(csvFilesFolderPath, StandardCharsets.UTF_8, ','));
        return new FetchServiceImpl(fetchers);
    }
}
