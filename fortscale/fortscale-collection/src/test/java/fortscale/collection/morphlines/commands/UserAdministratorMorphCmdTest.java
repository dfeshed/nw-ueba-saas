package fortscale.collection.morphlines.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;

import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordSinkCommand;
import fortscale.collection.morphlines.commands.UserAdministratorMorphCmdBuilder.IsUserAdministrator;
import fortscale.collection.tagging.service.UserTagEnum;
import fortscale.collection.tagging.service.impl.UserTaggingServiceImpl;

public class UserAdministratorMorphCmdTest {

	private RecordSinkCommand sink = new RecordSinkCommand();
	private Config config;
	private UserTaggingServiceImpl service;

	@Before
	public void setUp() throws Exception {	
		// mock morphline command parameters configuration
		config = mock(Config.class);
		when(config.getString("usernameField")).thenReturn("normalized_username");
		when(config.getString("isUserAdministratorField")).thenReturn("isUserAdministrator");
		
		// mock service
		service = mock(UserTaggingServiceImpl.class);
	}
	
	private Record getRecord(boolean skipUsername, String username) {
		Record record = new Record();
		if (!skipUsername)
			record.put("normalized_username", username);
		return record;
	}
	
	private IsUserAdministrator getCommand() {
		UserServiceMorphCmdBuilder builder = new UserServiceMorphCmdBuilder();
		MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
		return new IsUserAdministrator(builder, config, sink, sink, morphlineContext, service);
	}

	@Test
	public void serivce_returns_admin_account() throws Exception {
		when(service.isUserTagged(UserTagEnum.admin.getId(),"test-user")).thenReturn(true);
		
		IsUserAdministrator command = getCommand();
		Record record = getRecord(false, "test-user");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertEquals("test-user", output.getFirstValue("normalized_username"));
		assertEquals(true, output.getFirstValue("isUserAdministrator"));		
	}

	@Test
	public void serivce_when_username_is_empty() throws Exception {	
		IsUserAdministrator command = getCommand();
		Record record = getRecord(false, "");
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertEquals(false, output.getFirstValue("isUserAdministrator"));
		verify(service, times(0)).isUserTagged(anyString(),anyString());
	}
	
	@Test
	public void serivce_when_username_is_null() throws Exception {	
		IsUserAdministrator command = getCommand();
		Record record = getRecord(true, null);
		
		// execute the command
		boolean result = command.doProcess(record);
		Record output = sink.popRecord();
		
		assertTrue(result);
		assertNotNull(output);
		assertEquals(false, output.getFirstValue("isUserAdministrator"));
		verify(service, times(0)).isUserTagged(anyString(), anyString());
	}	
}
