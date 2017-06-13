package presidio.ade.domain.record.scanning;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import presidio.ade.domain.record.AdeRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * Scan given class path for records.
 * Add ade record type and pojo class to the map.
 * For example: dlp_file, EnrichedDlpFileRecord.class
 */
public class AdeRecordTypeToClass {

    private Map<String, Class<? extends AdeRecord>> adeRecordTypeToClass;

    /**
     * @param scanPackage class path to scan
     */
    public AdeRecordTypeToClass(String scanPackage) {
        adeRecordTypeToClass = new HashMap<>();
        findAnnotatedClasses(scanPackage);
    }

    /**
     * Add AdeRecordMetadata class to the map
     *
     * @param beanDef bean definition of AdeRecordMetadata class
     */
    private void addItemsToMap(BeanDefinition beanDef) {
        try {
            Class<?> pojoClass = Class.forName(beanDef.getBeanClassName());
            if (AdeRecord.class.isAssignableFrom(pojoClass)) {
                AdeRecordMetadata adeRecord = pojoClass.getAnnotation(AdeRecordMetadata.class);
                adeRecordTypeToClass.put(adeRecord.type(), (Class<? extends AdeRecord>) pojoClass);
            }
        } catch (Exception e) {
            System.err.println("Got exception: " + e.getMessage());
        }
    }

    public Class<? extends AdeRecord> getClass(String dataSource) {
        return adeRecordTypeToClass.get(dataSource);
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
        provider.addIncludeFilter(new AnnotationTypeFilter(AdeRecordMetadata.class));
        return provider;
    }

}







