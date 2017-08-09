package presidio.ade.domain.record.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import presidio.ade.domain.record.AdeRecord;

/**
 * Scan given class path for records.
 * Add ade record type and pojo class to the map.
 * For example: dlp_file, EnrichedDlpFileRecord.class
 */
public class AdeEventTypeToAdeRecordClassResolver<T extends AdeRecord> extends KeyToAdeRecordClassResolver<String,T> {
    /**
     * @param scanPackage class path to scan
     */
    public AdeEventTypeToAdeRecordClassResolver(String scanPackage) {
        super(scanPackage);
    }

    /**
     * Add AdeRecordMetadata class to the map
     *
     * SuppressWarnings: Class.forName return Class<?>, while the map expect to get Class<? extends AdeRecord>
     * @param beanDef bean definition of AdeRecordMetadata class
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void addItemsToMap(BeanDefinition beanDef) {
        try {
            Class<?> pojoClass = Class.forName(beanDef.getBeanClassName());
            if (AdeRecord.class.isAssignableFrom(pojoClass)) {
                AdeRecordMetadata adeRecord = pojoClass.getAnnotation(AdeRecordMetadata.class);
                keyToAdeRecordClassMap.put(adeRecord.adeEventType(), (Class<? extends T>)pojoClass);
            }
        } catch (Exception e) {
            System.err.println("Got exception: " + e.getMessage());
        }
    }

    @Override
    protected AnnotationTypeFilter getAnnotationTypeFilter() {
        return new AnnotationTypeFilter(AdeRecordMetadata.class);
    }

}







