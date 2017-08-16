package presidio.adapter.spring;

import fortscale.common.shell.PresidioExecutionService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.adapter.services.impl.FlumeAdapterExecutionService;
import presidio.adapter.util.FlumeConfigurationUtil;
import presidio.adapter.util.ProcessExecutor;

/**
 * Created by shays on 26/06/2017.
 */
@Configuration
public class AdapterTestConfig {

    @Bean
    public PresidioExecutionService presidioExecutionService() {
        final ProcessExecutor mockProcessExecutor = Mockito.mock(ProcessExecutor.class);
        Mockito.when(mockProcessExecutor.executeProcess(Mockito.anyString(), Mockito.anyListOf(String.class), Mockito.anyString())).thenReturn(0);

        final FlumeConfigurationUtil mockFlumeConfigurationUtil = Mockito.mock(FlumeConfigurationUtil.class);
        return new FlumeAdapterExecutionService(mockProcessExecutor, mockFlumeConfigurationUtil);
    }
}
