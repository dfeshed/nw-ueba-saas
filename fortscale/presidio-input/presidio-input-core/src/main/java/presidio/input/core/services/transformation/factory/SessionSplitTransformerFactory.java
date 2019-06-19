package presidio.input.core.services.transformation.factory;


import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import presidio.input.core.services.transformation.transformer.SessionSplitTransformer.SessionSplitTransformer;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Duration;

@SuppressWarnings("unused")
@Component
public class SessionSplitTransformerFactory extends AbstractServiceAutowiringFactory<Transformer> {

    @Autowired
    private PresidioInputPersistencyService inputPersistencyService;
    @Value("#{T(java.time.Duration).parse('${split.transformer.intervel:P2D}')}")
    private Duration interval;
    @Value("${split.transformer.page.size:1000}")
    private Integer pageSize;

    @Override
    public String getFactoryName() {
        return SessionSplitTransformerConf.SESSION_SPLIT_TRANSFORMER_FACTORY_NAME;
    }

    @Override
    public SessionSplitTransformer getProduct(FactoryConfig factoryConfig) {
        SessionSplitTransformerConf config = (SessionSplitTransformerConf)factoryConfig;
        return new SessionSplitTransformer(inputPersistencyService, interval, config.getEndDate(),
                config.getSchema(), pageSize);
    }
}
