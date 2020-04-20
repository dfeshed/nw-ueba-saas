package presidio.output.processor.services.alert.indicator.enricher;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.context.ApplicationContext;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.processor.config.IndicatorConfig;

/**
 * For correct deserialization and dependency injection,
 * implement {@link Implementable} and not {@link IndicatorEnricher}.
 *
 * @author Lior Govrin.
 */
public interface IndicatorEnricher {
    void enrichIndicator(IndicatorConfig indicatorConfig, Indicator indicator);

    @JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "class")
    interface Implementable extends IndicatorEnricher {
        void setApplicationContext(ApplicationContext applicationContext);
    }
}
