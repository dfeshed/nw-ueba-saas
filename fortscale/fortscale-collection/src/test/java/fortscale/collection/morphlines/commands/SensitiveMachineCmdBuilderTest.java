package fortscale.collection.morphlines.commands;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.mockito.MockitoAnnotations;

import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordSinkCommand;
import fortscale.collection.morphlines.commands.SensitiveMachineMorphCmdBuilder.IsSensitiveMachine;
import fortscale.services.SensitiveMachineService;

public class SensitiveMachineCmdBuilderTest {
	
	private Config config;
	private SensitiveMachineService service;

	private RecordSinkCommand sink = new RecordSinkCommand();
	
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		config = mock(Config.class);
		service = mock(SensitiveMachineService.class);

	}
	
	private IsSensitiveMachine getCommand() {
		// build IsSensitiveMachine command
		SensitiveMachineMorphCmdBuilder builder = new SensitiveMachineMorphCmdBuilder();
		MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
		return  new IsSensitiveMachine(builder, config, sink, sink, morphlineContext, service);
	}

	@Test
	public void test_updating_record_of_sensitive_machine() {
		when(config.getString("machineNameField")).thenReturn("machineName");
		when(config.getString("isSensitiveMachineField")).thenReturn("isSensitive");
		IsSensitiveMachine cmd = getCommand();
		Record record = new Record();
		record.put("machineName", "MY-PC");
		when(service.isMachineSensitive("MY-PC")).thenReturn(true);
		cmd.doProcess(record);
		Record output = sink.popRecord();
		assertEquals(output.getFirstValue("isSensitive"), true);
		
	}
	
	@Test
	public void test_updating_record_of_unsensitive_machine() {
		when(config.getString("machineNameField")).thenReturn("machineName");
		when(config.getString("isSensitiveMachineField")).thenReturn("isSensitive");
		IsSensitiveMachine cmd = getCommand();
		Record record = new Record();
		record.put("machineName", "MY-PC");
		when(service.isMachineSensitive("MY-PC")).thenReturn(false);
		cmd.doProcess(record);
		Record output = sink.popRecord();
		assertEquals(output.getFirstValue("isSensitive"), false);
	}
}
