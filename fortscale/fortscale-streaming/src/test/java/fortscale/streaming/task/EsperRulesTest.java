package fortscale.streaming.task;

import com.espertech.esper.client.*;
import com.espertech.esper.client.scopetest.EPAssertionUtil;
import com.espertech.esper.client.scopetest.SupportUpdateListener;
import fortscale.domain.core.*;
import fortscale.streaming.alert.rule.RuleUtils;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Tests EPL rules: verify they satisfy their conditions.
 * Created by galiar on 01/09/2015.
 */
public class EsperRulesTest {

    public static final String CREATE_ENRICHED_ENTITY_EVENT = "insert into EnrichedEntityEvent select EntityType.User as entityType, extractNormalizedUsernameFromContextId(contextId) as entityName, score, aggregated_feature_events, start_time_unix, end_time_unix, entity_event_name from EntityEvent";
    public static final String CREATE_ENRICHED_EVIDENCE = "insert into EnrichedEvidence select id, entityType, entityName, score, evidenceType, hourStartTimestamp(startDate) as hourlyStartDate, dayStartTimestamp(startDate) as dailyStartDate from Evidence";

    public static final String CREATE_ENRICHED_FORTSCALE_EVENT_BY_NOTIFICATIONS = "insert into EnrichedFortscaleEvent select id, entityType, entityName, score, evidenceType, hourStartTimestamp(startDate) as hourlyStartDate, dayStartTimestamp(startDate) as dailyStartDate, startDate , endDate, aggEvent() as aggregatedFeatureEvents, anomalyTypeFieldName, supportingInformation from Evidence where cast(Evidence.evidenceType, string) = 'Notification'";
    public static final String CREATE_ENRICHED_FORTSCALE_EVENT_BY_SMART_EVENTS = "insert into EnrichedFortscaleEvent select id, EntityType.User as entityType, extractNormalizedUsernameFromContextId(contextId) as entityName, cast(score, int) as score, EvidenceType.Smart as evidenceType, hourStartTimestamp(start_time_unix * 1000) as hourlyStartDate, dayStartTimestamp(start_time_unix * 1000) as dailyStartDate, start_time_unix as startDate, end_time_unix as endDate, aggregated_feature_events as aggregatedFeatureEvents, 'smart' as anomalyTypeFieldName, null as supportingInformation from EntityEvent where score >= 50";

    public static final String CREATE_HOURLY_CONTEXT_BY_USER = "create context HourlyTimeFrame partition by entityType,entityName,hourlyStartDate from EnrichedEvidence";
    public static final String CREATE_HOURLY_CONTEXT_BY_USER_EnrichedFortscaleEvent = "create context HourlyTimeFrame partition by entityType,entityName,hourlyStartDate from EnrichedFortscaleEvent";
    public static final String ENTITY_EVENT_NAME_HOURLY = "normalized_username_hourly";
    public static final String ENTITY_EVENT_NAME_DAILY = "normalized_username_daily";

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
        esperConfig.addPlugInSingleRowFunction("aggEvent",RuleUtils.class.getName(),"aggEvent");
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
    public void testSmartEventForSensetiveUserWithNotificationTest() throws Exception{

        epService.destroy();
        epService.initialize();

        epService.getEPAdministrator().destroyAllStatements();
        long eventStartData= 1441694789L;
        EPStatement stmt = initSmartEventWithSensetiveUserAccountWithNotification();

        //listener catches only events that pass the rule
        SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);

		// Alert with notification and tag

		EntityEvent entityEventLow =   new EntityEvent(eventStartData,99,60,60,new HashMap<String,String>(),"normalized_username_user1@fs.com",eventStartData +2,eventStartData +2,"entity_event_type",eventStartData +2,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
		Evidence notification = new Evidence(EntityType.User,"entityTypeFieldName","user1@fs.com", EvidenceType.Notification,eventStartData ,eventStartData +1,"anomalyTypeFieldName","anomalyValue",new ArrayList<String>(),99,Severity.Critical,3,EvidenceTimeframe.Hourly);

		List<String> userTags = new ArrayList<>();
		userTags.add("admin");
		EntityTags entityTags = new EntityTags(EntityType.User,"user1@fs.com",userTags);

		epService.getEPRuntime().sendEvent(entityTags);
		epService.getEPRuntime().sendEvent(notification);
		epService.getEPRuntime().sendEvent(entityEventLow);

		EventBean result = listener.assertOneGetNewAndReset();
		EPAssertionUtil.assertProps(result, new String[] { "entityName", "severity" }, new Object[] { "user1@fs.com", "Medium" });


		//Notification for user without tag should be ignored
		notification = new Evidence(EntityType.User,"entityTypeFieldName","user10@fs.com", EvidenceType.Notification,eventStartData ,eventStartData +1,"anomalyTypeFieldName","anomalyValue",new ArrayList<String>(),99,Severity.Critical,3,EvidenceTimeframe.Hourly);
		epService.getEPRuntime().sendEvent(notification);
		EPAssertionUtil.assertAllBooleanTrue(new Boolean[] { !listener.isInvoked() });

	}

    /**
     * test rule Suspicious hourly activity for EnrichedFortscaleEvent
     *  basic test - tests that notification is passed by this rule
     * @throws Exception
     */
    @Test
    public void testEnrichedFortscaleEventHourlyTest() throws Exception{

        epService.destroy();
        epService.initialize();

        epService.getEPAdministrator().destroyAllStatements();
        long eventStartData= 1441694789L;
        EPStatement stmt = initEnrichedFortscaleEventHourly();

        //listener catches only events that pass the rule
        SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);

        // Alert with notification and tag
        Evidence notification = new Evidence(EntityType.User,"entityTypeFieldName","user1@fs.com", EvidenceType.Notification,eventStartData ,eventStartData +1,"anomalyTypeFieldName","anomalyValue",new ArrayList<String>(),99,Severity.Critical,3,EvidenceTimeframe.Hourly);
        epService.getEPRuntime().sendEvent(notification);

        EventBean result = listener.assertOneGetNewAndReset();
        EPAssertionUtil.assertProps(result, new String[] { "entityName" }, new Object[] { "user1@fs.com"});

