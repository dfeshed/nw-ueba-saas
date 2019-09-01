package presidio.input.core.services.transformation;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.general.Schema;
import presidio.input.core.services.transformation.transformer.AbstractInputDocumentTransformer;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DeserializerTransformationService {

    private String configurationFilePath;
    private ObjectMapper objectMapper;
    private BeanPropertiesAutowireService beanPropertiesAutowireService;
    private List<AbstractInputDocumentTransformer> transformers = new ArrayList<>();
    private static final String SCHEMA = "schema";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";

    public DeserializerTransformationService(ObjectMapper objectMapper, String configurationFilePath, BeanPropertiesAutowireService beanPropertiesAutowireService){
        this.objectMapper = objectMapper;
        this.configurationFilePath = configurationFilePath;
        this.beanPropertiesAutowireService = beanPropertiesAutowireService;
    }

    public List<AbstractInputDocumentTransformer> getTransformers(Schema schema, Instant startDate, Instant endDate) {
        try {
            //Inject runtime dynamic values to object mapper
            InjectableValues.Std injectableValues = new InjectableValues.Std();
            injectableValues.addValue(SCHEMA, schema);
            injectableValues.addValue(START_DATE, startDate);
            injectableValues.addValue(END_DATE, endDate);
            objectMapper.setInjectableValues(injectableValues);

            AbstractInputDocumentTransformer transformer = objectMapper.readValue(new File(String.format("%s%s.json", configurationFilePath, schema.getName())), AbstractInputDocumentTransformer.class);
            transformer.postAutowireProcessor(beanPropertiesAutowireService);
            transformers.add(transformer);
            return transformers;
        } catch (Exception e) {
            String msg = String.format("Failed deserialize %s.", configurationFilePath);
            throw new IllegalArgumentException(msg, e);
        }
    }
}