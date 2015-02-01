package fortscale.streaming.scorer;

import org.apache.samza.config.ConfigException;
import org.junit.Test;

public class MaxScorerContainerTest extends ScorerContainerTest{
	

	@Test
	public void testBuildScorerWithScore1() throws Exception{
		testBuildScorerWithScore1(MaxScorerContainerFactory.SCORER_TYPE, 100.0d);
	}
	
	@Test
	public void testBuildScorerWithScore2() throws Exception{
		testBuildScorerWithScore2(MaxScorerContainerFactory.SCORER_TYPE, 90.0d);
	}
	
	@Test
	public void testBuildScorerWithScore3() throws Exception{
		testBuildScorerWithScore3(MaxScorerContainerFactory.SCORER_TYPE, 0.0d);
	}
	
	@Test
	public void testBuildScorerWithScore4() throws Exception{
		testBuildScorerWithScore4(MaxScorerContainerFactory.SCORER_TYPE, 100.0d);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNoScorerName() throws Exception{
		testBuildScorerWithNoScorerName(MaxScorerContainerFactory.SCORER_TYPE);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNonOutputFieldName() throws Exception{
		testBuildScorerWithNonOutputFieldName(MaxScorerContainerFactory.SCORER_TYPE);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankOutputFieldName() throws Exception{
		testBuildScorerWithBlankOutputFieldName(MaxScorerContainerFactory.SCORER_TYPE);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithNonScorers() throws Exception{
		testBuildScorerWithNonScorers(MaxScorerContainerFactory.SCORER_TYPE);
	}
	
	@Test(expected=ConfigException.class)
	public void testBuildScorerWithBlankScorers() throws Exception{
		testBuildScorerWithBlankScorers(MaxScorerContainerFactory.SCORER_TYPE);
	}
}