        // smart event
        EntityEvent smartEvent = new EntityEvent(eventStartData,99,60,60, new HashMap<>(),"normalized_username_user1@fs.com",eventStartData +1,eventStartData +1,"entity_event_type",eventStartData +1, new ArrayList<>(),ENTITY_EVENT_NAME_HOURLY);
        epService.getEPRuntime().sendEvent(smartEvent);


        result = listener.assertOneGetNewAndReset();
        EPAssertionUtil.assertProps(result, new String[] { "entityName" }, new Object[] { "user1@fs.com"});

    }




	/**
	 * test rule 'fortscale.esper.rule.statement._2_3_SmartEventWithSensitiveAccount'.
	 * tests that esper rule catch sensitive users with smart events ( also known as entityEvent), and that the rule doesn't catch extra.
	 * @throws Exception
	 */
	@Test
	public void testSmartEventForSensetiveUserWithoutNotificationTest() throws Exception{

		epService.destroy();
		epService.initialize();
		epService.getEPAdministrator().destroyAllStatements();
		long eventStartData= 1441694789L;
		EPStatement stmt = initSmartEventWithSensetiveUserAccountWithoutNotification();

		//listener catches only events that pass the rule
		SupportUpdateListener listener = new SupportUpdateListener();
		stmt.addListener(listener);

		//---test the rule without the notification LOW

		List<String> userTags = new ArrayList<>();
		userTags.add("admin");
		EntityTags entityTags = new EntityTags(EntityType.User,"user1@fs.com",userTags);

		//create events for testing
		//each of these event should satisfy one rule exactly
		//1441694790L == 2014/09/08:09:46:29
		EntityEvent entityEventLow =      new EntityEvent(eventStartData,99,62,62,new HashMap<String,String>(),"normalized_username_user1@fs.com",eventStartData +1,eventStartData +1,"entity_event_type",eventStartData +1,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);


		epService.getEPRuntime().sendEvent(entityTags);
		epService.getEPRuntime().sendEvent(entityEventLow);

		EventBean result = listener.assertOneGetNewAndReset();

		EPAssertionUtil.assertProps(result, new String[] { "entityName","severity" }, new Object[] { "user1@fs.com","Low"});

		//---test the rule without the notification Medium
		EntityEvent entityEventMedium =   new EntityEvent(eventStartData,99,72,72,new HashMap<String,String>(),"normalized_username_user1@fs.com",eventStartData +1,eventStartData +1,"entity_event_type",eventStartData +1,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);

		epService.getEPRuntime().sendEvent(entityEventMedium);
		result = listener.assertOneGetNewAndReset();
		EPAssertionUtil.assertProps(result, new String[] { "entityName","severity" }, new Object[] { "user1@fs.com","Medium"});


		//---test the rule without the notification High
		EntityEvent entityEventHigh =     new EntityEvent(eventStartData,99,88,88,new HashMap<String,String>(),"normalized_username_user1@fs.com",eventStartData +1,eventStartData +1,"entity_event_type",eventStartData +1,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);

		epService.getEPRuntime().sendEvent(entityEventHigh);
		result = listener.assertOneGetNewAndReset();
		EPAssertionUtil.assertProps(result, new String[] { "entityName","severity" }, new Object[] { "user1@fs.com","High"});

		//---test the rule without the notification Critical
		EntityEvent entityEventCritical = new EntityEvent(eventStartData,99,99,99,new HashMap<String,String>(),"normalized_username_user1@fs.com",eventStartData +1,eventStartData +1,"entity_event_type",eventStartData +1,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);

		epService.getEPRuntime().sendEvent(entityEventCritical);
		result = listener.assertOneGetNewAndReset();
		EPAssertionUtil.assertProps(result, new String[] { "entityName","severity" }, new Object[] { "user1@fs.com","Critical"});

		//these events should not satisfy any rule
		EntityEvent entityEventTooLow = new   EntityEvent(eventStartData,99,40,40,new HashMap<String,String>(),"normalized_username_user1@fs.com", eventStartData +1,eventStartData +1,"entity_event_type",eventStartData +1,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
		EntityEvent entityEventNotAdmin = new EntityEvent(eventStartData,99,98,98,new HashMap<String,String>(),"normalized_username_user10@fs.com",eventStartData +1,eventStartData +1,"entity_event_type",eventStartData +1,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);

		epService.getEPRuntime().sendEvent(entityEventTooLow);
		epService.getEPRuntime().sendEvent(entityEventNotAdmin);

		EPAssertionUtil.assertAllBooleanTrue(new Boolean[] {!listener.isInvoked()});
	}

    /**
     * Test rule for normal user without notification for event of low severity
     * @throws Exception
     */
	@Test
	public void testSmartEventWithNormalUserAccountWithoutNotification() throws Exception{

        EPStatement stmt = initSmartEventWithNormalUserAccountWithoutNotification();
		//listener catches only events that pass the rule
		SupportUpdateListener listener = new SupportUpdateListener();

		stmt.addListener(listener);

		EntityEvent entityEventLow =      new EntityEvent(1234L,99,65,65,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
		EntityEvent entityEventMedium =   new EntityEvent(1234L,99,75,75,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
		EntityEvent entityEventHigh =     new EntityEvent(1234L,99,90,90,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
		EntityEvent entityEventCritical = new EntityEvent(1234L,99,99,99,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);

		//these events should not satisfy any rule
		EntityEvent entityEventTooLow =  new EntityEvent(1234L,99,40,40,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
		EntityEvent entityEventIsAdmin = new EntityEvent(1234L,99,98,98,new HashMap<String,String>(),"normalized_username_user2@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);

		List<String> userTags = new ArrayList<>();
		userTags.add("admin");
		EntityTags entityTags = new EntityTags(EntityType.User,"user2@fs.com",userTags);

		epService.getEPRuntime().sendEvent(entityTags);
		epService.getEPRuntime().sendEvent(entityEventLow);
		EventBean result = listener.assertOneGetNewAndReset();

		EPAssertionUtil.assertProps(result, new String[] { "entityName", "severity" }, new Object[] { "user1@fs.com", "Low" });

		epService.getEPRuntime().sendEvent(entityEventMedium);
		EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), new String[]{"entityName", "severity"}, new Object[]{"user1@fs.com", "Medium"});

		epService.getEPRuntime().sendEvent(entityEventHigh);
		EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), new String[]{"entityName", "severity"}, new Object[]{"user1@fs.com", "High"});

		epService.getEPRuntime().sendEvent(entityEventCritical);
		epService.getEPRuntime().sendEvent(entityEventTooLow); // this one shouldn't affect
		epService.getEPRuntime().sendEvent(entityEventIsAdmin); // this one shouldn't affect
		EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), new String[]{"entityName", "severity"}, new Object[]{"user1@fs.com", "Critical"});
	}

    /**
     * Test rule for normal user with notification for event of low severity
     * @throws Exception
     */
    @Test
    public void testSmartEventWithNormalUserAccountAndNotificationLowSeverity() throws Exception{

        EPStatement stmt = initSmartEventWithNormalUserAccountWithNotification();
        //listener catches only events that pass the rule
        SupportUpdateListener listener = new SupportUpdateListener();

        stmt.addListener(listener);

        EntityEvent entityEventLow = new EntityEvent(1234L,99,55,55,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
        Evidence notification = new Evidence(EntityType.User,"entityTypeFieldName","user1@fs.com", EvidenceType.Notification,12345L ,12345L +1,"anomalyTypeFieldName","anomalyValue",new ArrayList<String>(),65,Severity.Low,3,EvidenceTimeframe.Hourly);

        List<String> userTags = new ArrayList<>();
        userTags.add("admin");
        EntityTags entityTags = new EntityTags(EntityType.User,"user2@fs.com",userTags);

        epService.getEPRuntime().sendEvent(notification);
        epService.getEPRuntime().sendEvent(entityTags);
        epService.getEPRuntime().sendEvent(entityEventLow);
        EventBean result = listener.assertOneGetNewAndReset();

        EPAssertionUtil.assertProps(result, new String[] { "entityName", "severity" }, new Object[] { "user1@fs.com", "Low" });
    }

    /**
     * Test rule for normal user with notification for event of medium severity
     * @throws Exception
     */
    @Test
    public void testSmartEventWithNormalUserAccountWithNotificationMediumSeverity() throws Exception{

        EPStatement stmt = initSmartEventWithNormalUserAccountWithNotification();
        //listener catches only events that pass the rule
        SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);

        EntityEvent entityEventMedium =      new EntityEvent(1234L,99,65,65,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
        Evidence notification = new Evidence(EntityType.User,"entityTypeFieldName","user1@fs.com", EvidenceType.Notification,12345L ,12345L +1,"anomalyTypeFieldName","anomalyValue",new ArrayList<String>(),75,Severity.Medium,3,EvidenceTimeframe.Hourly);

        epService.getEPRuntime().sendEvent(notification);
        epService.getEPRuntime().sendEvent(entityEventMedium);
        EventBean result = listener.assertOneGetNewAndReset();

        EPAssertionUtil.assertProps(result, new String[] { "entityName", "severity" }, new Object[] { "user1@fs.com", "Medium" });
    }

    /**
     * Test rule for normal user with notification for event of high severity
     * @throws Exception
     */
    @Test
    public void testSmartEventWithNormalUserAccountWithNotificationHighSeverity() throws Exception{

        EPStatement stmt = initSmartEventWithNormalUserAccountWithNotification();
        //listener catches only events that pass the rule
        SupportUpdateListener listener = new SupportUpdateListener();

        stmt.addListener(listener);

        EntityEvent entityEventMedium =      new EntityEvent(1234L,99,83,83,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
        Evidence notification = new Evidence(EntityType.User,"entityTypeFieldName","user1@fs.com", EvidenceType.Notification,12345L ,12345L +1,"anomalyTypeFieldName","anomalyValue",new ArrayList<String>(),80,Severity.High,3,EvidenceTimeframe.Hourly);

        epService.getEPRuntime().sendEvent(notification);
        epService.getEPRuntime().sendEvent(entityEventMedium);
        EventBean result = listener.assertOneGetNewAndReset();

        EPAssertionUtil.assertProps(result, new String[] { "entityName", "severity" }, new Object[] { "user1@fs.com", "High" });

    }

    /**
     * Test rule for normal user with notification for event of critical severity
     * @throws Exception
     */
    @Test
    public void testSmartEventWithNormalUserAccountWithNotificationCriticalSeverity() throws Exception{

        EPStatement stmt = initSmartEventWithNormalUserAccountWithNotification();
        //listener catches only events that pass the rule
        SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);

        EntityEvent entityEventCritical =      new EntityEvent(1234L,99,93,93,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
        Evidence notification = new Evidence(EntityType.User,"entityTypeFieldName","user1@fs.com", EvidenceType.Notification,12345L-5 ,12345L +5,"anomalyTypeFieldName","anomalyValue",new ArrayList<String>(),96,Severity.Critical,3,EvidenceTimeframe.Daily);

        epService.getEPRuntime().sendEvent(notification);
        epService.getEPRuntime().sendEvent(entityEventCritical);
        EventBean result = listener.assertOneGetNewAndReset();

        EPAssertionUtil.assertProps(result, new String[] { "entityName", "severity" }, new Object[] { "user1@fs.com", "Critical" });
    }

    /**
     * Negative test - test that rule is not triggered:1
     * On low score
     *
     * @throws Exception
     */
    @Test
    public void testSmartEventWithNormalUserAccountNegativeScore() throws Exception{

        EPStatement stmt = initSmartEventWithNormalUserAccountWithNotification();
        //listener catches only events that pass the rule
        SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);

        EntityEvent entityEventTooLow =  new EntityEvent(1234L,55,45,45,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
        Evidence notification = new Evidence(EntityType.User,"entityTypeFieldName","user1@fs.com", EvidenceType.Notification,12345L-5 ,12345L +5,"anomalyTypeFieldName","anomalyValue",new ArrayList<String>(),55,Severity.Low,3,EvidenceTimeframe.Daily);

        epService.getEPRuntime().sendEvent(notification);
        epService.getEPRuntime().sendEvent(entityEventTooLow);

        Assert.assertFalse(listener.getIsInvokedAndReset());
    }

    /**
     * Negative test - test that rule is not triggered:1
     * On "service" tag
     *
     * @throws Exception
     */
    @Test
    public void testSmartEventWithNormalUserAccountNegativeTag() throws Exception{

        EPStatement stmt = initSmartEventWithNormalUserAccountWithNotification();
        //listener catches only events that pass the rule
        SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);

        EntityEvent entityEventTooLow =  new EntityEvent(1234L,55,55,55,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
        Evidence notification = new Evidence(EntityType.User,"entityTypeFieldName","user1@fs.com", EvidenceType.Notification,12345L-5 ,12345L +5,"anomalyTypeFieldName","anomalyValue",new ArrayList<String>(),55,Severity.Low,3,EvidenceTimeframe.Daily);

        List<String> userTags = new ArrayList<>();
        userTags.add("service");
        EntityTags entityTags = new EntityTags(EntityType.User,"user1@fs.com",userTags);

        epService.getEPRuntime().sendEvent(entityTags);
        epService.getEPRuntime().sendEvent(notification);
        epService.getEPRuntime().sendEvent(entityEventTooLow);

        Assert.assertFalse(listener.getIsInvokedAndReset());
    }

    /**
     * Test normal user rule on user with notifiation, and with tag which is not admin, executive or service.
     * @throws Exception
     */
    @Test
		 public void testSmartEventWithNormalUserAccountWithNotificationOtherTag() throws Exception{


		EPStatement stmt = initSmartEventWithSensetiveUserAccountWithNotification();
		//listener catches only events that pass the rule
		SupportUpdateListener listener = new SupportUpdateListener();
		stmt.addListener(listener);

		EntityEvent entityEventLow =  new EntityEvent(1234L,99,55,55,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
		Evidence notification = new Evidence(EntityType.User,"entityTypeFieldName","user1@fs.com", EvidenceType.Notification,12345L ,12345L +1,"anomalyTypeFieldName","anomalyValue",new ArrayList<String>(),65,Severity.Low,3,EvidenceTimeframe.Hourly);

		List<String> userTags = new ArrayList<>();
		userTags.add("LR");
		EntityTags entityTags = new EntityTags(EntityType.User,"user1@fs.com",userTags);

		epService.getEPRuntime().sendEvent(notification);
		epService.getEPRuntime().sendEvent(entityTags);
		epService.getEPRuntime().sendEvent(entityEventLow);
		EventBean result = listener.assertOneGetNewAndReset();

		EPAssertionUtil.assertProps(result, new String[] { "entityName", "severity" }, new Object[] { "user1@fs.com", "Low" });
	}

    /**
     * Test normal user rule on user with notifiation, and with tag which is not admin, executive or service.
     * @throws Exception
     */
    @Test
    public void testSmartEventWithCustomTaggedUserAccountWithNotification() throws Exception{


        EPStatement stmt = initSmartEventWithSensetiveUserAccountWithNotification();
        //listener catches only events that pass the rule
        SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);

        EntityEvent entityEventLow =  new EntityEvent(1234L,99,55,55,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
        Evidence notification = new Evidence(EntityType.User,"entityTypeFieldName","user1@fs.com", EvidenceType.Notification,12345L ,12345L +1,"anomalyTypeFieldName","anomalyValue",new ArrayList<String>(),65,Severity.Low,3,EvidenceTimeframe.Hourly);

        List<String> userTags = new ArrayList<>();
        userTags.add("test");
        EntityTags entityTags = new EntityTags(EntityType.User,"user1@fs.com",userTags);

        epService.getEPRuntime().sendEvent(notification);
        epService.getEPRuntime().sendEvent(entityTags);
        epService.getEPRuntime().sendEvent(entityEventLow);
        EventBean result = listener.assertOneGetNewAndReset();

        EPAssertionUtil.assertProps(result, new String[]{"entityName", "severity"}, new Object[]{"user1@fs.com", "Low"});
    }

	@Test
	public void testSmartEventWithNormalUserAccountWithoutNotifications() throws Exception{

		EPStatement stmt = initSmartEventWithNormalUserAccountWithoutNotification();
		//listener catches only events that pass the rule
		SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);

		EntityEvent entityEventLow =  new EntityEvent(1234L,99,98,98,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);

		epService.getEPRuntime().sendEvent(entityEventLow);
		EventBean result = listener.assertOneGetNewAndReset();

		EPAssertionUtil.assertProps(result, new String[]{"entityName", "severity"}, new Object[]{"user1@fs.com", "Critical"});

	}

    /**
     * Test normal user rule on user without notification (other user have notification), and with tag which is not admin, executive or service.
     * @throws Exception
     */
    @Test
    public void testSmartEventWithCustomTaggedUserAndAdminAccountWithoutNotification() throws Exception{

        EPStatement stmt = initSmartEventWithSensetiveUserAccountWithoutNotification();
        //listener catches only events that pass the rule
        SupportUpdateListener listener = new SupportUpdateListener();

        stmt.addListener(listener);

        EntityEvent entityEventLow =  new EntityEvent(1234L,99,55,55,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);

        List<String> userTags = new ArrayList<>();
        userTags.add("test");
        userTags.add("admin");
        EntityTags entityTags = new EntityTags(EntityType.User,"user1@fs.com",userTags);

        epService.getEPRuntime().sendEvent(entityTags);
        epService.getEPRuntime().sendEvent(entityEventLow);
        EventBean result = listener.assertOneGetNewAndReset();

        EPAssertionUtil.assertProps(result, new String[] { "entityName", "severity" }, new Object[] { "user1@fs.com", "Low" });

    }

	/**
	 * Test normal user rule on user without notification (other user have notification), and with tag which is not admin, executive or service.
	 * @throws Exception
	 */
	@Test
	public void testSmartEventWithCustomTaggedUserAccountWithoutNotification() throws Exception{

		EPStatement stmt = initSmartEventWithSensetiveUserAccountWithoutNotification();
		//listener catches only events that pass the rule
		SupportUpdateListener listener = new SupportUpdateListener();

		stmt.addListener(listener);

		EntityEvent entityEventLow =  new EntityEvent(1234L,99,55,55,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
		List<String> userTags = new ArrayList<>();
		userTags.add("test");
		EntityTags entityTags = new EntityTags(EntityType.User,"user1@fs.com",userTags);

		epService.getEPRuntime().sendEvent(entityTags);
		epService.getEPRuntime().sendEvent(entityEventLow);
		EventBean result = listener.assertOneGetNewAndReset();

		EPAssertionUtil.assertProps(result, new String[] { "entityName", "severity" }, new Object[] { "user1@fs.com", "Low" });
	}

    /**
     * Test normal user rule on user without notification (other user have notification), and with tag which is not admin, executive or service.
     * @throws Exception
     */
    @Test
    public void testSmartEventWithNormalUserAccountWithoutNotificationOtherTag() throws Exception{

        EPStatement stmt = initSmartEventWithSensetiveUserAccountWithoutNotification();
        //listener catches only events that pass the rule
        SupportUpdateListener listener = new SupportUpdateListener();

        stmt.addListener(listener);

        EntityEvent entityEventLow =  new EntityEvent(1234L,99,55,55,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
        List<String> userTags = new ArrayList<>();
        userTags.add("LR");
        EntityTags entityTags = new EntityTags(EntityType.User,"user1@fs.com",userTags);

        epService.getEPRuntime().sendEvent(entityTags);
        epService.getEPRuntime().sendEvent(entityEventLow);
        EventBean result = listener.assertOneGetNewAndReset();

        EPAssertionUtil.assertProps(result, new String[] { "entityName", "severity" }, new Object[] { "user1@fs.com", "Low" });
    }

    /**
     * Test normal user rule on user with notifiation, and with tag which is not admin, executive or service.
     * @throws Exception
     */
    @Test
    public void testSmartEventWithSensetiveUserOnNormalUserRule() throws Exception{

        EPStatement stmt = initSmartEventWithNormalUserAccountWithNotification();
        //listener catches only events that pass the rule
        SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);

        EntityEvent entityEventLow = new EntityEvent(1234L,99,55,55,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
        Evidence notification = new Evidence(EntityType.User,"entityTypeFieldName","user1@fs.com", EvidenceType.Notification,12345L ,12345L +1,"anomalyTypeFieldName","anomalyValue",new ArrayList<String>(),65,Severity.Low,3,EvidenceTimeframe.Hourly);

        List<String> userTags = new ArrayList<>();
        userTags.add("admin");
        EntityTags entityTags = new EntityTags(EntityType.User,"user1@fs.com",userTags);

        epService.getEPRuntime().sendEvent(notification);
        epService.getEPRuntime().sendEvent(entityTags);
        epService.getEPRuntime().sendEvent(entityEventLow);
        EPAssertionUtil.assertAllBooleanTrue(new Boolean[] { !listener.isInvoked() });

    }

    /**
     * tests title has hourly/daily resolution, in respect to the entity event name.
     * tests 8 scenarios: normal user with and without notifications - hourly and daily, and sensitive user with and without notifications - hourly and daily.
     * @throws Exception
     */
    @Test
    public void testTitleHourlyOrDaily() throws Exception{

        //-----normal user without notifications

        EPStatement stmt =  initSmartEventWithNormalUserAccountWithoutNotification();
        //listener catches only events that pass the rule
        SupportUpdateListener listener = new SupportUpdateListener();
        stmt.addListener(listener);

        EntityEvent entityEventLowHourly =      new EntityEvent(1234L,99,55,55,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_HOURLY);
        epService.getEPRuntime().sendEvent(entityEventLowHourly);
        EventBean result = listener.assertOneGetNewAndReset();
        EPAssertionUtil.assertProps(result, new String[] { "entityName", "severity", "title" }, new Object[] { "user1@fs.com", "Low", "Suspicious Hourly Activity For User Account" });

        EntityEvent entityEventLowDaily =      new EntityEvent(1234L,99,55,55,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>(),ENTITY_EVENT_NAME_DAILY);
        epService.getEPRuntime().sendEvent(entityEventLowDaily);
        result = listener.assertOneGetNewAndReset();
        EPAssertionUtil.assertProps(result, new String[] { "entityName", "severity", "title" }, new Object[] { "user1@fs.com", "Low", "Suspicious Daily Activity For User Account" });

        //----normal user with notification

        stmt = initSmartEventWithNormalUserAccountWithNotification();
        listener = new SupportUpdateListener();
        stmt.addListener(listener);
        Evidence notification = new Evidence(EntityType.User,"entityTypeFieldName","user1@fs.com", EvidenceType.Notification,12345L ,12345L +1,"anomalyTypeFieldName","anomalyValue",new ArrayList<String>(),65,Severity.Low,3,EvidenceTimeframe.Hourly);
        epService.getEPRuntime().sendEvent(notification);

        epService.getEPRuntime().sendEvent(entityEventLowHourly);
        result = listener.assertOneGetNewAndReset();
        EPAssertionUtil.assertProps(result, new String[] { "entityName", "severity", "title" }, new Object[] { "user1@fs.com", "Low", "Suspicious Hourly Activity For User Account" });

        //-----sensitive user without notifications

        stmt = initSmartEventWithSensetiveUserAccountWithoutNotification();
        listener = new SupportUpdateListener();
        stmt.addListener(listener);

        List<String> userTags = new ArrayList<>();
        userTags.add("admin");
        EntityTags entityTags = new EntityTags(EntityType.User,"user1@fs.com",userTags);
        epService.getEPRuntime().sendEvent(entityTags);

        epService.getEPRuntime().sendEvent(entityEventLowHourly);
        result = listener.assertOneGetNewAndReset();
        EPAssertionUtil.assertProps(result, new String[] { "entityName", "severity", "title" }, new Object[] { "user1@fs.com", "Low", "Suspicious Hourly Privileged Account Activity" });

        epService.getEPRuntime().sendEvent(entityEventLowDaily);
        result = listener.assertOneGetNewAndReset();
        EPAssertionUtil.assertProps(result, new String[] { "entityName", "severity", "title" }, new Object[] { "user1@fs.com", "Low", "Suspicious Daily Privileged Account Activity" });

        //---sensitive user with notification

        stmt = initSmartEventWithSensetiveUserAccountWithNotification();
        listener = new SupportUpdateListener();
        stmt.addListener(listener);
        epService.getEPRuntime().sendEvent(notification);
        epService.getEPRuntime().sendEvent(entityTags);
        epService.getEPRuntime().sendEvent(entityEventLowHourly);
        result = listener.assertOneGetNewAndReset();
        EPAssertionUtil.assertProps(result, new String[] { "entityName", "severity", "title" }, new Object[] { "user1@fs.com", "Low", "Suspicious Hourly Privileged Account Activity" });

        epService.getEPRuntime().sendEvent(entityEventLowDaily);
        result = listener.assertOneGetNewAndReset();
        EPAssertionUtil.assertProps(result, new String[] { "entityName", "severity", "title" }, new Object[] { "user1@fs.com", "Low", "Suspicious Daily Privileged Account Activity" });

    }

    /**
     * Create esper statement (rule) for event on normal users with score above 50, with and without notification
     * Normal users are users which don't have "admin", "service", or "executive" tags.
     * @return esper statment
     */
    private EPStatement initSmartEventWithNormalUserAccountWithoutNotification() {
        epService.destroy();
        epService.initialize();
        epService.getEPAdministrator().destroyAllStatements();

        long currentTimeStamp  = new Date().getTime();

        String createTimestamp = "create variable Long currentTimestamp ="+(currentTimeStamp+(60*60*1000+60*60*1000)); // time now + 2 hours
        String createLastEventTimestamp = "create variable Long lastEventTimestamp =" + (currentTimeStamp +(60*30*1000)); // half hour greater then the current timestamp

        String enrichedEntityEvent = CREATE_ENRICHED_ENTITY_EVENT;
        String enrichEvidence = CREATE_ENRICHED_EVIDENCE;
        String hourlyContextByUser = CREATE_HOURLY_CONTEXT_BY_USER;

        String jokerNormalUserAccount = "select"
                + " case when SmartEvent.entity_event_name = 'normalized_username_hourly' then 'Suspicious Hourly Activity For User Account'"
                +" when  SmartEvent.entity_event_name = 'normalized_username_daily' then 'Suspicious Daily Activity For User Account'"
                +" end as title,"
                + " SmartEvent.entityType.toString() || '-' ||SmartEvent.entityName    as concatString,"
                + "case "
                + "when  (SmartEvent.score >= 50 and  SmartEvent.score < 70) then 'Low' "
                + "when  (SmartEvent.score >= 70 and  SmartEvent.score < 80) then 'Medium' "
                + "when  (SmartEvent.score >= 80 and  SmartEvent.score < 95) then 'High' "
                + "when  (SmartEvent.score >= 95) then 'Critical' "
                + "end as severity , "
                + "SmartEvent.entityType as entityType, SmartEvent.entityName as entityName, aggregated_feature_events, start_time_unix, end_time_unix, SmartEvent.score * 1.0 as score from EnrichedEntityEvent(score >= 50).std:groupwin(entityType,entityName).std:lastevent() as SmartEvent "
                + "where SmartEvent.entityType.toString() || '-' ||SmartEvent.entityName not in (select entityType.toString() || '-' ||entityName from EntityTags.std:groupwin(entityType,entityName).std:lastevent())"
                + "and SmartEvent.entityType.toString() || '-' ||SmartEvent.entityName not in (select entityType.toString() || '-' ||entityName from EnrichedEvidence(evidenceType = EvidenceType.Notification).win:expr_batch(oldest_timestamp+(60*60*1000+30*60*1000) < currentTimestamp or (oldest_event.hourlyStartDate is not null and lastEventTimestamp > 30*60*1000+hourEndTimestamp(oldest_event.hourlyStartDate))).std:lastevent())";



        epService.getEPAdministrator().createEPL(createTimestamp);
        epService.getEPAdministrator().createEPL(createLastEventTimestamp);
        epService.getEPAdministrator().createEPL(enrichEvidence);
        epService.getEPAdministrator().createEPL(enrichedEntityEvent);
        epService.getEPAdministrator().createEPL(hourlyContextByUser);

        return epService.getEPAdministrator().createEPL(jokerNormalUserAccount);
    }

    /**
     * Create esper statement (rule) for event on normal users with score above 50, with and without notification
     * Normal users are users which don't have "admin", "service", or "executive" tags.
     * @return esper statment
     */
    private EPStatement initSmartEventWithNormalUserAccountWithNotification() {
        epService.destroy();
        epService.initialize();
        epService.getEPAdministrator().destroyAllStatements();

        long currentTimeStamp  = new Date().getTime();

        String createTimestamp = "create variable Long currentTimestamp ="+(currentTimeStamp+(60*60*1000+60*60*1000)); // time now + 2 hours
        String createLastEventTimestamp = "create variable Long lastEventTimestamp =" + (currentTimeStamp +(60*30*1000)); // half hour greater then the current timestamp

        String enrichedEntityEvent = CREATE_ENRICHED_ENTITY_EVENT;
        String enrichEvidence = CREATE_ENRICHED_EVIDENCE;
        String hourlyContextByUser = CREATE_HOURLY_CONTEXT_BY_USER;


        String jokerNormalUserAccount = "select"
                + " case when SmartEvent.entity_event_name = 'normalized_username_hourly' then 'Suspicious Hourly Activity For User Account'"
                +" when  SmartEvent.entity_event_name = 'normalized_username_daily' then 'Suspicious Daily Activity For User Account'"
                +" end as title,"
                + "case "
                + "when  (SmartEvent.score >= 50 and  SmartEvent.score < 60) then 'Low' "
                + "when  (SmartEvent.score >= 60 and  SmartEvent.score < 70) then 'Medium' "
                + "when  (SmartEvent.score >= 70 and  SmartEvent.score < 85) then 'High' "
                + "when  (SmartEvent.score >= 85) then 'Critical' "
                + "end as severity , "
                + "SmartEvent.entityType as entityType, SmartEvent.entityName as entityName, aggregated_feature_events, start_time_unix, end_time_unix, SmartEvent.score * 1.0 as score from EnrichedEntityEvent(score >= 50).std:groupwin(entityType,entityName).std:lastevent() as SmartEvent "
                + "where SmartEvent.entityType.toString() || '-' ||SmartEvent.entityName not in (select entityType.toString() || '-' ||entityName from EntityTags.std:groupwin(entityType,entityName).std:lastevent())"
                + "and SmartEvent.entityType.toString() || '-' ||SmartEvent.entityName in (select entityType.toString() || '-' ||entityName from EnrichedEvidence(evidenceType = EvidenceType.Notification).win:expr_batch(oldest_timestamp+(60*60*1000+30*60*1000) < currentTimestamp or (oldest_event.hourlyStartDate is not null and lastEventTimestamp > 30*60*1000+hourEndTimestamp(oldest_event.hourlyStartDate))).std:lastevent())";


        epService.getEPAdministrator().createEPL(createTimestamp);
        epService.getEPAdministrator().createEPL(createLastEventTimestamp);
        epService.getEPAdministrator().createEPL(enrichEvidence);
        epService.getEPAdministrator().createEPL(enrichedEntityEvent);
        epService.getEPAdministrator().createEPL(hourlyContextByUser);


        return epService.getEPAdministrator().createEPL(jokerNormalUserAccount);
    }

	/**
	 * Create esper statement (rule) for event on sensitive users with score above 50, with and without notification
	 * Normal users are users which don't have "admin", "service", or "executive" tags.
	 * @return esper statment
	 */
	private EPStatement initSmartEventWithSensetiveUserAccountWithNotification() {
		epService.destroy();
		epService.initialize();
		epService.getEPAdministrator().destroyAllStatements();

		long currentTimeStamp  = new Date().getTime();

		String createTimestamp = "create variable Long currentTimestamp ="+(currentTimeStamp+(60*60*1000+60*60*1000)); // time now + 2 hours
		String createLastEventTimestamp = "create variable Long lastEventTimestamp =" + (currentTimeStamp +(60*30*1000)); // half hour greater then the current timestamp

		String enrichedEntityEvent = CREATE_ENRICHED_ENTITY_EVENT;
		String enrichEvidence = CREATE_ENRICHED_EVIDENCE;
		String hourlyContextByUser = CREATE_HOURLY_CONTEXT_BY_USER;

		String jokerNormalUserAccount = "select case when SmartEvent.entity_event_name = 'normalized_username_hourly' and ('admin' = any(tags) or 'executive' = any(tags) or 'service' = any(tags) ) then 'Suspicious Hourly Privileged Account Activity' when SmartEvent.entity_event_name = 'normalized_username_daily' and ('admin' = any(tags) or 'executive' = any(tags) or 'service' = any(tags) ) then 'Suspicious Daily Privileged Account Activity' when SmartEvent.entity_event_name = 'normalized_username_hourly' then 'Suspicious Hourly User Activity' when SmartEvent.entity_event_name = 'normalized_username_daily' then 'Suspicious Daily User Activity' end as title, case when (SmartEvent.score in [50:60) and ('admin' = any(tags) or 'executive' = any(tags) or 'service' = any(tags) )) then 'Low' when (SmartEvent.score in [50:60) and ('admin' <> any(tags) and 'executive' <> any(tags) and 'service' <> any(tags) )) then 'Low' when (SmartEvent.score in [60:70) and ('admin' = any(tags) or 'executive' = any(tags) or 'service' = any(tags) )) then 'Medium' when (SmartEvent.score in [60:70) and ('admin' <> any(tags) and 'executive' <> any(tags) and 'service' <> any(tags) )) then 'Medium' when (SmartEvent.score in [70:85) and ('admin' = any(tags) or 'executive' = any(tags) or 'service' = any(tags) )) then 'High' when (SmartEvent.score in [70:85) and ('admin' <> any(tags) and 'executive' <> any(tags) and 'service' <> any(tags) )) then 'High' when (SmartEvent.score >= 85 and ('admin' = any(tags) or 'executive' = any(tags) or 'service' = any(tags) )) then 'Critical' when (SmartEvent.score >= 85 and ('admin' <> any(tags) and 'executive' <> any(tags) and 'service' <> any(tags) )) then 'Critical' end as severity, Tags.entityType as entityType, Tags.entityName as entityName, aggregated_feature_events, start_time_unix, end_time_unix, SmartEvent.score * 1.0 as score, Tags.tags as tags from EnrichedEntityEvent(score >= 50).std:groupwin(entityName,entityType).std:lastevent() as SmartEvent inner join EntityTags.std:groupwin(entityType,entityName).std:lastevent() as Tags  on SmartEvent.entityName = Tags.entityName and SmartEvent.entityType = Tags.entityType  where SmartEvent.entityType.toString() || '-' ||SmartEvent.entityName  in (select entityType.toString() || '-' ||entityName from EnrichedEvidence(evidenceType = EvidenceType.Notification).win:expr_batch(oldest_timestamp+(60*60*1000+30*60*1000) < currentTimestamp or (oldest_event.hourlyStartDate is not null and lastEventTimestamp > 30*60*1000+hourEndTimestamp(oldest_event.hourlyStartDate))).std:lastevent())";


		epService.getEPAdministrator().createEPL(createTimestamp);
		epService.getEPAdministrator().createEPL(createLastEventTimestamp);
		epService.getEPAdministrator().createEPL(enrichEvidence);
		epService.getEPAdministrator().createEPL(enrichedEntityEvent);
		epService.getEPAdministrator().createEPL(hourlyContextByUser);


		return epService.getEPAdministrator().createEPL(jokerNormalUserAccount);
	}

	/**
	 * Create esper statement (rule) for event on sensitive users with score above 50, with and without notification
	 * Normal users are users which don't have "admin", "service", or "executive" tags.
	 * @return esper statment
	 */
	private EPStatement initSmartEventWithSensetiveUserAccountWithoutNotification() {
		epService.destroy();
		epService.initialize();
		epService.getEPAdministrator().destroyAllStatements();

		long currentTimeStamp  = new Date().getTime();

		String createTimestamp = "create variable Long currentTimestamp ="+(currentTimeStamp+(60*60*1000+60*60*1000)); // time now + 2 hours
		String createLastEventTimestamp = "create variable Long lastEventTimestamp =" + (currentTimeStamp +(60*30*1000)); // half hour greater then the current timestamp

		String enrichedEntityEvent = CREATE_ENRICHED_ENTITY_EVENT;
		String enrichEvidence = CREATE_ENRICHED_EVIDENCE;
		String hourlyContextByUser =CREATE_HOURLY_CONTEXT_BY_USER;


		String jokerNormalUserAccount ="select case when SmartEvent.entity_event_name = 'normalized_username_hourly' and ('admin' = any(tags) or 'executive' = any(tags) or 'service' = any(tags) ) then 'Suspicious Hourly Privileged Account Activity' when SmartEvent.entity_event_name = 'normalized_username_daily' and ('admin' = any(tags) or 'executive' = any(tags) or 'service' = any(tags) ) then 'Suspicious Daily Privileged Account Activity' when SmartEvent.entity_event_name = 'normalized_username_hourly' then 'Suspicious Hourly User Activity' when SmartEvent.entity_event_name = 'normalized_username_daily' then 'Suspicious Daily User Activity' end as title, case when (SmartEvent.score in [50:65) and ('admin' = any(tags) or 'executive' = any(tags) or 'service' = any(tags) )) then 'Low' when (SmartEvent.score in [50:70) and ('admin' <> any(tags) and 'executive' <> any(tags) and 'service' <> any(tags) )) then 'Low' when (SmartEvent.score in [65:75) and ('admin' = any(tags) or 'executive' = any(tags) or 'service' = any(tags) )) then 'Medium' when (SmartEvent.score in [70:80) and ('admin' <> any(tags) and 'executive' <> any(tags) and 'service' <> any(tags) )) then 'Medium' when (SmartEvent.score in [75:90) and ('admin' = any(tags) or 'executive' = any(tags) or 'service' = any(tags) )) then 'High' when (SmartEvent.score in [80:95) and ('admin' <> any(tags) and 'executive' <> any(tags) and 'service' <> any(tags) )) then 'High' when (SmartEvent.score >= 90 and ('admin' = any(tags) or 'executive' = any(tags) or 'service' = any(tags) )) then 'Critical' when (SmartEvent.score >= 95 and ('admin' <> any(tags) and 'executive' <> any(tags) and 'service' <> any(tags) )) then 'Critical' end as severity, Tags.entityType as entityType, Tags.entityName as entityName, aggregated_feature_events, start_time_unix, end_time_unix, SmartEvent.score * 1.0 as score, Tags.tags as tags from EnrichedEntityEvent(score >= 50).std:groupwin(entityName,entityType).std:lastevent() as SmartEvent inner join EntityTags.std:groupwin(entityType,entityName).std:lastevent() as Tags  on SmartEvent.entityName = Tags.entityName and SmartEvent.entityType = Tags.entityType  where SmartEvent.entityType.toString() || '-' ||SmartEvent.entityName not in (select entityType.toString() || '-' ||entityName from EnrichedEvidence(evidenceType = EvidenceType.Notification).win:expr_batch(oldest_timestamp+(60*60*1000+30*60*1000) < currentTimestamp or (oldest_event.hourlyStartDate is not null and lastEventTimestamp > 30*60*1000+hourEndTimestamp(oldest_event.hourlyStartDate))).std:lastevent())";


		epService.getEPAdministrator().createEPL(createTimestamp);
		epService.getEPAdministrator().createEPL(createLastEventTimestamp);
		epService.getEPAdministrator().createEPL(enrichEvidence);
		epService.getEPAdministrator().createEPL(enrichedEntityEvent);
		epService.getEPAdministrator().createEPL(hourlyContextByUser);


		return epService.getEPAdministrator().createEPL(jokerNormalUserAccount);
	}


    /**
     * Create esper statement (rule) for event on sensitive users with score above 50, with and without notification
     * Normal users are users which don't have "admin", "service", or "executive" tags.
     * @return esper statment
     */
    private EPStatement initEnrichedFortscaleEventHourly() {
        epService.destroy();
        epService.initialize();
        epService.getEPAdministrator().destroyAllStatements();

        long currentTimeStamp  = new Date().getTime();

        String createTimestamp = "create variable Long currentTimestamp ="+(currentTimeStamp+(60*60*1000+60*60*1000)); // time now + 2 hours
        String createLastEventTimestamp = "create variable Long lastEventTimestamp =" + (currentTimeStamp +(60*30*1000)); // half hour greater then the current timestamp

        String hourlyContextByUser = CREATE_HOURLY_CONTEXT_BY_USER_EnrichedFortscaleEvent;

        String jokerNormalUserAccount = "context HourlyTimeFrame select 'Suspicious hourly activity' as title, entityType,entityName, hourlyStartDate as startDate,hourEndTimestamp(hourlyStartDate) as endDate, window(*) as eventList, avg(score) as score  from EnrichedFortscaleEvent.win:expr_batch(oldest_timestamp+(10*60*1000) < currentTimestamp or (oldest_event.hourlyStartDate is not null and lastEventTimestamp > 30*60*1000+hourEndTimestamp(oldest_event.hourlyStartDate))) having count(*) > 0";


        epService.getEPAdministrator().createEPL(createTimestamp);
        epService.getEPAdministrator().createEPL(createLastEventTimestamp);
        epService.getEPAdministrator().createEPL(CREATE_ENRICHED_FORTSCALE_EVENT_BY_NOTIFICATIONS);
        epService.getEPAdministrator().createEPL(CREATE_ENRICHED_FORTSCALE_EVENT_BY_SMART_EVENTS);

        epService.getEPAdministrator().createEPL(hourlyContextByUser);

        return epService.getEPAdministrator().createEPL(jokerNormalUserAccount);
    }




}


