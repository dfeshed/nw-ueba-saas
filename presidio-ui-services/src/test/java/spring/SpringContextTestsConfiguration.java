package spring;


import fortscale.utils.configurations.ConfigrationServerClientUtilsImpl;

import fortscale.utils.spring.SpringPropertiesUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;


import java.io.IOException;
import java.util.Properties;


@Configuration

public class SpringContextTestsConfiguration  {



    @Bean
    ConfigrationServerClientUtilsImpl configrationServerClientUtilsMock() throws IOException {
        return new ConfigrationServerClientUtilsMock(testProperties());
    }

    @Bean
    Properties testProperties(){

        return springPropertiesUtil().getProperties();

    }

    @Bean SpringPropertiesUtil springPropertiesUtil() {
        SpringPropertiesUtil springPropertiesUtil = new SpringPropertiesUtil();
        springPropertiesUtil.setSystemPropertiesModeName("SYSTEM_PROPERTIES_MODE_OVERRIDE");
        springPropertiesUtil.setOrder(Ordered.HIGHEST_PRECEDENCE);
        springPropertiesUtil.setIgnoreUnresolvablePlaceholders(true);
        springPropertiesUtil.setLocalOverride(true);
        springPropertiesUtil.setLocation(new ClassPathResource("test.properties"));
        return springPropertiesUtil;
    }


}
