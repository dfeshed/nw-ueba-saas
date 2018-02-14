package fortscale.utils.elasticsearch.services;


public interface FileToStringCreating {

    String mappingConverting(Class<?> clazz);

    String settingsConverting(Class<?> clazz);
}
