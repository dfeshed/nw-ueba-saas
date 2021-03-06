package fortscale.common.shell.command;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultPromptProvider;
import org.springframework.stereotype.Component;

/**
 * Created by barak_schuster on 7/14/16.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FSPromptProvider extends DefaultPromptProvider {

    @Value("${spring.application.name}")
    String processName;

    @Override
    public String getPrompt() {
        return String.format("fs %s>", processName);
    }


    @Override
    public String getProviderName() {
        return String.format("%s prompt provider");
    }

}