package fortscale.utils.shell;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.shell.CommandLine;
import org.springframework.shell.ShellException;
import org.springframework.shell.SimpleShellCommandLineOptions;
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.support.logging.HandlerUtils;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BootShim {

    public static final String SHELL_BEAN_NAME = "shell";

    private CommandLine commandLine;
    private ConfigurableApplicationContext ctx;


    public BootShim(String[] args, ConfigurableApplicationContext context) {
        this.ctx = context;

        try {
            commandLine = SimpleShellCommandLineOptions.parseCommandLine(args);
        } catch (IOException e) {
            throw new ShellException(e.getMessage(), e);
        }

        this.configureApplicationContext(this.ctx);
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) this.ctx);
        if (commandLine.getDisableInternalCommands()) {
            scanner.scan(new String[]{"org.springframework.shell.converters", "org.springframework.shell.plugin.support"});
        } else {
            scanner.scan(new String[]{"org.springframework.shell.commands", "org.springframework.shell.converters", "org.springframework.shell.plugin.support"});
        }

    }

    private void configureApplicationContext(ConfigurableApplicationContext appctx) {
        this.createAndRegisterBeanDefinition(appctx, JLineShellComponent.class, SHELL_BEAN_NAME);
        appctx.getBeanFactory().registerSingleton("commandLine", commandLine);
    }

    protected void createAndRegisterBeanDefinition(ConfigurableApplicationContext appctx, Class<?> clazz, String name) {
        RootBeanDefinition rbd = new RootBeanDefinition();
        rbd.setBeanClass(clazz);
        DefaultListableBeanFactory bf = (DefaultListableBeanFactory) appctx.getBeanFactory();
        if (name != null) {
            bf.registerBeanDefinition(name, rbd);
        } else {
            bf.registerBeanDefinition(clazz.getSimpleName(), rbd);
        }

    }

    public ExitShellRequest run() {
        String[] commandsToExecuteAndThenQuit = commandLine.getShellCommandsToExecute();
        JLineShellComponent shell = this.ctx.getBean(SHELL_BEAN_NAME, JLineShellComponent.class);
        ExitShellRequest exitShellRequest;
        if (null != commandsToExecuteAndThenQuit) {
            boolean successful = false;
            exitShellRequest = ExitShellRequest.FATAL_EXIT;
            String[] arr$ = commandsToExecuteAndThenQuit;
            int len$ = commandsToExecuteAndThenQuit.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String cmd = arr$[i$];
                successful = shell.executeCommand(cmd).isSuccess();
                if (!successful) {
                    break;
                }
            }

            if (successful) {
                exitShellRequest = ExitShellRequest.NORMAL_EXIT;
            }
        } else {
            shell.start();
            shell.promptLoop();
            exitShellRequest = shell.getExitShellRequest();
            if (exitShellRequest == null) {
                exitShellRequest = ExitShellRequest.NORMAL_EXIT;
            }

            shell.waitForComplete();
        }

        return exitShellRequest;
    }

}
