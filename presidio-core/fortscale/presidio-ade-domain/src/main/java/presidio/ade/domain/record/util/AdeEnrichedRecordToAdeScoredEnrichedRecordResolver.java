package presidio.ade.domain.record.util;

import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;

/**
 * Created by YaronDL on 8/2/2017.
 */
public class AdeEnrichedRecordToAdeScoredEnrichedRecordResolver extends KeyToAdeRecordClassResolver<Class<? extends EnrichedRecord>, AdeScoredEnrichedRecord> {
    private static final Logger logger = Logger.getLogger(AdeEnrichedRecordToAdeScoredEnrichedRecordResolver.class);

    /**
     * @param scanPackage class path to scan
     */
    public AdeEnrichedRecordToAdeScoredEnrichedRecordResolver(String scanPackage) {
        super(scanPackage);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addItemsToMap(BeanDefinition beanDef) {
        try {
            Class<?> pojoClass = Class.forName(beanDef.getBeanClassName());
            AdeScoredEnrichedMetadata adeScoredEnrichedMetadata = pojoClass.getAnnotation(AdeScoredEnrichedMetadata.class);
            keyToAdeRecordClassMap.put(adeScoredEnrichedMetadata.enrichedRecord(), (Class<AdeScoredEnrichedRecord>)pojoClass);
        } catch (Exception e) {
            logger.error("Got exception: {}", e.getMessage(), e);
        }
    }

    @Override
    protected AnnotationTypeFilter getAnnotationTypeFilter() {
        return new AnnotationTypeFilter(AdeScoredEnrichedMetadata.class);
    }
}
