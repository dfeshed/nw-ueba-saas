package fortscale.domain.core.dao;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.rest.Alerts;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AlertsRepositoryImplTest {

	@Mock
	private MongoTemplate mongoTemplate;
	@InjectMocks
	private AlertsRepositoryImpl subject;

	@Mock
	private EvidencesRepository evidencesRepository;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testFindAll() throws IOException{
		List<Alert> alertsList = new ArrayList<>();
		alertsList.add(new Alert("Alert1", 1, 2, EntityType.User, "user1", "rule1", null, "a", 90, Severity.Critical, AlertStatus.Accepted, "a"));
		alertsList.add(new Alert("Alert2", 1, 2, EntityType.User, "user1", "rule1", null, "a", 90, Severity.Critical, AlertStatus.Accepted, "a"));

		when (mongoTemplate.find(any(Query.class), eq(Alert.class))).thenReturn(alertsList);
		Alerts alerts = subject.findAll(new PageRequest(1,0));
		verify(mongoTemplate).find(any(Query.class), eq(Alert.class));
		assertEquals("user1", alerts.getAlerts().get(0).getEntityName());
		assertEquals("rule1", alerts.getAlerts().get(1).getRule());
	}

	@Test
	public void testCount() throws IOException{
		when (mongoTemplate.count(any(Query.class), eq(Alert.class))).thenReturn(20L);
		Long count = subject.count(new PageRequest(1,0));
		assertEquals(new Long(20), count);
	}

	@Test
	public void testGetAlertById() {
		Alert alert = new Alert("Alert1", 1, 2, EntityType.User, "user1", "rule1", null, "a", 90, Severity.Critical, AlertStatus.Accepted, "a");

		List<Evidence> evidences = new ArrayList<>();
		Evidence evidence0 = new Evidence(EntityType.User,"entityName", 123L,123L, "type", "name0","anomalyValue","dataSource",99, Severity.Critical);
		Evidence evidence1 = new Evidence(EntityType.User,"entityName", 123L,123L, "type", "name0","anomalyValue","dataSource",99, Severity.Critical);

		evidences.add(evidence0);
		evidences.add(evidence1);

		alert.setEvidences(evidences);

		when (mongoTemplate.findById(any(Query.class), eq(Alert.class))).thenReturn(alert);
		Alert result = subject.getAlertById(alert.getId());
		verify(mongoTemplate).findById(any(Query.class), eq(Alert.class));
		assertEquals("user1", result.getEntityName());

	}


}
