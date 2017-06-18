package fortscale.utils.shell;

import fortscale.utils.shell.service.BootShim;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.core.CommandResult;
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class ShellServiceTest {

    @Autowired
    ConfigurableApplicationContext ctx;

    @Configuration
    @ComponentScan(value = "fortscale.utils.shell.testCommands")
    static public class StatSpringConfig {}

    @Test
    public void testRunCommand() {
        String[] args = {"calc", "--firstNumber", "1", "--secondNumber", "2", "--operator", "+"};
        BootShim bs = new BootShim(args, ctx);
        ExitShellRequest exitShellRequest = bs.run();
//        JLineShellComponent shell = this.ctx.getBean(BootShim.SHELL_BEAN_NAME, JLineShellComponent.class);
//        CommandResult cr = shell.executeCommand("calc --firstNumber 1 --secondNumber 2 --operator +");

        Assert.assertEquals(ExitShellRequest.NORMAL_EXIT, exitShellRequest);
//        Assert.assertEquals("executing: 1+2 sum:3", cr.getResult());
    }

    @Test
    public void testRunCommandInvalidParams() {
        String[] args = {"calc"}; //missing parameters
        BootShim bs = new BootShim(args, ctx);
        ExitShellRequest exitShellRequest = bs.run();
//        JLineShellComponent shell = this.ctx.getBean(BootShim.SHELL_BEAN_NAME, JLineShellComponent.class);
//        CommandResult cr = shell.executeCommand("calc --firstNumber 1 --secondNumber 2 --operator +");

        Assert.assertEquals(ExitShellRequest.FATAL_EXIT, exitShellRequest);
    }

}
