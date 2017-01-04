package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import fortscale.collection.morphlines.RecordSinkCommand;
import fortscale.collection.morphlines.commands.DcServiceFilterBuilder.DcServiceFilter;
import fortscale.services.ServersListConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link DcServiceFilter} unit tests.
 *
 * @author Lior Govrin
 */
@RunWith(MockitoJUnitRunner.class)
public class DcServiceFilterTest {
	private Config config;
	private ConfigObject configObject;
	private RecordSinkCommand recordSinkCommand = new RecordSinkCommand();
	private MorphlineContext morphlineContext = new MorphlineContext.Builder().build();

	@Mock
	private ServersListConfiguration serversListConfiguration;
	@InjectMocks
	private DcServiceFilterBuilder dcServiceFilterBuilder;

	@Before
	public void before() throws Exception {
		config = mock(Config.class);
		configObject = mock(ConfigObject.class);
	}

	private DcServiceFilter getCommand(String loginServiceRegex, String fieldName) throws Exception {
		when(serversListConfiguration.getLoginServiceRegex()).thenReturn(loginServiceRegex);
		when(config.getString("fieldName")).thenReturn(fieldName);
		when(config.root()).thenReturn(configObject);
		when(configObject.render()).thenReturn(fieldName);
		return (DcServiceFilter)dcServiceFilterBuilder.build(
				config, recordSinkCommand, recordSinkCommand, morphlineContext);
	}

	@Test
	public void should_not_drop_record_if_regex_is_null() throws Exception {
		DcServiceFilter command = getCommand(null, "service_name");
		Record record = new Record();
		record.put("service_name", "FS-DC-01");
		command.doProcess(record);
		Assert.assertEquals(record, recordSinkCommand.popRecord());
	}

	@Test
	public void should_not_drop_record_if_regex_is_empty() throws Exception {
		DcServiceFilter command = getCommand("", "machine_name");
		Record record = new Record();
		record.put("machine_name", "FS-DC-02");
		command.doProcess(record);
		Assert.assertEquals(record, recordSinkCommand.popRecord());
	}

	@Test
	public void should_not_drop_record_if_regex_is_blank() throws Exception {
		DcServiceFilter command = getCommand("   ", "hostname");
		Record record = new Record();
		record.put("hostname", "FS-DC-03");
		command.doProcess(record);
		Assert.assertEquals(record, recordSinkCommand.popRecord());
	}

	@Test
	public void should_not_drop_record_if_field_does_not_exist() throws Exception {
		DcServiceFilter command = getCommand(".*FS-DC-\\d{2}.*", "service_name");
		Record record = new Record();
		command.doProcess(record);
		Assert.assertEquals(record, recordSinkCommand.popRecord());
	}

	@Test
	public void should_not_drop_record_if_field_value_is_null() throws Exception {
		DcServiceFilter command = getCommand(".*FS-DC-\\d{2}.*", "machine_name");
		Record record = new Record();
		record.put("machine_name", null);
		command.doProcess(record);
		Assert.assertEquals(record, recordSinkCommand.popRecord());
	}

	@Test(expected = ClassCastException.class)
	public void should_fail_if_field_value_is_not_a_string() throws Exception {
		DcServiceFilter command = getCommand(".*FS-DC-\\d{2}.*", "not_a_string");
		Record record = new Record();
		record.put("not_a_string", 100);
		command.doProcess(record);
	}

	@Test
	public void should_not_drop_record_if_field_value_does_not_match_regex() throws Exception {
		DcServiceFilter command = getCommand(".*FS-DC-\\d{2}.*", "hostname");
		Record record = new Record();
		record.put("hostname", "LIOR-PC");
		command.doProcess(record);
		Assert.assertEquals(record, recordSinkCommand.popRecord());
	}

	@Test
	public void should_drop_record_if_field_value_matches_regex() throws Exception {
		DcServiceFilter command = getCommand(".*FS-DC-\\d{2}.*", "service_name");
		Record record = new Record();
		record.put("service_name", "FS-DC-04");
		command.doProcess(record);
		Assert.assertNull(recordSinkCommand.popRecord());
	}
}
