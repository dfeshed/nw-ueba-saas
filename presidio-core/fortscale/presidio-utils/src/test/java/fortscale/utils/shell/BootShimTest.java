package fortscale.utils.shell;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Properties;

/**
 * Created by barak_schuster on 7/26/17.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class BootShimTest {
    @Configuration
    @Import(BootShimConfig.class)
    public static class BootShimTestConfig
    {
        @Bean
        public TestPropertiesPlaceholderConfigurer bootShimTestProperties()
        {
            CommandLineArgsHolder.args= new String[]{"help"};
            return new TestPropertiesPlaceholderConfigurer(new Properties());
        }
    }
    @Autowired
    private BootShim bootShim;
    @Test
    public void test()
    {
        ExitShellRequest exitShellRequest = bootShim.run();
        Assert.assertEquals(0, exitShellRequest.getExitCode());
    }



}