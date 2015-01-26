package fortscale.streaming.scorer;

import static org.mockito.Mockito.when;

import org.apache.samza.config.Config;
import org.junit.Test;



public class ScorerContextTest extends ScorerBaseTest{	
	
	@Test(expected=RuntimeException.class)
	public void testCyclicDefinitionCauseException(){
		String scorerName = "testScorer";
		when(config.get(String.format("fortscale.score.%s.scorer", scorerName))).thenReturn(CyclicDefinitionScorerFactory.SCORER_NAME);
		ScorerContext context = new ScorerContext(config);
		ScorerFactory scorerFactory = new CyclicDefinitionScorerFactory();
		scorerFactory.getScorer(scorerName, config, context);
	}
	
	public class CyclicDefinitionScorerFactory implements ScorerFactory{
		public static final String SCORER_NAME="testFactory";
		
		public CyclicDefinitionScorerFactory(){
			scorerFactoryService.register(SCORER_NAME, this);
		}

		@Override
		public Scorer getScorer(String name, Config config,	ScorerContext context) {
			return (Scorer) context.resolve(Scorer.class, name);
		}
	}
	
	
}
