package fortscale.utils.monitoring.stats.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * An helper class that enable multiple StatsLongMetricParams annotation per field.
 *
 * Application shall not use this annotation directly.
 *
 * Created by gaashh on 4/4/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StatsLongMetricParamsList {
    StatsLongMetricParams[] value();
}
