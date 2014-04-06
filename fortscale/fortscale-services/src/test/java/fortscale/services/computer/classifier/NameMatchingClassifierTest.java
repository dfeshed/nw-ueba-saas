package fortscale.services.computer.classifier;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.*;

import java.util.Date;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fortscale.domain.core.Computer;
import fortscale.domain.core.ComputerUsageType;

@RunWith(JUnitParamsRunner.class)
public class NameMatchingClassifierTest {

	private NameMatchingClassifier classifier;
	
	@Before
	public void setUp() {
		classifier = new NameMatchingClassifier();
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
	public void missing_name_should_not_put_classification() {
		Computer computer = getComputer();
		computer.setName(null);
		
		classifier.classify(computer);
				 
		assertTrue(computer.getUsageClassifiers().isEmpty());
	}
	
	
	@Test
	@Parameters
	public void nameDetection(String name, String desktopRegex, String serverRegex, ComputerUsageType usage) {
		Computer computer = getComputer();
		computer.setName(name);
		classifier.setDesktopRegex(desktopRegex);
		classifier.setServerRegex(serverRegex);
		
		classifier.classify(computer);
		
		assertNotNull(computer.getUsageClassifier(NameMatchingClassifier.CLASSIFIER_NAME));
		assertEquals(usage,computer.getUsageClassifier(NameMatchingClassifier.CLASSIFIER_NAME).getUsageType());
	}
	
	@SuppressWarnings("unused")
	private Object[] parametersForNameDetection() {
        return	$(
    		$( "m-1000", "m-\\d+", null, ComputerUsageType.Desktop ),
    		$( "cltvm-02", "m-\\d+", "cltvm-[\\d]+", ComputerUsageType.Server ),
    		$( "cltvm-02", "m-\\d+", "cltvm-[\\d]+|dc-\\d", ComputerUsageType.Server ),
    		$( "cookoo", "m-\\d+", "cltvm-[\\d]+|dc-\\d", ComputerUsageType.Unknown ),
    		$( "M-1000", "m-\\d+", "", ComputerUsageType.Desktop )
		);
	}
	
}
