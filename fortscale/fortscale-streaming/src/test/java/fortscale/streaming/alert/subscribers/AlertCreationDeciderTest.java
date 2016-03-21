package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.EntityType;
import fortscale.domain.core.User;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.evidence.decider.Decider;
import fortscale.streaming.alert.subscribers.evidence.decider.DeciderCommand;
import junit.framework.TestCase;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import net.minidev.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;

/**
 * Created by tomerd on 29/02/2016.
 */
@RunWith(JUnitParamsRunner.class)
public class AlertCreationDeciderTest {



/*	@Test
	@Parameters
	public void testDecider(String testCase, Object[] lines, Object[] expected) throws Exception {
		Set<String> featuresSet = new HashSet<>();
		featuresSet.add("fortscale.streaming.alert.subscribers.evidence.decider.PriorityDeciderImpl");
		featuresSet.add("fortscale.streaming.alert.subscribers.evidence.decider.ScoreDeciderImpl");
		featuresSet.add("fortscale.streaming.alert.subscribers.evidence.decider.FreshnessDeciderImpl");

		//build input
		Decider decider = new Decider(featuresSet);
		List<EnrichedFortscaleEvent> evidencesEligibleForDecider = new ArrayList<>();
		for (Object line : lines){
			Map event = new HashMap<>();
			Integer lineSize = ((Object[])line).length;
			for (int i=0; i<lineSize; i++ ) {
				Object[] propertyPair = (Object[])((Object[])line)[i];
				event.put(propertyPair[0], propertyPair[1]);
			}
			evidencesEligibleForDecider.add(event);
		}
		//build expected
		String expectedName = (String)expected[0];
		Integer expectedScore = (Integer)expected[1];




		LinkedList<DeciderCommand> deciderLinkedList = decider.getDecidersLinkedList();
		DeciderCommand deciderCommand = deciderLinkedList.getFirst();
		String name = deciderCommand.getName(evidencesEligibleForDecider, deciderLinkedList);
		Integer score = deciderCommand.getScore(evidencesEligibleForDecider, deciderLinkedList);
		assertEquals("failed with Alert name", expectedName, name);
		assertEquals("failed with Alert score", expectedScore, score);
	}


	@SuppressWarnings("unused")
	private Object[] parametersForTestDecider() {
		return
				$(
						$ (
								"Take Name from vpn_geo_hopping and score from smart",
								$(
										$(
												$("dailyStartDate", 1456617600000L),
												$("hourlyStartDate", 1456686000000L),
												$("score", 90),
												$("anomalyTypeFieldName", "smart"),
												$("entityType", EntityType.User),
												$("entityName", "geohop2@somebigcompany.com"),
												$("eventTime", 1456686833000L),
												$("id", "49025258-8bd7-4f44-81fa-780d01b4b059")
										),
										$(
												$("dailyStartDate", 1456617600000L),
												$("hourlyStartDate", 1456686000000L),
												$("score", 70),
												$("anomalyTypeFieldName", "vpn_geo_hopping"),
												$("entityType", EntityType.User),
												$("entityName", "geohop2@somebigcompany.com"),
												$("eventTime", 1456686833000L),
												$("id", "49025258-8bd7-4f44-81fa-780d01b4b059")
										),
										$(
												$("dailyStartDate", 1456617600000L),
												$("hourlyStartDate", 1456686000000L),
												$("score", 80),
												$("anomalyTypeFieldName", "vpn_geo_hopping"),
												$("entityType", EntityType.User),
												$("entityName", "geohop2@somebigcompany.com"),
												$("eventTime", 1456686833000L),
												$("id", "49025258-8bd7-4f44-81fa-780d01b4b059")
										)
								),
								$(
										$(
												"vpn_geo_hopping",
												90
										)
								)
						),
						$ (
								"Take Name from vpn_geo_hopping and score from smart. two vpn events with same score, go to the freshness decider",
								$(
										$(
												$("dailyStartDate", 1456617600000L),
												$("hourlyStartDate", 1456686000000L),
												$("score", 70),
												$("anomalyTypeFieldName", "smart"),
												$("entityType", EntityType.User),
												$("entityName", "geohop2@somebigcompany.com"),
												$("eventTime", 1456686833000L),
												$("id", "49025258-8bd7-4f44-81fa-780d01b4b059")
										),
										$(
												$("dailyStartDate", 1456617600000L),
												$("hourlyStartDate", 1456686000000L),
												$("score", 90),
												$("anomalyTypeFieldName", "vpn_geo_hopping"),
												$("entityType", EntityType.User),
												$("entityName", "geohop2@somebigcompany.com"),
												$("eventTime", 1456686833000L),
												$("id", "49025258-8bd7-4f44-81fa-780d01b4b059")
										),
										$(
												$("dailyStartDate", 1456617600000L),
												$("hourlyStartDate", 1456686000000L),
												$("score", 90),
												$("anomalyTypeFieldName", "vpn_geo_hopping"),
												$("entityType", EntityType.User),
												$("entityName", "geohop2@somebigcompany.com"),
												$("eventTime", 1456686833000L),
												$("id", "49025258-8bd7-4f44-81fa-780d01b4b059")
										)
								),
								$(
										$(
												"vpn_geo_hopping",
												70
										)
								)
						)
				);
	}*/
}