package fortscale.utils.reflection;

import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PresidioReflectionUtils {

    private static final String NESTED_OBJECT_SPLIT_DELIMITER = "\\.";

    public static Object getFieldValue(Object obj, String requestedFieldName) {
        Field field = getAccessibleField(obj.getClass(), requestedFieldName);
        return org.springframework.util.ReflectionUtils.getField(field, obj);
    }

    /**
     * Sets the given object's field to the given value. This method works for nested objects also.
     * If one would like to change a nested object's field, the nested object delimiter should be found between
     * objects in the field name.
     * For example:
     * setFieldValue(obj = {
     *     "name": "Aaron",
     *     "cat": {
     *         "toy": "mouse"
     *     }
     * }, fieldName = "cat.toy", fieldValue = "rabbit")
     * becomes
     * {
     *    "name": "Aaron",
     *     "cat": {
     *       "toy": "rabbit"
*           }
     * }
     *
     * @param obj the given object
     * @param fieldName the field name belonging to the object which should be set to a the given value
     * @param fieldValue the value to set the object's field to
     */
    public static void setFieldValue(Object obj, String fieldName, Object fieldValue) throws IllegalAccessException {
        List<Field> fields = findNestedFields(obj.getClass(), fieldName);
        for (int i = 0; i < fields.size() - 1; i++) {
            obj = fields.get(i).get(obj);
        }
        fields.get(fields.size() - 1).set(obj, fieldValue);
    }

    public static List<Field> findNestedFields(Class clazz, String requestedFieldName) {
        ArrayList<Field> fields = new ArrayList<>();
        for (String fieldName : requestedFieldName.split(NESTED_OBJECT_SPLIT_DELIMITER)) {
            Field field = getAccessibleField(clazz, fieldName);
            fields.add(field);
            clazz = field.getType();
        }
        return fields;
    }

    public static <T> Set<Class<? extends T>> getSubTypes(String packagePath, Class<T> parentClass) {
        Reflections reflections = new Reflections(packagePath);
        return reflections.<T>getSubTypesOf(parentClass);
    }

    private static Field getAccessibleField(Class clazz, String fieldName) {
        Field field = org.springframework.util.ReflectionUtils.findField(clazz, fieldName);
        Objects.requireNonNull(field).setAccessible(true);
        return field;
    }
}
