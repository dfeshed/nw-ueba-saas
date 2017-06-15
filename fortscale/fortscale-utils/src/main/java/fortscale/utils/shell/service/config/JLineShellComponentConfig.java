package fortscale.utils.shell.service.config;

import fortscale.utils.shell.service.FortscaleJLineShellComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.CommandLine;
import org.springframework.shell.core.JLineShellComponent;

import java.io.IOException;


@Configuration
@ComponentScan(
        basePackages = {
                "org.springframework.shell.commands",
                "org.springframework.shell.converters",
                "org.springframework.shell.plugin.support"}
)
public class JLineShellComponentConfig {
    //    @Value("${fortscale.shellService.historySize}")
    @Value("50")
    int historySize;


    // comandLine bean is needed for JLineShellComponent
    @Bean
    public CommandLine commandLine() throws IOException {

        return new CommandLine(null, historySize, null);
    }


    @Bean
    public JLineShellComponent shell() {
        return new FortscaleJLineShellComponent();
    }
}
