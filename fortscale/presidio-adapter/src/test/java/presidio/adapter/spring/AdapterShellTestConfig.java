package presidio.adapter.spring;

import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.common.shell.command.PresidioCommands;
import fortscale.utils.shell.BootShimConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.Instant;

@Configuration
@Import({BootShimConfig.class, PresidioCommands.class})
public class AdapterShellTestConfig {


    @Bean
    public PresidioExecutionService adapterExecutionService() {
        return new PresidioExecutionService() {

            @Override
            public void run(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {

            }

            @Override
            public void cleanup(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {

            }

            @Override
            public void applyRetentionPolicy(Schema schema, Instant startDate, Instant endDate) throws Exception {

            }

            @Override
            public void cleanAll(Schema schema) throws Exception {

            }
        };
    }
}
