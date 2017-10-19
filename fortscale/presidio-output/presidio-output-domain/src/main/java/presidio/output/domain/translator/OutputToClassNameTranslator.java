package presidio.output.domain.translator;

import fortscale.common.general.Schema;
import org.springframework.beans.BeanUtils;
import presidio.output.domain.records.events.EnrichedEvent;

public class OutputToClassNameTranslator {

    private static final String PACKAGE_NAME = EnrichedEvent.class.getPackage().getName();
    private static final String ENRICHED_EVENT_NAME = EnrichedEvent.class.getSimpleName();

    public String toClassName(Schema schema) {
        return String.format(PACKAGE_NAME + ".%s" + ENRICHED_EVENT_NAME, schema.toCamelCase());
    }
}
