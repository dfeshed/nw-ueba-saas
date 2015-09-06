package fortscale.streaming.task;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.scopetest.EPAssertionUtil;
import com.espertech.esper.client.scopetest.ScopeTestHelper;
import com.espertech.esper.client.scopetest.SupportUpdateListener;
import fortscale.domain.core.EntityEvent;
import fortscale.domain.core.EntityTags;
import fortscale.domain.core.EntityType;
import fortscale.streaming.alert.rule.RuleUtils;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Tests EPL rules: verify they satisfy their conditions.
 * Created by galiar on 01/09/2015.
 */
public class EsperRulesTest {


	private EPServiceProvider epService;

	@Before
	public void setUp() throws Exception {
		//get configuration file which contains all the rules

		//load esper configuration file which
		Configuration esperConfig = new Configuration();
		esperConfig.addPlugInSingleRowFunction("extractNormalizedUsernameFromContextId", RuleUtils.class.getName(), "extractNormalizedUsernameFromContextId");
		esperConfig.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
		esperConfig.getEngineDefaults().getLogging().setEnableTimerDebug(false);
		esperConfig.addEventType(EntityEvent.class);
		esperConfig.addEventType(EntityTags.class);

		epService = EPServiceProviderManager.getDefaultProvider(esperConfig);


	}

	/**
	 * test rule 'fortscale.esper.rule.statement._2_3_SmartEventWithSensitiveAccount'.
	 * tests that esper rule catch sensitive users with smart events ( also known as entityEvent), and that the rule doesn't catch extra.
	 * @throws Exception
	 */
	@Test
	public void testSmartEventWithSensitiveAccountTest() throws Exception{
		// this rule is a copy of the rule exist in alert-generation-task.properties
		String jokerSensitiveAccount =
				"select 'Suspicious Activity For Sensitive Account' as title, case when score >= 50 and score < 65 then 'Low' when score >= 65 and score < 75 then 'Medium' when score >= 75 and score < 90 then 'High' when score >= 90 then 'Critical'  end as severity , Tags.entityType as entityType, Tags.entityName as entityName, aggregated_feature_events, start_time_unix, end_time_unix, score * 1.0 as score, Tags.tags as tags from EntityEvent(score >= 50).std:groupwin(contextId).std:lastevent() as SmartEvent, EntityTags.std:groupwin(entityType,entityName).std:lastevent() as Tags where extractNormalizedUsernameFromContextId(contextId) = Tags.entityName and ( 'admin' = any(Tags.tags) or 'executive' = any(Tags.tags) or 'service' = any(Tags.tags) )";

		EPStatement stmtLow = epService.getEPAdministrator().createEPL(jokerSensitiveAccount);

		//listener catches only events that pass the rule
		SupportUpdateListener listener = new SupportUpdateListener();
		stmtLow.addListener(listener);

		//create events for testing
		//each of these event should satisfy one rule exactly
		EntityEvent entityEventLow =      new EntityEvent(1234L,99,"event_type",55,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>());
		EntityEvent entityEventMedium =   new EntityEvent(1234L,99,"event_type",70,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>());
		EntityEvent entityEventHigh =     new EntityEvent(1234L,99,"event_type",80,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>());
		EntityEvent entityEventCritical = new EntityEvent(1234L,99,"event_type",99,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>());

		//these events should not satisfy any rule
		EntityEvent entityEventTooLow = new   EntityEvent(1234L,99,"event_type",40,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>());
		EntityEvent entityEventNotAdmin = new EntityEvent(1234L,99,"event_type",98,new HashMap<String,String>(),"normalized_username_user10@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>());


		List<String> userTags = new ArrayList<>();
		userTags.add("admin");
		EntityTags entityTags = new EntityTags(EntityType.User,"user1@fs.com",userTags);

		epService.getEPRuntime().sendEvent(entityTags);
		epService.getEPRuntime().sendEvent(entityEventLow);
		EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), new String[] { "entityName", "severity" }, new Object[] { "user1@fs.com", "Low" });

		epService.getEPRuntime().sendEvent(entityEventMedium);
		EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), new String[] { "entityName", "severity" }, new Object[] { "user1@fs.com", "Medium" });

		epService.getEPRuntime().sendEvent(entityEventHigh);
		EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), new String[] { "entityName", "severity" }, new Object[] { "user1@fs.com", "High" });


		epService.getEPRuntime().sendEvent(entityEventCritical);
		epService.getEPRuntime().sendEvent(entityEventTooLow); // this one shouldn't affect
		epService.getEPRuntime().sendEvent(entityEventNotAdmin); // this one shouldn't affect
		EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), new String[] { "entityName", "severity" }, new Object[] { "user1@fs.com", "Critical" });

	}
}
