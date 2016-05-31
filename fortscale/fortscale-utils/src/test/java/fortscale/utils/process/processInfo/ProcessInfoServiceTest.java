package fortscale.utils.process.processInfo;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Assert;
import java.io.File;



public class ProcessInfoServiceTest {

    @Test
    public void shouldCreatePidFile() {
        String pidFilePath = "/var/run/fortscale/processGroupName/processName.pid";
        File pidFile = new File(pidFilePath );
        ProcessInfoService processInfoService = new ProcessInfoServiceImpl("processName","processGroupName");
        processInfoService.init();
        Assert.assertEquals(true, pidFile.exists());
        processInfoService.shutdown();
        Assert.assertEquals(false, pidFile.exists());


    }

}
