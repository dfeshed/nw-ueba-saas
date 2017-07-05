package presidio.adapter.spring;

import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

/**
 * Created by shays on 26/06/2017.
 */
@Import({MongoConfig.class, AdapterConfig.class})
public class AdapterConfigProduction {

    @Autowired
    private PresidioExecutionService adapterExecutionService;

}
