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
import fortscale.collection.morphlines.commands.FieldFilterCmdBuilder.FieldFilter;

public class FieldFilterTest {
	private static ClassPathXmlApplicationContext testContextManager;

    private RecordSinkCommand sink = new RecordSinkCommand();
    private Config config;
    private ConfigObject configObject;
    
    FieldFilterCmdBuilder.FieldFilter command;
    
    
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
        keySet.add("listFile");

        when(config.root()).thenReturn(configObject);
        when(configObject.keySet()).thenReturn(keySet);        
    }
    
    private FieldFilterCmdBuilder.FieldFilter getCommand() {

    	FieldFilterCmdBuilder builder = new FieldFilterCmdBuilder();
        MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
        FieldFilterCmdBuilder.FieldFilter fieldFilter =  (FieldFilter) builder.build(config,sink,sink,morphlineContext);
        return fieldFilter;

    }
    
    

    @Test
    public void test_valueExistInTheWhiteListIsNotFiltered() throws Exception {    
    	when(config.getString("listFile")).thenReturn("classpath:exactMatchWhitelist.txt");
        when(config.getString("fieldName")).thenReturn("testedField");
        when(config.getBoolean("isBlacklist")).thenReturn(false);
        when(config.hasPath("isBlacklist")).thenReturn(true);
        when(config.getBoolean("isRegex")).thenReturn(false);
        when(config.hasPath("isRegex")).thenReturn(true);
        command = getCommand();
        
        Record record = new Record();
        record.put("testedField","TEST-PC");
        boolean result = command.doProcess(record);
        Record output = sink.popRecord();
        
        assertTrue(result);
        assertNotNull(output);
    }
    
    @Test
    public void test_valueNotExistInTheWhiteListIsFiltered() throws Exception {    
    	when(config.getString("listFile")).thenReturn("classpath:exactMatchWhitelist.txt");
        when(config.getString("fieldName")).thenReturn("testedField");
        when(config.getBoolean("isBlacklist")).thenReturn(false);
        when(config.hasPath("isBlacklist")).thenReturn(true);
        when(config.getBoolean("isRegex")).thenReturn(false);
        when(config.hasPath("isRegex")).thenReturn(true);
        command = getCommand();
        
        Record record = new Record();
        record.put("testedField","TEST-PC1");
        boolean result = command.doProcess(record);
        Record output = sink.popRecord();
        
        assertTrue(result);
        assertNull(output);
    }

    @Test
    public void test_valueExistInTheListIsFiltered1() throws Exception {    
    	when(config.getString("listFile")).thenReturn("classpath:blacklist.txt");
        when(config.getString("fieldName")).thenReturn("testedField");
        command = getCommand();
        
        Record record = new Record();
        record.put("testedField","TEST-PC");
        boolean result = command.doProcess(record);
        Record output = sink.popRecord();
        
        assertTrue(result);
        assertNull(output);
    }
    
    @Test
    public void test_valueExistInTheListIsFiltered2() throws Exception {    
    	when(config.getString("listFile")).thenReturn("file:src/test/resources/blacklist.txt");
        when(config.getString("fieldName")).thenReturn("${field.blacklist.filter.name}");
        command = getCommand();
        
        Record record = new Record();
        record.put("testedField","TEST-PC");
        boolean result = command.doProcess(record);
        Record output = sink.popRecord();
        
        assertTrue(result);
        assertNull(output);
    }
    
    @Test
    public void test_valueNotExistInTheListIsNotFiltered() throws Exception { 
    	when(config.getString("listFile")).thenReturn("classpath:blacklist.txt");
        when(config.getString("fieldName")).thenReturn("testedField");
        command = getCommand();
        
        Record record = new Record();
        record.put("testedField","TEST-PC1");
        boolean result = command.doProcess(record);
        Record output = sink.popRecord();
        
        assertTrue(result);
        assertNotNull(output);
    }


}

