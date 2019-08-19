package fortscale.utils.mongodb.util;

import fortscale.utils.reflection.PresidioReflectionUtils;

import java.lang.reflect.Field;

public class MongoReflectionUtils extends PresidioReflectionUtils {

    @Override
    public String getConfiguredFieldName(Field field) {
        String fieldName = field.getName();
        if (field.isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Field.class)) {
            fieldName = field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class).value();
        }
        return fieldName;
    }
}
