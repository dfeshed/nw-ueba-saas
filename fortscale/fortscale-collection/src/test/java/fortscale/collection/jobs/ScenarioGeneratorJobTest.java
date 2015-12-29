package fortscale.collection.jobs;

import fortscale.domain.core.Computer;
import fortscale.domain.core.User;
import fortscale.services.exceptions.HdfsException;
import fortscale.services.impl.HdfsService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Amir Kere on 28/12/15.
 */
public class ScenarioGeneratorJobTest {

	private ScenarioGeneratorJob scenarioGeneratorJob;
	private User user;
	private Computer computer;
	private HdfsService service;

	@Before
	public void setup() throws HdfsException, IOException {
		scenarioGeneratorJob = new ScenarioGeneratorJob();
		user = mock(User.class);
		computer = mock(Computer.class);
		service = mock(HdfsService.class);
		when(user.getUsername()).thenReturn("alrusr51@somebigcompany.com");
		when(user.getAdministratorAccount()).thenReturn(false);
		when(user.getUserServiceAccount()).thenReturn(false);
		when(user.getExecutiveAccount()).thenReturn(false);
		when(user.getTags()).thenReturn(new HashSet());
		when(user.getTags()).thenReturn(new HashSet());
		when(computer.getName()).thenReturn("alrusr51_PC");
		when(computer.getIsSensitive()).thenReturn(false);
	}

	@Test
	public void testRandomTimes()
			throws ClassNotFoundException, IOException, HdfsException, InstantiationException, IllegalAccessException {
		//scenarioGeneratorJob.createEvents(user, computer, "alrusr51_SRV", service);
	}

	@Test
	public void testRandomAnomalyTimes()
			throws ClassNotFoundException, IOException, HdfsException, InstantiationException, IllegalAccessException {
		DateTime now = new DateTime();
		//scenarioGeneratorJob.generateTimeLoginAnomalies(now.minusDays(1), now.minusDays(30), 2, 3, 3, 5, user, computer, "alrusr51_SRV");
	}

}