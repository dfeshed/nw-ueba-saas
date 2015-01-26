package fortscale.services.computer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import fortscale.services.cache.CacheHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fortscale.services.computer.SensitiveMachineServiceImpl;
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
		computers.add("ANOTHER-PC2");
		when(computerRepository.findNameByIsSensitive(true)).thenReturn(computers);
		CacheHandler<String,String> cache = new CacheHandler<String, String>(String.class) {

			Map<String,String> simpleCacheImpl = new HashMap<>();

			@Override public String get(String key) {
				return simpleCacheImpl.get(key);
			}

			@Override public void put(String key, String value) {
				simpleCacheImpl.put(key,value);
			}

			@Override public void remove(String key) {
				simpleCacheImpl.remove(key);
			}

			@Override public void close() throws IOException {
				simpleCacheImpl = null;
			}
		};
		cache.put("MY-PC","MY-PC");
		cache.put("ANOTHER-PC","ANOTHER-PC");
		service.setCache(cache);
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
		service.refreshSensitiveMachines();
		assertEquals(service.getCache().get("MY-PC"), "MY-PC");
		assertEquals(service.getCache().get("ANOTHER-PC"), "ANOTHER-PC");
		assertEquals(service.getCache().get("ANOTHER-PC2"), "ANOTHER-PC2");
	}

	@Test
	public void test_adding_existing_sensitiveMachine_to_sensitivemachines() throws IOException {
		creatingMachinesFile("DUMMY-PC");
		when(computerRepository.findIfComputerExists(anyString())).thenReturn(
				true);
		service.updateSensitiveMachines();
		assertEquals(service.getCache().get("DUMMY-PC"), "DUMMY-PC");
	}
	
	@Test
	public void test_adding_three_sensitiveMachines_to_sensitivemachines() throws IOException {
		creatingMachinesFile("dummy-pc\nX-PC\nY-PC");
		when(computerRepository.findIfComputerExists(anyString())).thenReturn(
			true);
		service.updateSensitiveMachines();
		assertEquals(service.getCache().get("MY-PC"), "MY-PC");
		assertEquals(service.getCache().get("DUMMY-PC"), "DUMMY-PC");
		assertEquals(service.getCache().get("X-PC"), "X-PC");
		assertEquals(service.getCache().get("Y-PC"), "Y-PC");
	}
	
	@Test
	public void test_adding_not_existed_machine_to_sensitive_machines() throws IOException {
		creatingMachinesFile("DUMMY-PC\n ");
		when(computerRepository.findIfComputerExists(anyString())).thenReturn(
				false);
		service.updateSensitiveMachines();
		assertNull(service.getCache().get("DUMMY-PC"));
	}
	
	@Test
	public void test_removing_sensitive_machine_from_sensitive_machines() throws IOException {
		creatingMachinesFile("-MY-PC");
		when(computerRepository.findIfComputerExists(anyString())).thenReturn(
				true);
		service.updateSensitiveMachines();
		assertNull(service.getCache().get("DUMMY-PC"));
		assertNull(service.getCache().get("MY-PC"));
		assertEquals(service.getCache().get("ANOTHER-PC"), "ANOTHER-PC");
	}
	
	@Test
	public void test_removing_not_sensitive_machine_from_sensitive_machines() throws IOException {
		creatingMachinesFile("-DUMMY-PC");
		when(computerRepository.findIfComputerExists(anyString())).thenReturn(
				true);
		service.updateSensitiveMachines();
		assertNull(service.getCache().get("DUMMY-PC"));
		assertEquals(service.getCache().get("MY-PC"), "MY-PC");
		assertEquals(service.getCache().get("ANOTHER-PC"), "ANOTHER-PC");
		
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
