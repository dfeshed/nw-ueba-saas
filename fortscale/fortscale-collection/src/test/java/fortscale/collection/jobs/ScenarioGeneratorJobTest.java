package fortscale.collection.jobs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/collection-context-test.xml"})
public class ScenarioGeneratorJobTest {

	private static final String CONTEXT = "classpath*:META-INF/spring/collection-context-test.xml";

	@Autowired
	private ScenarioGeneratorJob scenarioGeneratorJob;
	
	@Test
	public void testBucketCreation() {
		/*DateTime dt = new DateTime()
				.withZone(DateTimeZone.UTC)
				.withHourOfDay(4)
				.withMinuteOfHour(0)
				.withSecondOfMinute(0)
				.withMillisOfSecond(0);
		long startTime = dt.getMillis() / 1000;
		long endTime = dt.plusHours(1).minusMillis(1).getMillis() / 1000;*/
		try {
			scenarioGeneratorJob.createEvents(CONTEXT, "adminusr25fs@somebigcompany.com", "alrusr51_PC", "alrusr51_SRV");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
