package fortscale.domain.core.dao;

import fortscale.domain.core.AlertSeverity;
import fortscale.domain.core.EntityType;
import fortscale.domain.core.dao.rest.Alert;
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
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/fortscale-domain-context-test.xml"})
public class AlertsRepositoryImplTest {

	@Mock
	private HttpServletRequest httpRequest;
	@Mock
	private MongoTemplate mongoTemplate;
	@InjectMocks
	private AlertsRepositoryImpl subject;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testAlertsRepository() throws IOException{
		List<Alert> alertsList = new ArrayList<Alert>();
		alertsList.add(new Alert("1", 1, 2, EntityType.USER, "user1", "rule1", null, "a", 90, AlertSeverity.CRITICAL, "a", "a"));
		alertsList.add(new Alert("2", 1, 2, EntityType.USER, "user1", "rule1", null, "a", 90, AlertSeverity.CRITICAL, "a", "a"));

		when (mongoTemplate.find(any(Query.class), eq(Alert.class))).thenReturn(alertsList);
		when (httpRequest.getRequestURI()).thenReturn("fortscale.org/api/alerts/");
		Alerts alerts = subject.findAll(new PageRequest(1,0), httpRequest);
		assertEquals("user1", alerts.get_embedded().getData().get(0).getEntityName());
		assertEquals("rule1", alerts.get_embedded().getData().get(1).getRule());
		assertEquals("fortscale.org/api/alerts/", alerts.get_links().getLinks().get(0).getHref());
	}
}
