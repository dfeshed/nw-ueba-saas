package fortscale.utils.shell.service.config;

import fortscale.utils.shell.commands.config.ShellCommonCommandsConfig;
import fortscale.utils.shell.service.ShellService;
import fortscale.utils.shell.service.ShellServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.shell.core.JLineShellComponent;

import java.io.IOException;
import java.util.List;

@Configuration

@Import({JLineShellComponentConfig.class, ShellCommonCommandsConfig.class})

public class ShellServiceConfig {

    //    @Value("${fortscale.shellService.thread.disable}")
    @Value("false")
    boolean shellThreadDisabled;

    @Autowired
    JLineShellComponent shellComponent;

//    @Autowired
//    StandardProcessService standardProcessService;

    @Bean
    public ShellService shellService() throws IOException {

//        Namespace namespace = standardProcessService.getParsedArgs();
        List<String> shellCommandsToExecute = ShellServiceImpl.PresidioExecutionParams.getExecutionCommands();
//        if(namespace.get("eval")!=null)
//        {
//            if(namespace.get("eval") instanceof List)
//            {
//                shellCommandsToExecute.addAll(namespace.get("eval"));
//            }
//            else
//            {
//                shellCommandsToExecute.add(namespace.getString("eval"));
//            }
//        }
//        String commandLineFile = standardProcessService.getParsedArgs().getString("eval_file");
//
//        if(commandLineFile!=null)
//        {
//            shellCommandsToExecute.addAll(Files.readAllLines(Paths.get(commandLineFile)));
//        }

        return new ShellServiceImpl(shellComponent, shellThreadDisabled, shellCommandsToExecute);
    }
}
