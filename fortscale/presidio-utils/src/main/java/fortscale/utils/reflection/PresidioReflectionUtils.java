package fortscale.utils.reflection;

import fortscale.utils.hierarchy.HierarchyIterator;
import fortscale.utils.hierarchy.HierarchyLeafFinder;
import fortscale.utils.logging.Logger;
import org.reflections.Reflections;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.*;

import static org.apache.commons.lang3.Validate.notNull;
import static org.springframework.util.ReflectionUtils.findField;

public class PresidioReflectionUtils extends HierarchyLeafFinder<Object> {

    private static final String NULL_CLAZZ_EXCEPTION_MESSAGE =
            "'clazz' cannot be null.";
    private static final String FIELD_NOT_DECLARED_EXCEPTION_MESSAGE_FORMAT =
            "Class '%s' does not declare a field named '%s'.";
    private static Logger logger = Logger.getLogger(PresidioReflectionUtils.class);


    @Override
    public boolean isNull(Object object) {
        return object == null;
    }

    @Override
    protected Object getChild(Object parent, String subFieldName) throws Exception {
        return getAccessibleField(parent.getClass(), subFieldName).get(parent);
    }

    public static List<Field> findNestedFields(Class clazz, String fieldName) {
        notNull(clazz, NULL_CLAZZ_EXCEPTION_MESSAGE);
        notNull(fieldName, NULL_FIELD_NAME_EXCEPTION_MESSAGE);
        List<Field> fields = new ArrayList<>();
        for (String subFieldName: new HierarchyIterator(fieldName)) {
            Field field = getAccessibleField(clazz, subFieldName);
            fields.add(field);
            clazz = field.getType();
        }
        return fields;
    }

    public static <T> Collection<Class<? extends T>> getSubTypes(List<String> packagePaths, Class<T> parentClass) {
        Set<Class<? extends T>> allSubTypes = new HashSet<>();
        for (String packagePath: packagePaths) {
            allSubTypes.addAll(new Reflections(packagePath).getSubTypesOf(parentClass));
        }
        return allSubTypes;
    }

    private static @NotNull Field getAccessibleField(@NotNull Class clazz, @NotNull String fieldName) {
        Field field = findField(clazz, fieldName);
        notNull(field, FIELD_NOT_DECLARED_EXCEPTION_MESSAGE_FORMAT, clazz.getName(), fieldName);
        field.setAccessible(true);
        return field;
    }

    /**
     * Retrieves the type of the property with the given name of the given
     * Class.<br>
     * Supports nested properties following bean naming convention.
     *
     * "foo.bar.name"
     *
     * @param clazz
     * @param fieldName
     *
     * @return Object if no property exists.
     */
    public static Class<?> getPropertyType(Class<?> clazz, String fieldName) {
        notNull(clazz, NULL_CLAZZ_EXCEPTION_MESSAGE);
        notNull(fieldName, NULL_FIELD_NAME_EXCEPTION_MESSAGE);

        List<Field> fields;

        try {
            fields = findNestedFields(clazz, fieldName);
        } catch (Exception e) {
            logger.error("Failed to find field name {} in {} Class", fieldName, clazz, e);
            return Object.class;
        }

        return fields.get(fields.size() - 1).getType();
    }
}
