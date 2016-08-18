package fortscale.collection.morphlines.commands;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import fortscale.collection.monitoring.ItemContext;
import fortscale.collection.monitoring.MorphlineCommandMonitoringHelper;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;

import fortscale.collection.morphlines.RecordSinkCommand;

public class ParseFieldBuilderTest  {
  
	private static ClassPathXmlApplicationContext testContextManager;

    private RecordSinkCommand sink = new RecordSinkCommand();
    private Config config;


    private ConfigObject configObject;
    
    
    @BeforeClass
	public static void setUpClass(){
		testContextManager = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
	}
    
    @AfterClass
	public static void finalizeTestClass(){
		testContextManager.close();
		testContextManager = null;
	}

    @Before
    public void setUp() throws Exception {

        // mock morphline command parameters configuration
        config = mock(Config.class);
        configObject = mock(ConfigObject.class);




        Set<String> keySet = new HashSet<String>();
        keySet.add("leftSignCharacter");
        keySet.add("rightSignCharacter");
        keySet.add("fieldName");
        keySet.add("outputField");
        keySet.add("ignoreConfig");
        keySet.add("toParse");

        when(config.root()).thenReturn(configObject);
        when(configObject.keySet()).thenReturn(keySet);

    }
    
    private Record getRecord(String fieldName,String fieldValue) {
        Record record = new Record();
        record.put(fieldName,fieldValue);
        record.put(MorphlineCommandMonitoringHelper.ITEM_CONTEXT,
                new ItemContext("", null, new MorphlineMetrics(null, "dataSource")));
        return record;
    }

    private ParseFieldBuilder.ParseField getCommand() {

        ParseFieldBuilder builder = new ParseFieldBuilder();
        MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
        ParseFieldBuilder.ParseField ParseField =  new  ParseFieldBuilder.ParseField(builder,config,sink,sink,morphlineContext);
        return ParseField;

    }


    @Test
    public void test_parsingUserName() throws Exception {


            Record record = getRecord("username", "kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone");

            when(config.getString("leftSignCharacter")).thenReturn(null);
            when(config.getString("rightSignCharacter")).thenReturn("-");
            when(config.getString("fieldName")).thenReturn("username");
            when(config.getString("outputField")).thenReturn(null);
            when(config.getBoolean("ignoreConfig")).thenReturn(false);
            when(config.getString("toParse")).thenReturn("${fortscale.Morphline.Command.toParse}");


            ParseFieldBuilder.ParseField command = getCommand();
            boolean result = command.doProcess(record);
            Record output = sink.popRecord();

            assertTrue(result);
            assertNotNull(output);
            assertEquals("kebarrow", output.getFirstValue("username"));



    }




}
