package presidio.ade.domain.record.util;

import presidio.ade.domain.record.enriched.EnrichedRecord;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by YaronDL on 8/2/2017.
 */
// Make the annotation available at runtime:
@Retention(RetentionPolicy.RUNTIME)
// Allow to use only on types:
@Target(ElementType.TYPE)
public @interface AdeScoredEnrichedMetadata {
    Class<? extends EnrichedRecord> enrichedRecord();
}
