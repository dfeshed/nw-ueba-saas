package fortscale.collection.morphlines.commands;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

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

public class FieldBlacklistFilterTest {
	private static ClassPathXmlApplicationContext testContextManager;

    private RecordSinkCommand sink = new RecordSinkCommand();
    private Config config;
    private ConfigObject configObject;
    
    FieldBlacklistFilterCmdBuilder.FieldBlacklistFilter command;
    
    
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
        keySet.add("fieldName");
        keySet.add("blacklistFile");

        when(config.root()).thenReturn(configObject);
        when(configObject.keySet()).thenReturn(keySet);

        when(config.getString("blacklistFile")).thenReturn("classpath:blacklist.txt");
        when(config.getString("fieldName")).thenReturn("testedField");
        
        command = getCommand();
    }
    
    private FieldBlacklistFilterCmdBuilder.FieldBlacklistFilter getCommand() {

    	FieldBlacklistFilterCmdBuilder builder = new FieldBlacklistFilterCmdBuilder();
        MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
        FieldBlacklistFilterCmdBuilder.FieldBlacklistFilter fieldBlacklistFilter =  new  FieldBlacklistFilterCmdBuilder.FieldBlacklistFilter(builder,config,sink,sink,morphlineContext);
        return fieldBlacklistFilter;

    }


    @Test
    public void test_valueExistInTheListIsFiltered() throws Exception {     
        Record record = new Record();
        record.put("testedField","TEST-PC");
        boolean result = command.doProcess(record);
        Record output = sink.popRecord();
        
        assertTrue(result);
        assertNull(output);
    }
    
    @Test
    public void test_valueNotExistInTheListIsNotFiltered() throws Exception {     
        Record record = new Record();
        record.put("testedField","TEST-PC1");
        boolean result = command.doProcess(record);
        Record output = sink.popRecord();
        
        assertTrue(result);
        assertNotNull(output);



    }




}

