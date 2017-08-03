package fortscale.utils.influxdb;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Ignore
public class InfluxdbServiceTest {

	static InfluxdbService remoteService = mock(InfluxdbService.class);

	@Configuration
    @EnableRetry
    public static class influxdbTestSpringConfig {

        @Bean
        public InfluxdbService influxdbClient() throws Exception {
			when(remoteService.describeDatabases())
					.thenThrow(new RuntimeException("Remote Exception 1"))
					.thenThrow(new RuntimeException("Remote Exception 2"))
					.thenReturn(Arrays.asList("Completed"));
            return remoteService;
        }
		@Bean
		public RetryTemplate retryTemplate() {
			RetryTemplate retryTemplate = new RetryTemplate();
			FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
			fixedBackOffPolicy.setBackOffPeriod(2000l);
			retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

			SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
			retryPolicy.setMaxAttempts(3);

			retryTemplate.setRetryPolicy(retryPolicy);
			return retryTemplate;
		}
    }
	@Autowired
	private RetryTemplate retryTemplate;


	@Test
	public void shouldRetryThreeTimes()
	{
		List<String> databases = this.retryTemplate.execute(context -> this.remoteService.describeDatabases());
		verify(remoteService,times(3)).describeDatabases();
		assertThat(databases,is(Arrays.asList("Completed")));
		verifyNoMoreInteractions(remoteService);
	}
	@After
	public void validate() {
		validateMockitoUsage();
	}
}