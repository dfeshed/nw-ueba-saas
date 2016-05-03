package fortscale.utils.standardProcess;

import fortscale.utils.spring.MainProcessPropertiesConfigurer;
import fortscale.utils.standardProcess.pidService.PidService;
import fortscale.utils.standardProcess.pidService.config.PidServiceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.File;
import java.util.Properties;

import static org.mockito.Mockito.mock;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class PidServiceTest {

    @Import({PidServiceConfig.class})
    @Configuration
    static class ContextConfiguration {
        @Bean
        public static MainProcessPropertiesConfigurer mainProcessPropertiesConfigurer() {

            String[] overridingFileList = null;

            Properties properties = new Properties();
            properties.put("fortscale.pid.folder","/var/run/fortscale");

            MainProcessPropertiesConfigurer configurer = new MainProcessPropertiesConfigurer(overridingFileList, properties);

            return configurer;
        }
    }

    @Autowired
    PidService pidService;

    @Test
    public void shouldDoStuff() {
        File file = mock(File.class);
        Mockito.when(file.exists()).thenReturn(true);

        Mockito.spy(pidService);
        pidService.process("1234",file);
        pidService.writePidFile("test");
    }

}
