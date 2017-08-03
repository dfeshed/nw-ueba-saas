package presidio.ade.domain.record.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;

/**
 * Created by YaronDL on 8/2/2017.
 */
public class AdeEnrichedRecordToAdeScoredEnrichedRecordResolver extends KeyToAdeRecordClassResolver<Class<? extends EnrichedRecord>, AdeScoredEnrichedRecord> {
    /**
     * @param scanPackage class path to scan
     */
    public AdeEnrichedRecordToAdeScoredEnrichedRecordResolver(String scanPackage) {
        super(scanPackage);
    }

    @Override
    protected void addItemsToMap(BeanDefinition beanDef) {
        try {
            Class<?> pojoClass = Class.forName(beanDef.getBeanClassName());
            AdeScoredEnrichedMetadata adeScoredEnrichedMetadata = pojoClass.getAnnotation(AdeScoredEnrichedMetadata.class);
            keyToAdeRecordClassMap.put(adeScoredEnrichedMetadata.erichedRecord(), (Class<AdeScoredEnrichedRecord>)pojoClass);
        } catch (Exception e) {
            System.err.println("Got exception: " + e.getMessage());
        }
    }

    @Override
    protected AnnotationTypeFilter getAnnotationTypeFilter() {
        return new AnnotationTypeFilter(AdeScoredEnrichedMetadata.class);
    }
}
