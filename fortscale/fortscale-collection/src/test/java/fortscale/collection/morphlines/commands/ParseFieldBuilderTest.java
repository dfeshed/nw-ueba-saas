package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import fortscale.collection.morphlines.RecordSinkCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/collection-context-test-light.xml"})

public class ParseFieldBuilderTest  {

    private RecordSinkCommand sink = new RecordSinkCommand();
    private Config config;


    private ConfigObject configObject;

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
        keySet.add("configurationPath");

        when(config.root()).thenReturn(configObject);
        when(configObject.keySet()).thenReturn(keySet);

    }

    private Record getRecord(String fieldName,String fieldValue) {
        Record record = new Record();
        record.put(fieldName,fieldValue);
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
            when(config.getString("configurationPath")).thenReturn("fortscale.Morphline.Command.toParse");


            ParseFieldBuilder.ParseField command = getCommand();
            boolean result = command.doProcess(record);
            Record output = sink.popRecord();

            assertTrue(result);
            assertNotNull(output);
            assertEquals("kebarrow", output.getFirstValue("username"));



    }




}
