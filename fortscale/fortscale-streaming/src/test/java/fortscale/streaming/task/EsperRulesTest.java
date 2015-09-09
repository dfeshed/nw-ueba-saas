package fortscale.streaming.task;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.scopetest.EPAssertionUtil;
import com.espertech.esper.client.scopetest.ScopeTestHelper;
import com.espertech.esper.client.scopetest.SupportUpdateListener;
import fortscale.domain.core.*;
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
		esperConfig.addPlugInSingleRowFunction("hourStartTimestamp",RuleUtils.class.getName(),"hourStartTimestamp");
		esperConfig.addPlugInSingleRowFunction("hourEndTimestamp",RuleUtils.class.getName(),"hourEndTimestamp");
		esperConfig.addPlugInSingleRowFunction("dayStartTimestamp",RuleUtils.class.getName(),"dayStartTimestamp");
		esperConfig.addImport("fortscale.domain.core.*");
		esperConfig.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
		esperConfig.getEngineDefaults().getLogging().setEnableTimerDebug(false);
		esperConfig.addEventType(EntityEvent.class);
		esperConfig.addEventType(EntityTags.class);
		esperConfig.addEventType(Evidence.class);

		epService = EPServiceProviderManager.getDefaultProvider(esperConfig);


	}

	/**
	 * test rule 'fortscale.esper.rule.statement._2_3_SmartEventWithSensitiveAccount'.
	 * tests that esper rule catch sensitive users with smart events ( also known as entityEvent), and that the rule doesn't catch extra.
	 * @throws Exception
	 */
	@Test
	public void testSmartEventWithSensitiveAccountTest() throws Exception{


		long eventStartData= 1441694789L;
		long eventHourEndDate = 1441695599L;

		String enrichEvidence = "insert into EnrichedEvidence select id, entityType, entityName, score, evidenceType, hourStartTimestamp(startDate) as hourlyStartDate, dayStartTimestamp(startDate) as dailyStartDate from Evidence";
		String enrichedEntityEvent = "insert into EnrichedEntityEvent select EntityType.User as entityType, extractNormalizedUsernameFromContextId(contextId) as entityName, score, hourStartTimestamp(start_time_unix) as hourlyStartDate, dayStartTimestamp(start_time_unix) as dailyStartDate, aggregated_feature_events, start_time_unix, end_time_unix from EntityEvent";
		String hourlyContextByUser = "create context HourlyTimeFrame partition by entityType,entityName,hourlyStartDate from EnrichedEvidence";

		String createTimestamp = "create variable Long currentTimestamp =5400110L";
		String createLastEventTimestamp = "create variable Long lastEventTimestamp =" + eventHourEndDate +" + (30*60*1000) - 1"; // bigger in one than 30*60*1000+hourEndTimestamp

		// this rule is a copy of the rule exist in alert-generation-task.properties
		String jokerSensitiveAccount =
				" @Audit select 'Suspicious Activity For Sensitive Account' as title, size,"
						+ " case"
						+ " when (SmartEvent.score in [50:65)and size =0 ) or (SmartEvent.score in [50:60) and size >=1  ) then 'Low' "
						+ " when (SmartEvent.score in [65:75) and size =0 ) or (SmartEvent.score in [60:70)and size >=1 ) then 'Medium'"
						+ " when (SmartEvent.score in [75:90) and size =0  ) or (SmartEvent.score in [70:85)and size >=1 ) then 'High'"
						+ " when (SmartEvent.score >= 90 and size =0 ) or (SmartEvent.score >= 85 and size >=1  ) then 'Critical'"
						+ "  end as severity , Tags.entityType as entityType, Tags.entityName as entityName,"
						+ " aggregated_feature_events, start_time_unix, end_time_unix, SmartEvent.score * 1.0 as score, Tags.tags as tags"
						+ " from "
						+ "EnrichedEntityEvent(score >= 50).std:groupwin(entityName,entityType).std:lastevent() as SmartEvent"
						+ " inner join"
						+ " EntityTags.std:groupwin(entityType,entityName).std:lastevent() as Tags "
						+ " on SmartEvent.entityName = EntityTags.entityName and SmartEvent.entityType = Tags.entityType "
						+ "left outer join"
						+ " EnrichedEvidence(evidenceType = EvidenceType.Notification).win:expr_batch(oldest_timestamp+(60*60*1000+30*60*1000) < currentTimestamp or"
						+ " (oldest_event.hourlyStartDate is not null and lastEventTimestamp > 30*60*1000+hourEndTimestamp(oldest_event.hourlyStartDate))).std:size(entityType,entityName) as Notification"
						+ " on SmartEvent.entityName = Notification.entityName and SmartEvent.entityType = Notification.entityType"
						+ " where "
						+ "  'admin' = any(Tags.tags) or 'executive' = any(Tags.tags) or 'service' = any(Tags.tags) ";


		epService.getEPAdministrator().createEPL(createTimestamp);
		epService.getEPAdministrator().createEPL(createLastEventTimestamp);
		epService.getEPAdministrator().createEPL(enrichEvidence);
		epService.getEPAdministrator().createEPL(enrichedEntityEvent);
		epService.getEPAdministrator().createEPL(hourlyContextByUser);
		EPStatement stmt = epService.getEPAdministrator().createEPL(jokerSensitiveAccount);

		//listener catches only events that pass the rule
		SupportUpdateListener listener = new SupportUpdateListener();
		stmt.addListener(listener);

		//create events for testing
		//each of these event should satisfy one rule exactly
		//1441694790L == 2014/09/08:09:46:29
		EntityEvent entityEventLow =      new EntityEvent(eventStartData,99,"event_type",62,new HashMap<String,String>(),"normalized_username_user1@fs.com",eventStartData +1,eventStartData +1,"entity_event_type",eventStartData +1,new ArrayList<JSONObject>());
		EntityEvent entityEventMedium =   new EntityEvent(eventStartData,99,"event_type",72,new HashMap<String,String>(),"normalized_username_user1@fs.com",eventStartData +1,eventStartData +1,"entity_event_type",eventStartData +1,new ArrayList<JSONObject>());
		EntityEvent entityEventHigh =     new EntityEvent(eventStartData,99,"event_type",88,new HashMap<String,String>(),"normalized_username_user1@fs.com",eventStartData +1,eventStartData +1,"entity_event_type",eventStartData +1,new ArrayList<JSONObject>());
		EntityEvent entityEventCritical = new EntityEvent(eventStartData,99,"event_type",99,new HashMap<String,String>(),"normalized_username_user1@fs.com",eventStartData +1,eventStartData +1,"entity_event_type",eventStartData +1,new ArrayList<JSONObject>());

		//these events should not satisfy any rule
		EntityEvent entityEventTooLow = new   EntityEvent(eventStartData,99,"event_type",40,new HashMap<String,String>(),"normalized_username_user1@fs.com", eventStartData +1,eventStartData +1,"entity_event_type",eventStartData +1,new ArrayList<JSONObject>());
		EntityEvent entityEventNotAdmin = new EntityEvent(eventStartData,99,"event_type",98,new HashMap<String,String>(),"normalized_username_user10@fs.com",eventStartData +1,eventStartData +1,"entity_event_type",eventStartData +1,new ArrayList<JSONObject>());

		//---test the rule without the notification

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

		//---test the rule with notification

		Evidence notification = new Evidence(EntityType.User,"entityTypeFieldName","user1@fs.com", EvidenceType.Notification,eventStartData ,eventStartData +1,"anomalyTypeFieldName","anomalyValue",new ArrayList<String>(),99,Severity.Critical,3,EvidenceTimeframe.Hourly);

		entityEventLow.setScore(55);
		entityEventMedium.setScore(62);
		entityEventHigh.setScore(72);
		entityEventCritical.setScore(90);

		epService.getEPRuntime().sendEvent(notification);
		listener.reset();
		epService.getEPRuntime().sendEvent(entityEventLow);
		EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), new String[] { "entityName", "severity", "size" }, new Object[] { "user1@fs.com", "Low",1L });

		epService.getEPRuntime().sendEvent(entityEventMedium);
		EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), new String[] { "entityName", "severity","size" }, new Object[] { "user1@fs.com", "Medium",1L });

		epService.getEPRuntime().sendEvent(entityEventHigh);
		EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), new String[] { "entityName", "severity","size" }, new Object[] { "user1@fs.com", "High",1L });

		epService.getEPRuntime().sendEvent(entityEventCritical);
		epService.getEPRuntime().sendEvent(entityEventTooLow); // this one shouldn't affect
		epService.getEPRuntime().sendEvent(entityEventNotAdmin); // this one shouldn't affect
		EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), new String[] { "entityName", "severity","size" }, new Object[] { "user1@fs.com", "Critical",1L });


	}
}
