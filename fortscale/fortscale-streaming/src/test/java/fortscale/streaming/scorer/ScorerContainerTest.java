package fortscale.streaming.scorer;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import com.google.common.collect.Lists;

public class ScorerContainerTest extends ScorerBaseTest{

	protected static final String CONST_FIELD_NAME1 = "testFieldName1";
	protected static final String CONST_FIELD_NAME2 = "testFieldName2";
	protected static final String CONST_OUTPUT_FIELD_NAME = "outputTestField";
	protected static final String CONST_SCORER_NAME1 = "ConstantRegexScorerTestScorerName1";
	protected static final String CONST_SCORER_NAME2 = "ConstantRegexScorerTestScorerName2";
	protected static final String OUTPUT_FIELD_NAME = "outputTestField";
	protected static final String SCORER_NAME = "MaxScorerContainerTestScorerName";

	protected double delta = 0.0;
	
	private void configConstantScorer(String scorerName, String outputFieldName, String fieldName, String regex, Integer constant){
		when(config.get(String.format("fortscale.score.%s.scorer", scorerName))).thenReturn(ContstantRegexScorerFactory.SCORER_TYPE);
		when(config.get(String.format("fortscale.score.%s.output.field.name", scorerName))).thenReturn(outputFieldName);
		when(config.get(String.format("fortscale.score.%s.regex.fieldname", scorerName))).thenReturn(fieldName);
		when(config.get(String.format("fortscale.score.%s.regex", scorerName))).thenReturn(regex);
		when(config.getInt(String.format("fortscale.score.%s.constant", scorerName))).thenReturn(constant);
	}
	
	protected Scorer buildScorer(String scorerType, String scorerName, String outputFieldName, List<String> scorers){
		if(scorerName !=null){
			when(config.get(String.format("fortscale.score.%s.scorer", scorerName))).thenReturn(scorerType);
			if(scorers != null)
				when(config.getList(String.format("fortscale.score.%s.scorers", scorerName))).thenReturn(scorers);
			if(outputFieldName != null)
				when(config.get(String.format("fortscale.score.%s.output.field.name", scorerName))).thenReturn(outputFieldName);
		}
		return (Scorer) context.resolve(Scorer.class, scorerName);
	}
	
	protected List<String> configScorers(){
		List<String> scorers = new ArrayList<>();
		configConstantScorer(CONST_SCORER_NAME1, CONST_OUTPUT_FIELD_NAME, CONST_FIELD_NAME1, "test.*", 100);
		scorers.add(CONST_SCORER_NAME1);
		configConstantScorer(CONST_SCORER_NAME2, CONST_OUTPUT_FIELD_NAME, CONST_FIELD_NAME2, "unit.*", 90);
		scorers.add(CONST_SCORER_NAME2);
		return scorers; 
	}
	
	protected void testBuildScorerWithScore1(String scorerType, double expectedScore) throws Exception{
		List<String> scorers = configScorers(); 
		Scorer scorer = buildScorer(scorerType, SCORER_NAME, OUTPUT_FIELD_NAME, scorers);
		EventMessage eventMessage = buildEventMessage(true, CONST_FIELD_NAME1, "testA1B");
		addToEventMessage(eventMessage, CONST_FIELD_NAME2, "unit908o");
		testScore(scorer, expectedScore, eventMessage);
	}
	
	private void testScore(Scorer scorer, double expectedScore, EventMessage eventMessage) throws Exception{
		FeatureScore score = scorer.calculateScore(eventMessage);
		Assert.assertNotNull(score);
		Assert.assertEquals(expectedScore, score.getScore(), delta);
		Assert.assertEquals(OUTPUT_FIELD_NAME, score.getName());
	}
	
	protected void testBuildScorerWithScore2(String scorerType, double expectedScore) throws Exception{
		List<String> scorers = configScorers(); 
		Scorer scorer = buildScorer(scorerType, SCORER_NAME, OUTPUT_FIELD_NAME, scorers);
		EventMessage eventMessage = buildEventMessage(true, CONST_FIELD_NAME1, "tesA1B");
		addToEventMessage(eventMessage, CONST_FIELD_NAME2, "unit908o");
		testScore(scorer, expectedScore, eventMessage);
	}
	
	protected void testBuildScorerWithScore3(String scorerType, double expectedScore) throws Exception{
		List<String> scorers = configScorers(); 
		Scorer scorer = buildScorer(scorerType, SCORER_NAME, OUTPUT_FIELD_NAME, scorers);
		EventMessage eventMessage = buildEventMessage(true, CONST_FIELD_NAME1, "tesA1B");
		addToEventMessage(eventMessage, CONST_FIELD_NAME2, "unt908o");
		testScore(scorer, expectedScore, eventMessage);
	}
	
	protected void testBuildScorerWithScore4(String scorerType, double expectedScore) throws Exception{
		List<String> scorers = configScorers(); 
		Scorer scorer = buildScorer(scorerType, SCORER_NAME, OUTPUT_FIELD_NAME, Lists.reverse(scorers));
		EventMessage eventMessage = buildEventMessage(true, CONST_FIELD_NAME1, "testA1B");
		addToEventMessage(eventMessage, CONST_FIELD_NAME2, "unit908o");
		testScore(scorer, expectedScore, eventMessage);
	}
	
	protected void testBuildScorerWithNoScorerName(String scorerType) throws Exception{
		List<String> scorers = configScorers(); 
		buildScorer(scorerType, null, OUTPUT_FIELD_NAME, scorers);
	}
	
	protected void testBuildScorerWithNonOutputFieldName(String scorerType) throws Exception{
		List<String> scorers = configScorers(); 
		buildScorer(scorerType, SCORER_NAME, null, scorers);
	}
	
	protected void testBuildScorerWithBlankOutputFieldName(String scorerType) throws Exception{
		List<String> scorers = configScorers(); 
		buildScorer(scorerType, SCORER_NAME, "   ", scorers);
	}
	
	protected void testBuildScorerWithNonScorers(String scorerType) throws Exception{
		buildScorer(scorerType, SCORER_NAME, OUTPUT_FIELD_NAME, null);
	}
	
	protected void testBuildScorerWithBlankScorers(String scorerType) throws Exception{
		buildScorer(scorerType, SCORER_NAME, OUTPUT_FIELD_NAME, new ArrayList<String>());
	}
}
