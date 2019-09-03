package presidio.input.core.services.transformation;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.general.Schema;
import fortscale.utils.reflection.PresidioReflectionUtils;
import fortscale.utils.transform.AbstractJsonObjectTransformer;
import fortscale.utils.transform.IJsonObjectTransformer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DeserializerTransformationService implements ApplicationContextAware {

    private static final String SCHEMA = "schema";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String TRANSFORMERS_PACKAGE_LOCATION = "fortscale.utils.transform";
    private static final String TRANSFORMERS_PACKAGE_LOCATION_INPUT = "presidio.input.core.services.transformation.transformer";

    private String configurationFilePath;
    private ObjectMapper objectMapper;
    private List<IJsonObjectTransformer> transformers = new ArrayList<>();
    private ApplicationContext applicationContext;

    public DeserializerTransformationService(ObjectMapper objectMapper, String configurationFilePath) {
        this.objectMapper = objectMapper;
        this.configurationFilePath = configurationFilePath;
    }

    public List<IJsonObjectTransformer> getTransformers(Schema schema, Instant startDate, Instant endDate) {
        try {
            //Inject runtime dynamic values to object mapper
            InjectableValues.Std injectableValues = new InjectableValues.Std();
            injectableValues.addValue(SCHEMA, schema);
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
     * Autowire Beans and @Value properties after json was desirialize and call postAutowireProcessor.
     * @param transformer IJsonObjectTransformer
     */
    public void autowireProcessor(IJsonObjectTransformer transformer) {
        List<Object> result = PresidioReflectionUtils.findNestedObjectsByType(transformer, IJsonObjectTransformer.class);
        result.add(transformer);
        result.forEach(obj -> {
                    applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(obj, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
                    ((IJsonObjectTransformer) obj).postAutowireProcessor();
                }
        );
    }


    private void registerTransformerSubTypes(ObjectMapper objectMapper) {
        Collection<Class<? extends AbstractJsonObjectTransformer>> subTypes =
                PresidioReflectionUtils.getSubTypes(new String[]{TRANSFORMERS_PACKAGE_LOCATION},
                AbstractJsonObjectTransformer.class);
        List<Class<?>> subTypesAsGenericClasses = subTypes.stream().map(x -> (Class<?>) x).collect(Collectors.toList());
        objectMapper.registerSubtypes(subTypesAsGenericClasses);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}