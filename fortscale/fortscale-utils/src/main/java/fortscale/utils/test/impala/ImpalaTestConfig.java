package fortscale.utils.test.impala;

import fortscale.utils.impala.ImpalaClient;
import fortscale.utils.impala.test.ImpalaTestClient;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.EmbeddedDb.EmbededDataSourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * configuration class that enables module-test with impala client. records are written into in-memory store
 * Created by baraks on 1/2/2017.
 */
@Configuration
@Import(EmbededDataSourceConfig.class)
public class ImpalaTestConfig {

    @Autowired
    private DataSource embeddedDataSource;

    @Bean
    public JdbcOperations impalaTestTemplate()
    {
        return new JdbcTemplate(embeddedDataSource);
    }

    @Bean
    public ImpalaClient impalaClient()
    {
        return new ImpalaTestClient();
    }

    @Bean
    public static TestPropertiesPlaceholderConfigurer impalaClientTestPropertiesPlaceholderConfigurer()
    {
        Properties properties = new Properties();
        properties.put("impala.table.fields.updateTimestamp","update_timestamp");
        return new TestPropertiesPlaceholderConfigurer(properties);
    }
}
