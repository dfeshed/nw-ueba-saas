package presidio.input.core.services.transformation.factory;

import fortscale.domain.lastoccurrenceinstant.LastOccurrenceInstantReader;
import fortscale.domain.lastoccurrenceinstant.LastOccurrenceInstantReaderConfiguration;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import presidio.input.core.services.transformation.transformer.NewOccurrenceTransformer;
import presidio.input.core.services.transformation.transformer.Transformer;

@Component
@Import(LastOccurrenceInstantReaderConfiguration.class)
public class NewOccurrenceTransformerFactory extends AbstractServiceAutowiringFactory<Transformer> {
    private final LastOccurrenceInstantReader lastOccurrenceInstantReader;

    @Autowired
    public NewOccurrenceTransformerFactory(LastOccurrenceInstantReader lastOccurrenceInstantReader) {
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
