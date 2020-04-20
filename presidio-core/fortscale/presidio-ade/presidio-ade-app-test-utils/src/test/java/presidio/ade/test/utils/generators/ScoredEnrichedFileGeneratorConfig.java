package presidio.ade.test.utils.generators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStoreMongoConfig;
import presidio.ade.test.utils.converters.FileRaw2EnrichedConverter;
import presidio.ade.test.utils.converters.FileRaw2ScoredEnrichedConverter;

/**
 * Created by barak_schuster on 15/08/2017.
 */
@Configuration
@Import(ScoredEnrichedDataStoreMongoConfig.class)
public class ScoredEnrichedFileGeneratorConfig {
    @Autowired
    private ScoredEnrichedDataStore scoredEnrichedDataStore;

    @Bean
    public ScoredEnrichedFileGenerator scoredEnrichedFileGenerator()
    {
        FileRaw2ScoredEnrichedConverter fileRaw2ScoredEnrichedConverter = getFileRaw2ScoredEnrichedConverter();
        return new ScoredEnrichedFileGenerator(scoredEnrichedDataStore,fileRaw2ScoredEnrichedConverter);
    }

    private FileRaw2ScoredEnrichedConverter getFileRaw2ScoredEnrichedConverter() {
        FileRaw2EnrichedConverter fileRaw2EnrichedConverter = new FileRaw2EnrichedConverter();
        return new FileRaw2ScoredEnrichedConverter(fileRaw2EnrichedConverter);
    }
}
