package fortscale.utils.elasticsearch.services;


public interface TemplateExtractor {

    String mappingConverting(Class<?> clazz);

    String settingsConverting(Class<?> clazz);
}
