package fortscale.services.computer.classifier;

import static org.junit.Assert.*;

import java.util.Date;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fortscale.domain.core.Computer;
import fortscale.domain.core.ComputerUsageClassifier;
import fortscale.domain.core.ComputerUsageType;


@RunWith(JUnitParamsRunner.class)
public class OperatingSystemClassifierTest {

	private OperatingSystemClassifier classifier;
	
	@Before
	public void setUp() {
		classifier = new OperatingSystemClassifier();
	}
	
	private Computer getComputer() {
		Computer computer = new Computer();
		computer.setName("My-PC");
		computer.setDistinguishedName("CN=My-PC,CN=Computers,DC=Fortscale,DC=DOM");
		computer.setWhenCreated(new Date(500L));
		computer.setWhenChanged(new Date(500L));
		
		return computer;
	}
	
	
	@Test
	public void missing_os_should_not_put_classification() {
		Computer computer = getComputer();
		
		classifier.classify(computer);
				 
		assertTrue(computer.getUsageClassifiers().isEmpty());
	}
	
	@Test
	public void classifier_should_only_create_one_classification() {
		Computer computer = getComputer();
		computer.setOperatingSystem("Windows 7 Ultimate");
		computer.putUsageClassifier(new ComputerUsageClassifier(OperatingSystemClassifier.CLASSIFIER_NAME, ComputerUsageType.Server));
		
		classifier.classify(computer);
		
		assertTrue(computer.getUsageClassifiers().size()==1);
	}
	
	@Test
	@Parameters({ 
		"Windows 7 Professional, false, true",
		"Windows 7 Ultimate, false, true",
		"unknown, null, null",
		"Windows Server 2008 R2 Standard, true, false",
		"Windows 8.1 Enterprise, false, true",
		"Mac OS X, false, true",
		"Debian, null, null"
	})
	public void check_os_detection(String os, Boolean isServer, Boolean isEndpoint) {
		Computer computer = getComputer();
		computer.setOperatingSystem(os);
		
		classifier.classify(computer);
		
		assertNotNull(computer.getUsageClassifier(OperatingSystemClassifier.CLASSIFIER_NAME));
		if (isServer)
			assertTrue(ComputerUsageType.Server==computer.getUsageClassifier(OperatingSystemClassifier.CLASSIFIER_NAME).getUsageType());
		if (isEndpoint)
			assertTrue(ComputerUsageType.Desktop==computer.getUsageClassifier(OperatingSystemClassifier.CLASSIFIER_NAME).getUsageType());
	}
	
}
