package presidio.output.proccesor;

import fortscale.utils.shell.BootShim;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.shell.core.CommandResult;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.proccesor.spring.OutputProcessorTestConfiguration;
import presidio.output.processor.services.OutputExecutionService;
import presidio.output.processor.services.OutputExecutionServiceImpl;

import java.util.Properties;

@RunWith(SpringRunner.class)
public class FortscaleOutputProcessorApplicationTest {

    public static final String EXECUTION_COMMAND = "run  --schema DLPFILE --start_date 2017-06-13T07:00:00.00Z --end_date 2017-06-13T09:00:00.00Z --fixed_duration_strategy 3600";

    @Autowired
    private BootShim bootShim;

    @Autowired
    OutputExecutionService executionService;

    @Test
    @Ignore
    public void contextLoads() throws Exception {
        Assert.assertTrue(executionService instanceof OutputExecutionServiceImpl);
    }

    @Test
    @Ignore
    //TODO add this test when we have solution to Junits with elasticsearch
    public void outputShellTest() {
        CommandResult commandResult = bootShim.getShell().executeCommand(EXECUTION_COMMAND);
        Assert.assertTrue(commandResult.isSuccess());
    }

    @Configuration
    @Import(OutputProcessorTestConfiguration.class)
    @EnableSpringConfigured
    public static class springConfig {
        @Bean
        public static TestPropertiesPlaceholderConfigurer inputCoreTestConfigurer() {
            Properties properties = new Properties();
            properties.put("streaming.event.field.type.aggr_event", "aggr_event");
            properties.put("streaming.aggr_event.field.context", "context");
            properties.put("page.iterator.page.size", "1000");
            properties.put("severity.critical", "95");
            properties.put("severity.high", "85");
            properties.put("severity.mid", "70");
            properties.put("severity.low", "50");
            properties.put("smart.threshold.score", "50");
            properties.put("smart.page.size", "100");
            properties.put("elasticsearch.port", "9300");
            properties.put("elasticsearch.clustername", "fortscale");
            properties.put("elasticsearch.host", "localhost");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }

    }
}
