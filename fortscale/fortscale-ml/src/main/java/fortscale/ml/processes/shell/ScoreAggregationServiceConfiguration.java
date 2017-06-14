package fortscale.ml.processes.shell;

import fortscale.services.config.ParametersValidationServiceConfig;
import fortscale.services.parameters.ParametersValidationService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreConfig;

import java.time.Instant;

import static fortscale.common.general.CommonStrings.*;

/**
 * Created by barak_schuster on 6/14/17.
 */
@Configuration
@Import({EnrichedDataStoreConfig.class,
        ParametersValidationServiceConfig.class
})
public class ScoreAggregationServiceConfiguration {

    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    private ParametersValidationService parametersValidationService;

    @Bean
    public CommandLineRunner commandLineRunner() {

        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                // todo: all of this will be change when using spring shell
                String dataSourceParam = parametersValidationService.getMandatoryParamAsString(COMMAND_LINE_DATA_SOURCE_FIELD_NAME, strings);
                Instant startTimeParam = Instant.parse(parametersValidationService.getMandatoryParamAsString(COMMAND_LINE_START_DATE_FIELD_NAME, strings));
                Instant endTimeParam = Instant.parse(parametersValidationService.getMandatoryParamAsString(COMMAND_LINE_END_DATE_FIELD_NAME, strings));
                FixedDurationStrategy fixedDurationStrategy = FixedDurationStrategy.fromSeconds((long)Float.parseFloat((parametersValidationService.getMandatoryParamAsString(COMMAND_LINE_FIXED_DURATION_FIELD_NAME, strings))));
                ScoreAggregationsService scoreAggregationsService = new ScoreAggregationsService(fixedDurationStrategy, enrichedDataStore);
                TimeRange timeRange = new TimeRange(startTimeParam, endTimeParam);
                scoreAggregationsService.execute(timeRange,dataSourceParam);
            }
        };
    }


}
