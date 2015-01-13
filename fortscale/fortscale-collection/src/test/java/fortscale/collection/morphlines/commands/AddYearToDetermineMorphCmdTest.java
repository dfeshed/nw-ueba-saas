package fortscale.collection.morphlines.commands;

import com.typesafe.config.*;
import com.typesafe.config.impl.*;
import fortscale.collection.morphlines.RecordSinkCommand;
import fortscale.collection.tagging.service.impl.UserTaggingServiceImpl;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Before;
import org.junit.Test;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddYearToDetermineMorphCmdTest {

	private RecordSinkCommand sink = new RecordSinkCommand();
	private Config config;
	private UserTaggingServiceImpl service;

	@Before
	public void setUp() throws Exception {	
		// mock morphline command parameters configuration
		config = mock(Config.class);
		when(config.root()).thenAnswer(getRoot());

	}

	private static Answer<ConfigObject> getRoot() {
		return new Answer<ConfigObject>() {
			public ConfigObject answer(InvocationOnMock invocation) {
				Map map = new HashedMap();
				return new ConfigObject(){

					@Override
					public Config toConfig() {
						return null;
					}

					@Override
					public ConfigOrigin origin() {
						return null;
					}

					@Override
					public ConfigValueType valueType() {
						return null;
					}

					@Override
					public Map<String, Object> unwrapped() {
						return null;
					}

					@Override
					public String render() {
						return null;
					}

					@Override
					public String render(ConfigRenderOptions configRenderOptions) {
						return null;
					}

					@Override
					public ConfigObject withFallback(ConfigMergeable configMergeable) {
						return null;
					}

					@Override
					public Config atPath(String s) {
						return null;
					}

					@Override
					public Config atKey(String s) {
						return null;
					}

					@Override
					public int size() {
						return 0;
					}

					@Override
					public boolean isEmpty() {
						return false;
					}

					@Override
					public boolean containsKey(Object key) {
						return false;
					}

					@Override
					public boolean containsValue(Object value) {
						return false;
					}

					@Override
					public ConfigValue get(Object o) {
						return null;
					}

					@Override
					public ConfigValue put(String key, ConfigValue value) {
						return null;
					}

					@Override
					public ConfigValue remove(Object key) {
						return null;
					}

					@Override
					public void putAll(Map<? extends String, ? extends ConfigValue> m) {

					}

					@Override
					public void clear() {

					}

					@Override
					public Set<String> keySet() {
						Set<String> set = new HashSet<String>();
						set.add("timezone");
						return set;
					}

					@Override
					public Collection<ConfigValue> values() {
						return null;
					}

					@Override
					public Set<Entry<String, ConfigValue>> entrySet() {
						return null;
					}

					@Override
					public ConfigObject withOnlyKey(String s) {
						return null;
					}

					@Override
					public ConfigObject withoutKey(String s) {
						return null;
					}

					@Override
					public ConfigObject withValue(String s, ConfigValue configValue) {
						return null;
					}
				};
			}
		};
	}
	
	private Record getRecord(String dateTime, String timezone) {
		Record record = new Record();
		record.put("date_time", dateTime);
		record.put("timezone", timezone);
		return record;
	}

	private AddYearToDatetimeMorphCmdBuilder.AddYearToDatetime getCommand() {
		AddYearToDatetimeMorphCmdBuilder builder = new AddYearToDatetimeMorphCmdBuilder();
		MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
		return new AddYearToDatetimeMorphCmdBuilder.AddYearToDatetime(builder, config, sink, sink, morphlineContext);
	}

	private String getYear(String date, String dateFormat, String timezone)  {
		try {
			TimeZone tz = TimeZone.getTimeZone(timezone != null ? timezone : "UTC");
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeZone(tz);
			Integer currentYear = calendar.get(Calendar.YEAR);
			String year = Integer.toString(currentYear);

			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			sdf.setTimeZone(tz);
			Date parsedDate = null;

			parsedDate = sdf.parse(year + " " + date);

			Date currentDate = calendar.getTime();
			if (parsedDate.compareTo(currentDate)>0) {
				currentYear -= 1;
			}
			return Integer.toString(currentYear);
		} catch (ParseException e) {
			return null;
		}
	}

	@Test
	public void add_year_april() throws Exception {

		String originalDate = "Apr 14 01:50:26";
		String dateFormat = "yyyy MMM d HH:mm:ss";
		String timezone = "Asia/Jerusalem";
		when(config.getString("dateFormat")).thenReturn(dateFormat);
		when(config.getString("timezone")).thenReturn(timezone);

		AddYearToDatetimeMorphCmdBuilder.AddYearToDatetime command = getCommand();
		Record record = getRecord(originalDate, timezone);
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		String expectedValue = getYear(originalDate, dateFormat, timezone) + " " + originalDate;
		
		assertTrue(result);
		assertNotNull(output);
		assertEquals(expectedValue, output.getFirstValue("date_time"));
	}

	@Test
	public void add_year_january() throws Exception {

		String originalDate = "Jan 14 01:50:26";
		String dateFormat = "yyyy MMM d HH:mm:ss";
		String timezone = "Asia/Jerusalem";
		when(config.getString("dateFormat")).thenReturn(dateFormat);
		when(config.getString("timezone")).thenReturn(timezone);

		AddYearToDatetimeMorphCmdBuilder.AddYearToDatetime command = getCommand();
		Record record = getRecord(originalDate, timezone);

		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		String expectedValue = getYear(originalDate, dateFormat, timezone) + " " + originalDate;

		assertTrue(result);
		assertNotNull(output);
		assertEquals(expectedValue, output.getFirstValue("date_time"));
	}

	@Test
	public void add_year_turn_of_the_year() throws Exception {

		String originalDate = "Jan 1 00:10:21";
		String dateFormat = "yyyy MMM d HH:mm:ss";
		String timezone = "Asia/Jerusalem";
		when(config.getString("dateFormat")).thenReturn(dateFormat);
		when(config.getString("timezone")).thenReturn(timezone);

		AddYearToDatetimeMorphCmdBuilder.AddYearToDatetime command = getCommand();
		Record record = getRecord(originalDate, timezone);

		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		String expectedValue = getYear(originalDate, dateFormat, timezone) + " " + originalDate;

		assertTrue(result);
		assertNotNull(output);
		assertEquals(expectedValue, output.getFirstValue("date_time"));
	}

	@Test
	public void add_year_UTC() throws Exception {

		String originalDate = "Jan 14 01:50:26";
		String dateFormat = "yyyy MMM d HH:mm:ss";
		String timezone = "UTC";
		when(config.getString("dateFormat")).thenReturn(dateFormat);
		when(config.getString("timezone")).thenReturn(timezone);

		AddYearToDatetimeMorphCmdBuilder.AddYearToDatetime command = getCommand();
		Record record = getRecord(originalDate, timezone);

		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		String expectedValue = getYear(originalDate, dateFormat, timezone) + " " + originalDate;

		assertTrue(result);
		assertNotNull(output);
		assertEquals(expectedValue, output.getFirstValue("date_time"));
	}

	@Test
	public void test_null_time() throws Exception {

		String originalDate = null;
		String dateFormat = "yyyy MMM d HH:mm:ss";
		String timezone = "UTC";
		when(config.getString("dateFormat")).thenReturn(dateFormat);
		when(config.getString("timezone")).thenReturn(timezone);

		AddYearToDatetimeMorphCmdBuilder.AddYearToDatetime command = getCommand();
		Record record = getRecord(originalDate, timezone);

		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();

		assertFalse("Date should not be null", result);
	}

	@Test
	public void test_empty_time() throws Exception {

		String originalDate = "";
		String dateFormat = "yyyy MMM d HH:mm:ss";
		String timezone = "UTC";
		when(config.getString("dateFormat")).thenReturn(dateFormat);
		when(config.getString("timezone")).thenReturn(timezone);

		AddYearToDatetimeMorphCmdBuilder.AddYearToDatetime command = getCommand();
		Record record = getRecord(originalDate, timezone);

		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		String expectedValue = getYear(originalDate, dateFormat, timezone) + " " + originalDate;

		assertFalse("Date should not be empty", result);
	}

	@Test
	public void test_empty_timezone() throws Exception {

		String originalDate = "Jan 14 01:50:26";
		String dateFormat = "yyyy MMM d HH:mm:ss";
		String timezone = null;
		when(config.getString("dateFormat")).thenReturn(dateFormat);
		when(config.getString("timezone")).thenReturn(timezone);

		AddYearToDatetimeMorphCmdBuilder.AddYearToDatetime command = getCommand();
		Record record = getRecord(originalDate, timezone);

		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		String expectedValue = getYear(originalDate, dateFormat, timezone) + " " + originalDate;

		assertTrue(result);
		assertNotNull(output);
		assertEquals(expectedValue, output.getFirstValue("date_time"));
	}


}
