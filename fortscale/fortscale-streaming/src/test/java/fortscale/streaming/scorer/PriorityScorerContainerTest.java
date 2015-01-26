package fortscale.streaming.scorer;

import org.apache.samza.config.ConfigException;
import org.junit.Test;

public class PriorityScorerContainerTest extends ScorerContainerTest{	
	@Test
	public void testBuildScorerWithScore1() throws Exception{
		testBuildScorerWithScore1(PriorityScorerContainerFactory.SCORER_TYPE, 100.0d);
	}
	
	@Test
	public void testBuildScorerWithScore2() throws Exception{
		testBuildScorerWithScore2(PriorityScorerContainerFactory.SCORER_TYPE, 90.0d);
	}
	
	@Test
	public void testBuildScorerWithScore3() throws Exception{
		testBuildScorerWithScore3(PriorityScorerContainerFactory.SCORER_TYPE, 0.0d);
	}
	
	@Test
	public void testBuildScorerWithScore4() throws Exception{
		testBuildScorerWithScore4(PriorityScorerContainerFactory.SCORER_TYPE, 90.0d);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoScorerName() throws Exception{
		testBuildScorerWithNoScorerName(PriorityScorerContainerFactory.SCORER_TYPE);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNonOutputFieldName() throws Exception{
		testBuildScorerWithNonOutputFieldName(PriorityScorerContainerFactory.SCORER_TYPE);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankOutputFieldName() throws Exception{
		testBuildScorerWithBlankOutputFieldName(PriorityScorerContainerFactory.SCORER_TYPE);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNonScorers() throws Exception{
		testBuildScorerWithNonScorers(PriorityScorerContainerFactory.SCORER_TYPE);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankScorers() throws Exception{
		testBuildScorerWithBlankScorers(PriorityScorerContainerFactory.SCORER_TYPE);
	}
}
