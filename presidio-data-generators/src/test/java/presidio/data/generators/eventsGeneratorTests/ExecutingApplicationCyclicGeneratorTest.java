package presidio.data.generators.eventsGeneratorTests;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.dlpfileop.DEFAULT_APPLICATIONS;
import presidio.data.generators.event.dlpfile.ExecutingApplicationCyclicGenerator;

public class ExecutingApplicationCyclicGeneratorTest {

    @Test
    public void ExecutingApplicationCyclicGeneratorTest() {
        ExecutingApplicationCyclicGenerator EAG = new ExecutingApplicationCyclicGenerator();

        Assert.assertEquals(EAG.getNext(), DEFAULT_APPLICATIONS.POWERSHELL_EXE.value);
        Assert.assertEquals(EAG.getNext(), DEFAULT_APPLICATIONS.POWERCFG_EXE.value);
        Assert.assertEquals(EAG.getNext(), DEFAULT_APPLICATIONS.EVENTVWR_EXE.value);
        Assert.assertEquals(EAG.getNext(), DEFAULT_APPLICATIONS.COMPMGMT_EXE.value);
        Assert.assertEquals(EAG.getNext(), DEFAULT_APPLICATIONS.TASKSCHD_EXE.value);
        Assert.assertEquals(EAG.getNext(), DEFAULT_APPLICATIONS.SECPOL_EXE.value);

        Assert.assertEquals(EAG.getNext(), DEFAULT_APPLICATIONS.POWERSHELL_EXE.value); //cyclic
    }

    @Test
    public void ExecutingApplicationCyclicGenerator1Test() {
        String[] evTypes = {"app1", "app2"};
        ExecutingApplicationCyclicGenerator EAG = new ExecutingApplicationCyclicGenerator(evTypes);

        Assert.assertEquals(EAG.getNext(), "app1");
        Assert.assertEquals(EAG.getNext(), "app2");

    }
}
