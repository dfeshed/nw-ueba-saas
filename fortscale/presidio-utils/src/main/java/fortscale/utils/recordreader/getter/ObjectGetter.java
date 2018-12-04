package fortscale.utils.recordreader.getter;

import fortscale.utils.recordreader.transformation.Transformation;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

public class ObjectGetter extends Getter {
    private static final Map<Class<?>, Fields> classToFieldsMap = new HashMap<>();

    private final Object object;
    private final Class<?> clazz;
    private final Fields fields;

    public ObjectGetter(Map<String, Transformation<?>> transformations, Object object) {
        super(transformations);
        this.object = Objects.requireNonNull(object);
        this.clazz = object.getClass();
        this.fields = classToFieldsMap.computeIfAbsent(clazz, Fields::new);
    }

    @Override
    Class<?> getInstanceClass() {
        return clazz;
    }

    @Override
    boolean isPresentInInstance(String key) {
        return fields.getOptionalField(key).isPresent();
    }

    @Override
    Object getFromInstance(String key) {
        Optional<Field> optionalField = fields.getOptionalField(key);

        if (optionalField.isPresent()) {
            try { return optionalField.get().get(object); }
            catch (IllegalAccessException e) { throw new RuntimeException(e); }
        } else {
            throw new NoSuchElementException(String.format("Class %s does not " +
                    "contain a field named %s.", clazz.getSimpleName(), key));
        }
    }

    private static final class Fields {
        private final Class<?> clazz;
        private final Map<String, Optional<Field>> nameToOptionalFieldMap;

        public Fields(Class<?> clazz) {
            this.clazz = Objects.requireNonNull(clazz);
            this.nameToOptionalFieldMap = new HashMap<>();
        }

        public Optional<Field> getOptionalField(String name) {
            return nameToOptionalFieldMap.computeIfAbsent(name, key -> {
                Field field = ReflectionUtils.findField(clazz, name);
                if (field != null) field.setAccessible(true);
                return Optional.ofNullable(field);
            });
        }
    }
}
