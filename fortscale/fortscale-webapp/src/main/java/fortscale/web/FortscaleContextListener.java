package fortscale.web;

import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContextEvent;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by rans on 24/09/15.
 */
public class FortscaleContextListener extends ContextLoaderListener {
    @Override
    public final void contextInitialized(final ServletContextEvent sce) {
        super.contextInitialized(sce);
        //set default timezone to UTC for the GUI
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = runtimeMxBean.getInputArguments();
        boolean userTimezoneExists = false;
        for (String argument : arguments){
            if (argument.toLowerCase().startsWith("-duser.timezone")){
                userTimezoneExists = true;
            }
        }
        if (!userTimezoneExists) {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        }

    }
}
