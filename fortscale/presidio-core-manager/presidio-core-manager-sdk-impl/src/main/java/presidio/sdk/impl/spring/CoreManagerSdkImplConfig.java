package presidio.sdk.impl.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import presidio.input.sdk.impl.services.PresidioInputSdkMongoImpl;
import presidio.sdk.impl.services.CoreManagerServiceImpl;


/**
 * Created by shays on 17/05/2017.
 */
@Configuration
public class CoreManagerSdkImplConfig {


    @Bean
    CoreManagerServiceImpl coreManagerService(){


        //return new CoreManagerServiceImpl(new PresidioInputSdkMongoImpl());
        return new CoreManagerServiceImpl(null);
    }

}
