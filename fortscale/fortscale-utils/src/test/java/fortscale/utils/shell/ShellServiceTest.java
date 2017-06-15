package fortscale.utils.shell;

import fortscale.utils.process.standardProcess.StandardProcessService;
import fortscale.utils.process.standardProcess.StandardProcessServiceImpl;
import fortscale.utils.shell.service.ShellServiceImpl;
import fortscale.utils.shell.service.config.JLineShellComponentConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.shell.core.CommandResult;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Properties;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class ShellServiceTest {

    @Configuration
    @Import({JLineShellComponentConfig.class})
    @ComponentScan(value = "fortscale.utils.shell.testCommands")
    static public class StatSpringConfig {


        @Autowired
        JLineShellComponent shellComponent;

        @Bean
        StandardProcessService standardProcessService()
        {
            StandardProcessService standardProcessService = new StandardProcessServiceImpl();

            return standardProcessService;
        }
        @Bean
        public ShellServiceImpl shellService() {
            return new ShellServiceImpl(shellComponent,true, null);
        }
        @Bean
        private static TestPropertiesPlaceholderConfigurer dpmShellEnvironmentPropertyConfigurer() {
            Properties properties = new Properties();
            properties.put("fortscale.shellService.historySize",3000);

            TestPropertiesPlaceholderConfigurer configurer = new TestPropertiesPlaceholderConfigurer( properties);

            return configurer;
        }
    }
    @Autowired
    ShellServiceImpl shellService;

    @Test
    public void testStatsTopicServiceConfig() {

        JLineShellComponent shell = shellService.getShell();
        CommandResult cr = shell.executeCommand("calc --firstNumber 1 --secondNumber 2 --operator +");
        Assert.assertEquals(true, cr.isSuccess());
        Assert.assertEquals("executing: 1+2 sum:3",cr.getResult());
    }


}
