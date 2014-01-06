package fortscale.collection.morphlines;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;


public class OpenVPNMorphlineTests {

	private MorphlinesItemsProcessor subject;
	
	@Before
	public void setUp() throws Exception {
		
		Resource conf = new FileSystemResource("target/resources/readVPN_openVPN.conf");
		subject = new MorphlinesItemsProcessor(conf);
	}

	@Test
	public void test() {
		Object result = subject.process("dummy line");
		Assert.isTrue(result==null);
	}

}
