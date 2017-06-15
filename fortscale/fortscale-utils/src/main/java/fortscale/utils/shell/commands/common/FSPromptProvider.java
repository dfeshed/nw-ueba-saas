package fortscale.utils.shell.commands.common;

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

    //    @Value("${fortscale.process.name}")
    @Value("dummy process name") //todo change this
            String processName;

    @Override
    public String getPrompt() {
        return String.format("fs%s>", processName);
    }


    @Override
    public String getProviderName() {
        return String.format("%s prompt provider");
    }

}