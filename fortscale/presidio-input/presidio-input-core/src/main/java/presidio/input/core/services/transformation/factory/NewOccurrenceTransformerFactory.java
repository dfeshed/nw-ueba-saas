package presidio.input.core.services.transformation.factory;

import fortscale.domain.lastoccurrenceinstant.reader.LastOccurrenceInstantReader;
import fortscale.domain.lastoccurrenceinstant.reader.LastOccurrenceInstantReaderCacheConfiguration;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import presidio.input.core.services.transformation.transformer.NewOccurrenceTransformer;
import presidio.input.core.services.transformation.transformer.Transformer;

@Component
@Import(LastOccurrenceInstantReaderCacheConfiguration.class)
public class NewOccurrenceTransformerFactory extends AbstractServiceAutowiringFactory<Transformer> {
    private final LastOccurrenceInstantReader lastOccurrenceInstantReader;

    @Autowired
    public NewOccurrenceTransformerFactory(
            @Qualifier("lastOccurrenceInstantReaderCache") LastOccurrenceInstantReader lastOccurrenceInstantReader) {

        this.lastOccurrenceInstantReader = lastOccurrenceInstantReader;
    }

    @Override
    public String getFactoryName() {
        return NewOccurrenceTransformerConf.NEW_OCCURRENCE_TRANSFORMER_FACTORY_NAME;
    }

    @Override
    public Transformer getProduct(FactoryConfig factoryConfig) {
        NewOccurrenceTransformerConf newOccurrenceTransformerConf = (NewOccurrenceTransformerConf)factoryConfig;
        return new NewOccurrenceTransformer(
                lastOccurrenceInstantReader,
                newOccurrenceTransformerConf.getSchema(),
                newOccurrenceTransformerConf.getEntityType(),
                newOccurrenceTransformerConf.getInstantFieldName(),
                newOccurrenceTransformerConf.getExpirationDelta(),
                newOccurrenceTransformerConf.getBooleanFieldName());
    }
}
