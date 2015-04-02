package fortscale.streaming.service;

import fortscale.streaming.model.AmtSession;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.mockito.Mockito.*;

public class AmtSummaryServiceTest {
	AmtSummaryService amtSummaryService;

	@Before
	public void setUp() {
		Set<String> sensitiveActionCodes = new HashSet<>();
		sensitiveActionCodes.add("CODE1");
		sensitiveActionCodes.add("CODE2");

		Map<String, List<String>> failedActionCodes = new HashMap<>();
		List<String> actionString = new ArrayList<>();
		actionString.add("s1");
		actionString.add("s2");
		failedActionCodes.put("CODE3", actionString);
		actionString = new ArrayList<>();
		actionString.add("s3");
		actionString.add("s4");
		failedActionCodes.put("CODE4", actionString);

		amtSummaryService = spy(new AmtSummaryService(sensitiveActionCodes, failedActionCodes));
	}

	@Test
	public void updateSessionValues_should_update_all_property_values_in_session() throws Exception {
		AmtSession amtSession = mock(AmtSession.class);
		amtSummaryService.updateSessionValues(amtSession, "CODE3", "s1", "host", "ip", 1234l, true);
		verify(amtSession, times(1)).addHostname("host");
		verify(amtSession, times(1)).addIpAddress("ip");
		verify(amtSession, times(1)).setEndTimeUnix(1234l);
		verify(amtSession, times(1)).markVipYid();
		verify(amtSummaryService, times(1)).incSensitiveActionCode(amtSession, "CODE3");
		verify(amtSummaryService, times(1)).incFailedActionCode(amtSession, "CODE3", "s1");
	}

	@Test
	public void updateSessionValues_should_not_update_vip_yid() throws Exception {
		AmtSession amtSession = mock(AmtSession.class);
		amtSummaryService.updateSessionValues(amtSession, "CODE3", "s1", "host", "ip", 1234l, false);
		verify(amtSession, never()).markVipYid();
	}

	@Test
	public void incSensitiveActionCode_should_increase_the_count_of_sensitive_actions() throws Exception {
		AmtSession amtSession = mock(AmtSession.class);
		amtSummaryService.incSensitiveActionCode(amtSession, "CODE1");
		verify(amtSession, times(1)).incActionTypeCount(AmtSession.ActionType.Sensitive);
	}

	@Test
	public void incSensitiveActionCode_should_not_increase_the_count_of_sensitive_actions_wrong_code() throws Exception {
		AmtSession amtSession = mock(AmtSession.class);
		amtSummaryService.incSensitiveActionCode(amtSession, "CODE3");
		verify(amtSession, never()).incActionTypeCount(AmtSession.ActionType.Sensitive);
	}

	@Test
	public void incFailedActionCode_should_increase_the_count_of_failed_actions() throws Exception {
		AmtSession amtSession = mock(AmtSession.class);
		amtSummaryService.incFailedActionCode(amtSession, "CODE3", "s2");
		verify(amtSession, times(1)).incActionTypeCount(AmtSession.ActionType.Failed);
	}

	@Test
	public void incFailedActionCode_should_not_increase_the_count_of_failed_actions_wrong_string() throws Exception {
		AmtSession amtSession = mock(AmtSession.class);
		amtSummaryService.incFailedActionCode(amtSession, "CODE4", "s5");
		verify(amtSession, never()).incActionTypeCount(AmtSession.ActionType.Failed);
	}

	@Test
	public void incFailedActionCode_should_not_increase_the_count_of_failed_actions_wrong_string_case() throws Exception {
		AmtSession amtSession = mock(AmtSession.class);
		amtSummaryService.incFailedActionCode(amtSession, "CODE4", "S4");
		verify(amtSession, never()).incActionTypeCount(AmtSession.ActionType.Failed);
	}

	@Test
	public void incFailedActionCode_should_not_increase_the_count_of_failed_actions_wrong_code() throws Exception {
		AmtSession amtSession = mock(AmtSession.class);
		amtSummaryService.incFailedActionCode(amtSession, "CODE4", "s5");
		verify(amtSession, never()).incActionTypeCount(AmtSession.ActionType.Failed);
	}
}
