package presidio.ade.domain.record.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import presidio.ade.domain.record.AdeRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by YaronDL on 8/2/2017.
 */

public abstract class KeyToAdeRecordClassResolver<K,T extends AdeRecord> {

    protected Map<K, Class<? extends T>> keyToAdeRecordClassMap;

    /**
     * @param scanPackage class path to scan
     */
    public KeyToAdeRecordClassResolver(String scanPackage) {
        keyToAdeRecordClassMap = new HashMap<>();
        findAnnotatedClasses(scanPackage);
    }

    /**
     * Add AdeRecordMetadata class to the map
     *
     * SuppressWarnings: Class.forName return Class<?>, while the map expect to get Class<? extends AdeRecord>
     * @param beanDef bean definition of AdeRecordMetadata class
     */
    @SuppressWarnings("unchecked")
    protected abstract void addItemsToMap(BeanDefinition beanDef);

    protected abstract AnnotationTypeFilter getAnnotationTypeFilter();

    public Class<? extends T> getClass(K key) {
        return keyToAdeRecordClassMap.get(key);
    }

    /**
     * Get the provider and scan the class path for candidate components (e.g: EnrichedDlpFileRecord).
     *
     * @param scanPackage class path to scan
     */
    public void findAnnotatedClasses(String scanPackage) {
        // A component provider that scans the classpath
        ClassPathScanningCandidateComponentProvider provider = createComponentScanner();

        //Scan the class path for candidate components.
        for (BeanDefinition beanDef : provider.findCandidateComponents(scanPackage)) {
            addItemsToMap(beanDef);
        }
    }

    /**
     * Create provider that scans the classpath from a base package.
     *
     * @return provider
     */
    private ClassPathScanningCandidateComponentProvider createComponentScanner() {
        // A component provider that scans the classpath from a base package.
        // Don't pull default filters (@Component, etc.).
        ClassPathScanningCandidateComponentProvider provider
                = new ClassPathScanningCandidateComponentProvider(false);

        //Create new AnnotationTypeFilter for the AdeRecordMetadata.class and add an it to the provider.
        provider.addIncludeFilter(getAnnotationTypeFilter());
        return provider;
    }

}