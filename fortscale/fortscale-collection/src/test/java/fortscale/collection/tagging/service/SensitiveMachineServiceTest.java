package fortscale.collection.tagging.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fortscale.collection.tagging.service.impl.SensitiveMachineServiceImpl;
import fortscale.domain.core.dao.ComputerRepository;

public class SensitiveMachineServiceTest {

	@Mock
	private ComputerRepository computerRepository;

	@InjectMocks
	private SensitiveMachineServiceImpl service;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		List<String> computers = new ArrayList<String>();
		computers.add("MY-PC");
		computers.add("ANOTHER-PC");
		when(computerRepository.findNameByIsSensitive(true)).thenReturn(computers);
		Set<String> sensitiveMachines = new HashSet<String>();
		sensitiveMachines.add("MY-PC");
		sensitiveMachines.add("ANOTHER-PC");
		service.setSensitiveMachines(sensitiveMachines);
		service.setDeletionSymbol("-");

	}

	@Test
	public void isSensitive_should_return_true() {
		assertTrue(service.isMachineSensitive("my-pc") == true);
	}

	@Test
	public void isSensitive_should_return_false() {
		assertTrue(service.isMachineSensitive("YOUR-PC") == false);
	}

	@Test
	public void test_loadSensitiveMachinesFromMongo_output() {
		Set<String> out = new HashSet<String>();
		out.add("MY-PC");
		out.add("ANOTHER-PC");
		assertEquals(service.loadSensitiveMachinesFromMongo(), out);
	}

	@Test
	public void test_adding_existing_sensitiveMachine_to_sensitivemachines() throws IOException {
		creatingMachinesFile("DUMMY-PC");
		when(computerRepository.findIfComputerExists(anyString())).thenReturn(
				true);
		service.updateSensitiveMachines();
		assertTrue(service.getSensitiveMachines().contains("DUMMY-PC") == true);
	}
	
	@Test
	public void test_adding_three_sensitiveMachines_to_sensitivemachines() throws IOException {
		creatingMachinesFile("dummy-pc\nX-PC\nY-PC");
		when(computerRepository.findIfComputerExists(anyString())).thenReturn(
			true);
		service.updateSensitiveMachines();
		assertTrue(service.getSensitiveMachines().contains("DUMMY-PC") == true);
		assertTrue(service.getSensitiveMachines().contains("X-PC") == true);
		assertTrue(service.getSensitiveMachines().contains("Y-PC") == true);
	}
	
	@Test
	public void test_adding_not_existed_machine_to_sensitive_machines() throws IOException {
		creatingMachinesFile("DUMMY-PC\n ");
		when(computerRepository.findIfComputerExists(anyString())).thenReturn(
				false);
		service.updateSensitiveMachines();
		assertTrue(service.getSensitiveMachines().contains("DUMMY-PC") == false);
		
	}
	
	@Test
	public void test_removing_sensitive_machine_from_sensitive_machines() throws IOException {
		creatingMachinesFile("-MY-PC");
		when(computerRepository.findIfComputerExists(anyString())).thenReturn(
				true);
		service.updateSensitiveMachines();
		assertTrue(service.getSensitiveMachines().contains("MY-PC") == false);
	}
	
	@Test
	public void test_removing_not_sensitive_machine_from_sensitive_machines() throws IOException {
		creatingMachinesFile("-DUMMY-PC");
		when(computerRepository.findIfComputerExists(anyString())).thenReturn(
				true);
		Set<String> oldSensitiveMachine = new HashSet<String>(service.getSensitiveMachines());
		service.updateSensitiveMachines();
		assertEquals(service.getSensitiveMachines(), oldSensitiveMachine);
		
	}
	
	private void creatingMachinesFile(String input){
		try {
			File machinesFile = File.createTempFile("temp", ".txt");
			machinesFile.deleteOnExit();
			service.setFilePath(machinesFile.getAbsolutePath());
			BufferedWriter output = new BufferedWriter(new FileWriter(machinesFile.getAbsolutePath()));
			output.write(input);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
