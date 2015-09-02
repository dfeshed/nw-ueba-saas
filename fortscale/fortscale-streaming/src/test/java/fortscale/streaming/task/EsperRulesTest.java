package fortscale.streaming.task;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.scopetest.EPAssertionUtil;
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
		esperConfig.addPlugInSingleRowFunction("extractNormalizedUsernameFromContextId", RuleUtils.class.getName(),"extractNormalizedUsernameFromContextId");
		esperConfig.addEventType(EntityEvent.class);
		esperConfig.addEventType(EntityTags.class);

		epService = EPServiceProviderManager.getDefaultProvider(esperConfig);
		MockitoAnnotations.initMocks(this);

	}

	/**
	 * test rule 'fortscale.esper.rule.statement._2_3_SmartEventWithSensitiveAccount'.
	 * tests that esper rule catch sensitive users with smart events ( also known as entityEvent), and that the rule doesn't catch extra.
	 * @throws Exception
	 */
	@Test
	public void testSmartEventWithSensitiveAccountTest() throws Exception{
		String contextEntityEvent = "create context EntityEventFrame partition by contextId from EntityEvent";
		String SmartEventWithSensitiveAccount = "select 'Suspicious Activity For Sensitive Account' as title, Tags.entityType as entityType, Tags.entityName as entityName, aggregated_feature_events, start_time_unix, end_time_unix, score * 1.0 as score, Tags.tags as tags from EntityEvent(score > 50).std:groupwin(contextId).win:time(1 sec) as SmartEvent, EntityTags.std:groupwin(entityType,entityName).std:lastevent() as Tags where extractNormalizedUsernameFromContextId(contextId) = Tags.entityName and (('admin' = any(Tags.tags)) or ('executive' = any(Tags.tags)) or('service' = any(Tags.tags)))";
		EPStatement stmt = epService.getEPAdministrator().createEPL(SmartEventWithSensitiveAccount);

		//listener catches only events that pass the rule
		SupportUpdateListener listener = new SupportUpdateListener();
		stmt.addListener(listener);
		EntityEvent entityEventGood = new EntityEvent(1234L,99,"event_type",99,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>());
		EntityEvent entityEventNotSensitive = new EntityEvent(1234L,99,"event_type",99,new HashMap<String,String>(),"normalized_username_user2@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>());
		EntityEvent entityEventNoScore = new EntityEvent(1234L,99,"event_type",0,new HashMap<String,String>(),"normalized_username_user1@fs.com",12345L,12345L,"entity_event_type",12345L,new ArrayList<JSONObject>());

		List<String> userTags = new ArrayList<>();
		userTags.add("admin");

		EntityTags entityTags = new EntityTags(EntityType.User,"user1@fs.com",userTags);
		epService.getEPRuntime().sendEvent(entityTags);
		epService.getEPRuntime().sendEvent(entityEventGood);
		EPAssertionUtil.assertProps(listener.assertOneGetNewAndReset(), new String[] { "entityName" }, new Object[] { "user1@fs.com" });

		epService.getEPRuntime().sendEvent(entityTags);
		epService.getEPRuntime().sendEvent(entityEventNotSensitive);
		assertFalse(listener.getAndClearIsInvoked());

		epService.getEPRuntime().sendEvent(entityTags);
		epService.getEPRuntime().sendEvent(entityEventNoScore);
		assertFalse(listener.getAndClearIsInvoked());


	}
}
