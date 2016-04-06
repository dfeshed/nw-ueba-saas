package fortscale.streaming.alert.subscribers;

import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.evidence.decider.DeciderCommand;
import fortscale.streaming.alert.subscribers.evidence.decider.AlertTypeConfigurationServiceImpl;
import fortscale.streaming.alert.subscribers.evidence.decider.AlertDeciderServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by tomerd on 29/02/2016.
 */


@RunWith(MockitoJUnitRunner.class)
public class AlertCreationDeciderTest {

	@Mock
	public AlertTypeConfigurationServiceImpl conf;

	@Before
	public void setUp(){
		//Return the alert name as paramter that passed (anomalyType)
		Mockito.when(conf.getAlertNameByAnonalyType(Mockito.any())).thenAnswer(new Answer<Object>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return (String) args[0];
			}
		});
	}


	/**
	 * In this test the first decider return 1 result,
	 * so the second decide is not called
	 */
	@Test
	public void nameDecider1Test(){

		DeciderCommand deciderCommand1 = Mockito.mock(DeciderCommand.class);
		DeciderCommand deciderCommand2 = Mockito.mock(DeciderCommand.class);

		//deciderCommand1 return array of one list
		Mockito.when(deciderCommand1.decide(Mockito.anyList())).thenReturn(
			Arrays.asList(new EnrichedFortscaleEventBuilder().setAnomalyTypeFieldName("smart").buildObject())
		);

		List<EnrichedFortscaleEvent> originalEvidencesList =  Arrays.asList(new EnrichedFortscaleEventBuilder().buildObject(),
				new EnrichedFortscaleEventBuilder().buildObject());

		AlertDeciderServiceImpl d = Mockito.spy(new AlertDeciderServiceImpl());
		d.setConf(conf);
		d.setNameDecidersList(Arrays.asList(deciderCommand1, deciderCommand2));
		String name = d.decideName(originalEvidencesList);

		Assert.assertEquals("smart",name);
		//Verify that first decider called 1 time, and the second one didn't returned at all
		Mockito.verify(deciderCommand1, Mockito.times(1)).decide(originalEvidencesList);
		Mockito.verify(deciderCommand2, Mockito.never()).decide(Mockito.anyList());
	}


	/**
	 * In this test the first decider and second decider return 2 results,
	 * and the third decider only one decider. All deciders should be called
	 */
	@Test
	public void nameDecider2Test(){

		DeciderCommand deciderCommand1 = Mockito.mock(DeciderCommand.class);
		DeciderCommand deciderCommand2 = Mockito.mock(DeciderCommand.class);
		DeciderCommand deciderCommand3 = Mockito.mock(DeciderCommand.class);

		EnrichedFortscaleEvent evidence1 = new EnrichedFortscaleEventBuilder().setAnomalyTypeFieldName("smart").buildObject();
		EnrichedFortscaleEvent evidence2 = new EnrichedFortscaleEventBuilder().setAnomalyTypeFieldName("BruteForce").buildObject();
		EnrichedFortscaleEvent evidence3 =new EnrichedFortscaleEventBuilder().setAnomalyTypeFieldName("GeoHopping").buildObject();

		List<EnrichedFortscaleEvent> originalEvidencesList = Arrays.asList(evidence1, evidence2, evidence3	);


		Mockito.when(deciderCommand1.decide(Mockito.anyList())).thenReturn(originalEvidencesList);

		Mockito.when(deciderCommand2.decide(Mockito.anyList())).thenReturn(
				Arrays.asList(evidence1, evidence2	)
		);

		//deciderCommand3 return array of one
		Mockito.when(deciderCommand3.decide(Mockito.anyList())).thenReturn(
				Arrays.asList(evidence2)
		);


		AlertDeciderServiceImpl d = Mockito.spy(new AlertDeciderServiceImpl());
		d.setConf(conf);
		d.setNameDecidersList(Arrays.asList(deciderCommand1, deciderCommand2,deciderCommand3));
		String name = d.decideName(originalEvidencesList);

		Assert.assertEquals("BruteForce",name);
		//Verify that first decider called 1 time, and the second one didn't returned at all
		Mockito.verify(deciderCommand1, Mockito.times(1)).decide(originalEvidencesList);
		Mockito.verify(deciderCommand2, Mockito.times(1)).decide(originalEvidencesList);
		Mockito.verify(deciderCommand3, Mockito.times(1)).decide(Mockito.anyList());
	}

	/**
	 * In this test the first decider and second decider return 2 results,
	 * and the third decider only one decider. All deciders should be called
	 */
	@Test
	public void scoreDeciderTest(){

		DeciderCommand deciderCommand1 = Mockito.mock(DeciderCommand.class);
		DeciderCommand deciderCommand2 = Mockito.mock(DeciderCommand.class);
		DeciderCommand deciderCommand3 = Mockito.mock(DeciderCommand.class);

		//deciderCommand1 return array of TWO
		Mockito.when(deciderCommand1.decide(Mockito.anyList())).thenReturn(
				Arrays.asList(
						new EnrichedFortscaleEventBuilder().setAnomalyTypeFieldName("smart").setScore(50).buildObject(),
						new EnrichedFortscaleEventBuilder().setAnomalyTypeFieldName("BruteForce").setScore(60).buildObject(),
						new EnrichedFortscaleEventBuilder().setAnomalyTypeFieldName("GeoHopping").setScore(70).buildObject()
				)
		);

		//deciderCommand2 return array of TWO
		Mockito.when(deciderCommand2.decide(Mockito.anyList())).thenReturn(
				Arrays.asList(
						new EnrichedFortscaleEventBuilder().setAnomalyTypeFieldName("smart").setScore(50).buildObject(),
						new EnrichedFortscaleEventBuilder().setAnomalyTypeFieldName("BruteForce").setScore(60).buildObject()
				)
		);

		//deciderCommand3 return array of one
		Mockito.when(deciderCommand3.decide(Mockito.anyList())).thenReturn(
				Arrays.asList(
						new EnrichedFortscaleEventBuilder().setAnomalyTypeFieldName("BruteForce").setScore(60).buildObject()
				)
		);

		List<EnrichedFortscaleEvent> originalEvidencesList =  Arrays.asList(
				new EnrichedFortscaleEventBuilder().setAnomalyTypeFieldName("smart").setScore(50).buildObject(),
				new EnrichedFortscaleEventBuilder().setAnomalyTypeFieldName("BruteForce").setScore(60).buildObject(),
				new EnrichedFortscaleEventBuilder().setAnomalyTypeFieldName("GeoHopping").setScore(70).buildObject()
		);

		AlertDeciderServiceImpl d = Mockito.spy(new AlertDeciderServiceImpl());
		d.setConf(conf);
		d.setScoreDecidersList(Arrays.asList(deciderCommand1, deciderCommand2, deciderCommand3));
		int score = d.decideScore(originalEvidencesList);

		Assert.assertEquals(60,score);
		//Verify that first decider called 1 time, and the second one didn't returned at all
		Mockito.verify(deciderCommand1, Mockito.times(1)).decide(originalEvidencesList);
		Mockito.verify(deciderCommand2, Mockito.times(1)).decide(originalEvidencesList);
		Mockito.verify(deciderCommand3, Mockito.times(1)).decide(Mockito.anyList());
	}


}