package fortscale.services.impl;

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

import fortscale.domain.core.Computer;
import fortscale.domain.core.dao.ComputerRepository;

public class SensitiveMachineServiceTest {

	@Mock
	private ComputerRepository computerRepository;

	@InjectMocks
	private SensitiveMachineServiceImpl service;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Set<String> sensitiveMachines = new HashSet<String>();
		sensitiveMachines.add("MY-PC");
		service.setSensitiveMachines(sensitiveMachines);
		Computer computer1 = mock(Computer.class);
		Computer computer2 = mock(Computer.class);
		List<Computer> computers = new ArrayList<Computer>();
		computers.add(computer1);
		computers.add(computer2);
		when(computerRepository.findByIsSensitive(true)).thenReturn(computers);
		when(computer1.getName()).thenReturn("comp1");
		when(computer2.getName()).thenReturn("comp2");
		service.setDeletionSymbol("-");

	}

	@Test
	public void isSensitive_should_return_true() {
		assertTrue(service.isMachineSensitive("MY-PC") == true);
	}

	@Test
	public void isSensitive_should_return_false() {
		assertTrue(service.isMachineSensitive("YOUR-PC") == false);
	}

	@Test
	public void test_loadSensitiveMachinesFromMongo_output() {
		Set<String> out = new HashSet<String>();
		out.add("comp1");
		out.add("comp2");
		assertEquals(service.loadSensitiveMachinesFromMongo(), out);
	}

	@Test
	public void test_adding_existing_sensitiveMachine_to_sensitivemachines() {
		creatingMachinesFile("DUMMY-PC");
		when(computerRepository.findByName(anyString())).thenReturn(
				new Computer());
		service.updateSensitiveMachines();
		assertTrue(service.getSensitiveMachines().contains("DUMMY-PC") == true);
	}
	
	@Test
	public void test_adding_three_sensitiveMachines_to_sensitivemachines() {
		creatingMachinesFile("DUMMY-PC\nX-PC\nY-PC");
		when(computerRepository.findByName(anyString())).thenReturn(
				new Computer());
		service.updateSensitiveMachines();
		assertTrue(service.getSensitiveMachines().contains("DUMMY-PC") == true);
		assertTrue(service.getSensitiveMachines().contains("X-PC") == true);
		assertTrue(service.getSensitiveMachines().contains("Y-PC") == true);
	}
	
	@Test
	public void test_adding_not_existed_machine_to_sensitive_machines() {
		creatingMachinesFile("DUMMY-PC\n ");
		when(computerRepository.findByName(anyString())).thenReturn(
				null);
		service.updateSensitiveMachines();
		assertTrue(service.getSensitiveMachines().contains("DUMMY-PC") == false);
		
	}
	
	@Test
	public void test_removing_sensitive_machine_from_sensitive_machines() {
		creatingMachinesFile("-MY-PC");
		when(computerRepository.findByName(anyString())).thenReturn(
				new Computer());
		service.updateSensitiveMachines();
		assertTrue(service.getSensitiveMachines().contains("MY-PC") == false);
	}
	
	@Test
	public void test_removing_not_sensitive_machine_from_sensitive_machines() {
		creatingMachinesFile("-DUMMY-PC");
		when(computerRepository.findByName(anyString())).thenReturn(
				new Computer());
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
