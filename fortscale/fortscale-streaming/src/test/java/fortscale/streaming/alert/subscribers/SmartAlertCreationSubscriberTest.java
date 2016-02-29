package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.*;
import fortscale.services.AlertsService;
import fortscale.services.EvidencesService;
import fortscale.services.ForwardingService;
import fortscale.services.UserService;
import junit.framework.TestCase;
import net.minidev.json.JSONObject;
import org.fusesource.hawtjni.runtime.T32;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Created by tomerd on 29/02/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class SmartAlertCreationSubscriberTest extends TestCase {

	final String NOTIFICATION_EVIDENCE_TYPE = "Notification";

	@Mock
	private EvidencesService evidencesDao;

	@Mock
	private AlertsService alertsService;

	@InjectMocks
	SmartAlertCreationSubscriber subscriber;

	@InjectMocks
	ForwardingService forwardingService;

	@Mock
	private UserService userService;

	@Test
	public void testUpdate() throws Exception {

		List<String> tags = new ArrayList<>();
		String title = "Suspicious Daily User Activity";
		String severity = "Critical";
		EntityType entityType = EntityType.User;
		String entityName = "user@somebigcompany.com";
		List<JSONObject> aggregatedFeatureEvents = new ArrayList<>();
		long startTime = 1455753600000l;
		long endTime = 1455839999000l;
		Double score = 100.0;

		User user = new User();
		user.setUsername(entityName);

		List<Evidence> returnValue = new ArrayList<Evidence>();

		when(evidencesDao.findByEndDateBetweenAndEvidenceTypeAndEntityName(anyLong(), anyLong(), anyString(), anyString())).
				thenReturn(new ArrayList<Evidence>());

		when(userService.findByUsername(anyString())).thenReturn(user);

		doNothing().when(alertsService).saveAlertInRepository(any(Alert.class));

		subscriber.update(title, severity, entityType, entityName, aggregatedFeatureEvents, startTime, endTime, score, tags);
	}
}