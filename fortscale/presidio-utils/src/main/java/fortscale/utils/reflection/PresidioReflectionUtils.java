package fortscale.utils.reflection;

import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.Validate;
import org.springframework.util.ReflectionUtils;

import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.*;

import static java.lang.String.format;
import static org.apache.commons.lang3.Validate.notNull;
import static org.springframework.util.ReflectionUtils.findField;

public class PresidioReflectionUtils {
    private static final String NULL_CLAZZ_EXCEPTION_MESSAGE =
            "'clazz' cannot be null.";
    private static final String NULL_FIELD_NAME_EXCEPTION_MESSAGE =
            "'fieldName' cannot be null.";
    private static final String BROKEN_HIERARCHY_MESSAGE_FORMAT =
            "Cannot get the leaf of field '%s' because the instance of the class declaring subfield '%s' is null.";
    private static final String FIELD_NOT_DECLARED_EXCEPTION_MESSAGE_FORMAT =
            "Class '%s' does not declare a field named '%s'.";

    private static final String FIELD_NAME_DELIMITING_REGEX = "\\.";

    public static Object getFieldValue(Object object, String fieldName) {
        Leaf leaf = getLeaf(object, fieldName);
        leaf.assertHierarchyNotBroken();
        return leaf.getValue();
    }

    public static Object getFieldValue(Object object, String fieldName, Object defaultValue) {
        Leaf leaf = getLeaf(object, fieldName);
        return leaf.isHierarchyBroken() ? defaultValue : leaf.getValue();
    }

    /**
     * Sets the given object's field to the given value. This method works for nested objects also. If one would like to
     * change a nested object's field, the nested object delimiter should be found between objects in the field name.
     * The exception should not be thrown because the accessibility flag of the Field object is set to true.
     *
     * For example:
     * ============
     * setFieldValue(object = {
     *     "name": "Aaron",
     *     "cat": {
     *         "toy": "mouse"
     *     }
     * }, fieldName = "cat.toy", fieldValue = "rabbit")
     * Becomes:
     * ========
     * {
     *     "name": "Aaron",
     *     "cat": {
     *         "toy": "rabbit"
     *     }
     * }
     *
     * @param object     the given object
     * @param fieldName  the field name belonging to the object which should be set to the given value
     * @param fieldValue the value to set the object's field to
     */
    public static void setFieldValue(Object object, String fieldName, Object fieldValue) throws IllegalAccessException {
        Leaf leaf = getLeaf(object, fieldName);
        leaf.assertHierarchyNotBroken();
        leaf.getField().set(leaf.getParent(), fieldValue);
    }

    public static List<Field> findNestedFields(Class clazz, String fieldName) {
        notNull(clazz, NULL_CLAZZ_EXCEPTION_MESSAGE);
        notNull(fieldName, NULL_FIELD_NAME_EXCEPTION_MESSAGE);
        List<Field> fields = new ArrayList<>();

        for (String subfieldName : fieldName.split(FIELD_NAME_DELIMITING_REGEX)) {
            Field field = getAccessibleField(clazz, subfieldName);
            fields.add(field);
            clazz = field.getType();
        }

        return fields;
    }

    public static <T> Set<Class<? extends T>> getSubTypes(String[] packagePaths, Class<T> parentClass) {
        Set<Class<? extends T>> allSubTypes = new HashSet<>();
        for (String packagePath: packagePaths) {
            allSubTypes.addAll(new Reflections(packagePath).getSubTypesOf(parentClass));
        }
        return allSubTypes;
    }

    private static Field getAccessibleField(Class clazz, String fieldName) {
        Field field = ReflectionUtils.findField(clazz, fieldName);
        Validate.notNull(field, "Class %s does not contain a field named %s.", clazz.getName(), fieldName);
    private static @NotNull Leaf getLeaf(Object value, @NotNull String fieldName) {
        notNull(fieldName, NULL_FIELD_NAME_EXCEPTION_MESSAGE);
        Object parent = null;
        Field field = null;

        try {
            for (String subfieldName : fieldName.split(FIELD_NAME_DELIMITING_REGEX)) {
                if (value == null) {
                    String brokenHierarchyMessage = format(BROKEN_HIERARCHY_MESSAGE_FORMAT, fieldName, subfieldName);
                    return new Leaf(brokenHierarchyMessage);
                }

                parent = value;
                field = getAccessibleField(parent.getClass(), subfieldName);
                value = field.get(parent);
            }
        } catch (IllegalAccessException e) {
            // Should not be thrown because the accessibility flag of the Field object is set to true.
            throw new RuntimeException(e);
        }

        return new Leaf(parent, field, value);
    }

    private static @NotNull Field getAccessibleField(@NotNull Class clazz, @NotNull String fieldName) {
        Field field = findField(clazz, fieldName);
        notNull(field, FIELD_NOT_DECLARED_EXCEPTION_MESSAGE_FORMAT, clazz.getName(), fieldName);
        field.setAccessible(true);
        return field;
    }
}
