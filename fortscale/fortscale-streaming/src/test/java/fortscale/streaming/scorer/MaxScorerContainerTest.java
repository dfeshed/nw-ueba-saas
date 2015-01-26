package fortscale.streaming.scorer;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/scorers-context-test.xml" })
public class MaxScorerContainerTest {

//	private static final String FIELD_NAME = "testFieldName";
//	private static final String OUTPUT_FIELD_NAME = "outputTestField";
//	private static final String SCORER_NAME = "ConstantRegexScorerTestScorerName";
//	
//	private Config config;
//	
//	@Autowired
//	private ScorerFactoryService scorerFactoryService;
//	
//	@Before
//    public void setUp() {
//        config = mock(Config.class);
//    }
//
//	@Test
//	public void thisAlwaysPasses() {
//	}
//
//	@Test
//	@Ignore
//	public void thisIsIgnored() {
//	}
//	
//	private Scorer buildConstantScorer(String scorerName, String outputFieldName, String fieldName, String regex, Integer constant){
//		when(config.get(String.format("fortscale.score.%s.output.field.name", scorerName))).thenReturn(outputFieldName);
//		when(config.get(String.format("fortscale.score.%s.regex.fieldname", scorerName))).thenReturn(fieldName);
//		when(config.get(String.format("fortscale.score.%s.regex", scorerName))).thenReturn(regex);
//		when(config.getInt(String.format("fortscale.score.%s.constant", scorerName))).thenReturn(constant);
//
//		return scorerFactoryService.getScorer(ContstantRegexScorerFactory.SCORER_NAME,scorerName, config, null);
//	}
//	
//	private Scorer buildScorer(String scorerName, String outputFieldName, String scores){
//		if(scorerName !=null){
//			if(outputFieldName != null)
//				when(config.get(String.format("fortscale.score.%s.output.field.name", scorerName))).thenReturn(outputFieldName);
//			if(fieldName != null)
//				when(config.get(String.format("fortscale.score.%s.regex.fieldname", scorerName))).thenReturn(fieldName);
//			if(regex != null)
//				when(config.get(String.format("fortscale.score.%s.regex", scorerName))).thenReturn(regex);
//			if(constant != null)
//				when(config.getInt(String.format("fortscale.score.%s.constant", scorerName))).thenReturn(constant);
//			else{
//				String k = String.format("fortscale.score.%s.constant", scorerName);
//				when(config.getInt(k)).thenThrow(new ConfigException("Missing key " + k + "."));
//			}
//		}
//		return scorerFactoryService.getScorer(ContstantRegexScorerFactory.SCORER_NAME,scorerName, config, null);
//	}
//	
//	private EventMessage buildEventMessage(boolean isAddInput, String fieldName, String fieldValue){
//		JSONObject jsonObject = null;
//		if(isAddInput){
//			jsonObject = new JSONObject();
//			jsonObject.put(fieldName, fieldValue);
//		}
//		return new EventMessage(jsonObject);
//	}
//	
//	@Test(expected=ConfigException.class)
//	public void testBuildScorerWithNoScorerName() throws Exception{
//		@SuppressWarnings("unused")
//		Scorer scorer = buildScorer(null, OUTPUT_FIELD_NAME, FIELD_NAME, "test.*", 100);
//	}
//	
//	@Test(expected=ConfigException.class)
//	public void testBuildScorerWithNonOutputFieldName() throws Exception{
//		@SuppressWarnings("unused")
//		Scorer scorer = buildScorer(SCORER_NAME, null, FIELD_NAME, "test.*", 100);
//	}
//	
//	@Test(expected=ConfigException.class)
//	public void testBuildScorerWithBlankOutputFieldName() throws Exception{
//		@SuppressWarnings("unused")
//		Scorer scorer = buildScorer(SCORER_NAME, "  ", FIELD_NAME, "test.*", 100);
//	}
//	
//	@Test(expected=ConfigException.class)
//	public void testBuildScorerWithNonFieldName() throws Exception{
//		@SuppressWarnings("unused")
//		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, null, "test.*", 100);
//	}
//	
//	@Test(expected=ConfigException.class)
//	public void testBuildScorerWithBlankFieldName() throws Exception{
//		@SuppressWarnings("unused")
//		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, "   ", "test.*", 100);
//	}
//	
//	@Test(expected=ConfigException.class)
//	public void testBuildScorerWithNonRegex() throws Exception{
//		@SuppressWarnings("unused")
//		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, FIELD_NAME, null, 100);
//	}
//	
//	@Test(expected=ConfigException.class)
//	public void testBuildScorerWithBlankRegex() throws Exception{
//		@SuppressWarnings("unused")
//		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, FIELD_NAME, "", 100);
//	}
//	
//	@Test(expected=ConfigException.class)
//	public void testBuildScorerWithNonConstant() throws Exception{
//		@SuppressWarnings("unused")
//		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, FIELD_NAME, "test.*", null);
//	}
//	
//	@Test
//	public void testBuildScorer() throws Exception{
//		Scorer scorer = buildScorer(SCORER_NAME, OUTPUT_FIELD_NAME, FIELD_NAME, "test.*", 100);
//		Assert.assertNotNull(scorer);
//	}
}
