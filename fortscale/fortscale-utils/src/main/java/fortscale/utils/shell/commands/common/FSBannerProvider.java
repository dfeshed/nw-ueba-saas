package fortscale.utils.shell.commands.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

/**
 * Created by barak_schuster on 7/14/16.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FSBannerProvider extends DefaultBannerProvider {
    @Value("application.name")
            String processName;

    @Override
    public String getBanner() {
        StringBuffer buf = new StringBuffer();
        buf.append("===============================================================" + OsUtils.LINE_SEPARATOR);
        buf.append("*                                                            *" + OsUtils.LINE_SEPARATOR);
        buf.append("*  _____              __                       .__           *" + OsUtils.LINE_SEPARATOR);
        buf.append("*_/ ____\\____________/  |_  ______ ____ _____  |  |   ____   *" + OsUtils.LINE_SEPARATOR);
        buf.append("*\\   __\\/  _ \\_  __ \\   __\\/  ___// ___\\\\__  \\ |  | _/ __ \\  *" + OsUtils.LINE_SEPARATOR);
        buf.append("* |  | (  <_> )  | \\/|  |  \\___ \\\\  \\___ / __ \\|  |_\\  ___/  *" + OsUtils.LINE_SEPARATOR);
        buf.append("* |__|  \\____/|__|   |__| /____  >\\___  >____  /____/\\___  > *" + OsUtils.LINE_SEPARATOR);
        buf.append("*                              \\/     \\/     \\/          \\/  *" + OsUtils.LINE_SEPARATOR);
        buf.append("*                                                            *" + OsUtils.LINE_SEPARATOR);
        buf.append("===============================================================" + OsUtils.LINE_SEPARATOR);
        buf.append("Process:" + processName);
        return buf.toString();
    }

    @Override
    public String getWelcomeMessage() {
        return String.format("Welcome to %s CLI", processName);
    }

    @Override
    public String getProviderName() {
        return String.format("%s Banner", processName);
    }
}
