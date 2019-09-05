package presidio.input.core.services.transformation;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import fortscale.common.general.Schema;
import fortscale.utils.reflection.PresidioReflectionUtils;
import fortscale.utils.transform.AbstractJsonObjectTransformer;
import fortscale.utils.transform.IJsonObjectTransformer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


public class DeserializerTransformationService implements ApplicationContextAware {

    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String TRANSFORMERS_UTIL_PACKAGE_LOCATION = "fortscale.utils.transform";
    private static final String TRANSFORMERS_INPUT_PACKAGE_LOCATION = "presidio.input.core.services.transformation.transformer";
    private String configurationFilePath;
    private ObjectMapper objectMapper;
    private ApplicationContext applicationContext;

    public DeserializerTransformationService(String configurationFilePath) {
        this.objectMapper = new ObjectMapper();
        this.configurationFilePath = configurationFilePath;
    }

    public List<IJsonObjectTransformer> getTransformers(Schema schema, Instant startDate, Instant endDate) {
        try {
            List<IJsonObjectTransformer> transformers = new ArrayList<>();
            //Inject runtime dynamic values to object mapper
            InjectableValues.Std injectableValues = new InjectableValues.Std();
            injectableValues.addValue(START_DATE, startDate);
            injectableValues.addValue(END_DATE, endDate);
            objectMapper.setInjectableValues(injectableValues);

            // Register all transformer subtypes so that the object mapper can deserialize them by their 'type'
            registerTransformerSubTypes(objectMapper);

            IJsonObjectTransformer transformer = objectMapper.readValue(new File(String.format("%s%s.json", configurationFilePath, schema.getName())), IJsonObjectTransformer.class);
            autowireProcessor(transformer);
            transformers.add(transformer);
            return transformers;
        } catch (Exception e) {
            String msg = String.format("Failed deserialize %s.", configurationFilePath);
            throw new IllegalArgumentException(msg, e);
        }
    }

    /**
     * Autowire Beans and @Value properties after json was desirialize and invoke  post constructor method.
     *
     * @param transformer IJsonObjectTransformer
     */
    public void autowireProcessor(IJsonObjectTransformer transformer) {
        autowire(transformer, new HashSet<>());
    }


    private void registerTransformerSubTypes(ObjectMapper objectMapper) {
        Collection<Class<? extends AbstractJsonObjectTransformer>> subTypes =
                PresidioReflectionUtils.getSubTypes(new String[]{TRANSFORMERS_INPUT_PACKAGE_LOCATION, TRANSFORMERS_UTIL_PACKAGE_LOCATION},
                AbstractJsonObjectTransformer.class);
        List<Class<?>> subTypesAsGenericClasses = subTypes.stream().map(x -> (Class<?>) x).collect(Collectors.toList());
        objectMapper.registerSubtypes(subTypesAsGenericClasses);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    private void autowire(Object objectToScan, Set<Object> scanned) {
        if (objectToScan == null) {
            return;
        }
        //prevent endless scan loops
        if (!scanned.add(objectToScan)) {
            return;
        }

        Class clazz = objectToScan.getClass();
        for (Field declaredField : getDeclaredFieldsUpFrom(clazz)) {
            // skip static fields
            if (Modifier.isStatic(declaredField.getModifiers())) {
                continue;
            }
            // skip primitives
            if (declaredField.getType().isPrimitive()) {
                continue;
            }
            try {
                declaredField.setAccessible(true);
                if (Collection.class.isAssignableFrom(declaredField.getType())) {
                    Collection<?> collection = (Collection) declaredField.get(objectToScan);
                    if (collection != null) {
                        collection.forEach(item -> autowire(item, scanned));
                    }
                } else if (Map.class.isAssignableFrom(declaredField.getType())) {
                    Map<?, ?> map = (Map) declaredField.get(objectToScan);
                    if (map != null) {
                        map.forEach((key, value) -> {
                            if (!key.getClass().isPrimitive()) {
                                autowire(key, scanned);
                            }
                            if (!value.getClass().isPrimitive()) {
                                autowire(value, scanned);
                            }
                        });
                    }
                } else {
                    Object item = declaredField.get(objectToScan);
                    autowire(item, scanned);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(objectToScan, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        invokePostConstructMethod(objectToScan);
    }

    /**
     * @param clazz start class
     * @return list of all fields for clazz and base classes
     */
    private List<Field> getDeclaredFieldsUpFrom(Class clazz) {

        List<Field> currentClassFields = Lists.newArrayList(clazz.getDeclaredFields());
        Class<?> parentClass = clazz.getSuperclass();
        if (parentClass != null) {
            List<Field> parentClassFields = (List<Field>) getDeclaredFieldsUpFrom(parentClass);
            currentClassFields.addAll(parentClassFields);
        }

        return currentClassFields;
    }


    /**
     * Invoke post constructor method
     *
     * @param objectToScan
     */
    private void invokePostConstructMethod(Object objectToScan) {
        try {
            Method[] methods = objectToScan.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    method.invoke(objectToScan);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}