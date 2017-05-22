package fortscale;

import fortscale.domain.DlpFileRecord;
import fortscale.services.InputProcessServiceImpl;
import fortscale.spring.InputSdkImplConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InputSdkImplConfig.class)
public class FortscaleInputCoreApplicationTest {

	@Autowired
    InputProcessServiceImpl processService;

	@Test
	public void contextLoads() throws Exception {
		processService.store(new DlpFileRecord());

	}

}
