package fortscale.common.shell.command;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultHistoryFileNameProvider;
import org.springframework.stereotype.Component;

/**
 * Created by barak_schuster on 7/14/16.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FSHistoryFileNameProvider extends DefaultHistoryFileNameProvider {

    public String getHistoryFileName() {
        return "/dev/null" ;
    }

    @Override
    public String getProviderName() {
        return "spring shell /dev/null log name provider";
    }
}
