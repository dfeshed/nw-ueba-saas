package presidio.output.commons.services.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.commons.services.entity.EntitySeverityComputeData;
import presidio.output.commons.services.entity.EntitySeverityService;
import presidio.output.commons.services.entity.EntitySeverityServiceImpl;
import presidio.output.domain.records.entity.EntitySeverity;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@Import(EntityUpdatePropertiesServiceConfig.class)
public class EntitySeverityServiceConfig {

    @Value("${entity.severity.compute.data.critical.percentage.of.entities:1}")
    private String entitySeverityComputeDataCriticalPercentageOfEntities;

    @Value("${entity.severity.compute.data.critical.minimum.delta.factor:1.5}")
    private String entitySeverityComputeDataCriticalMinimumDeltaFactor;

    @Value("${entity.severity.compute.data.critical.maximum.entities:5}")
    private String entitySeverityComputeDataCriticalMaximumEntities;

    @Value("${entity.severity.compute.data.high.percentage.of.entities:4}")
    private String entitySeverityComputeDataHighPercentageOfEntities;

    @Value("${entity.severity.compute.data.high.minimum.delta.factor:1.3}")
    private String entitySeverityComputeDataHighMinimumDeltaFactor;

    @Value("${entity.severity.compute.data.high.maximum.entities:10}")
    private String entitySeverityComputeDataHighMaximumEntities;

    @Value("${entity.severity.compute.data.medium.percentage.of.entities:10}")
    private String entitySeverityComputeDataMediumPercentageOfEntities;

    @Value("${entity.severity.compute.data.medium.minimum.delta.factor:1.1}")
    private String entitySeverityComputeDataMediumDeltaFactor;

    @Value("${entity.severity.compute.data.low.percentage.of.entities:80}")
    private String entitySeverityComputeDataLowPercentageOfEntities;

    @Bean
    public EntitySeverityService entitySeverityService() {

        Map<EntitySeverity, EntitySeverityComputeData> severityToComputeDataMap = new LinkedHashMap<>();
        severityToComputeDataMap.put(EntitySeverity.CRITICAL, new EntitySeverityComputeData(new Double(entitySeverityComputeDataCriticalPercentageOfEntities),
                new Double(entitySeverityComputeDataCriticalMinimumDeltaFactor), new Double(entitySeverityComputeDataCriticalMaximumEntities)));
        severityToComputeDataMap.put(EntitySeverity.HIGH, new EntitySeverityComputeData(new Double(entitySeverityComputeDataHighPercentageOfEntities),
                new Double(entitySeverityComputeDataHighMinimumDeltaFactor), new Double(entitySeverityComputeDataHighMaximumEntities)));
        severityToComputeDataMap.put(EntitySeverity.MEDIUM, new EntitySeverityComputeData(new Double(entitySeverityComputeDataMediumPercentageOfEntities),
                new Double(entitySeverityComputeDataMediumDeltaFactor)));

        severityToComputeDataMap.put(EntitySeverity.LOW, new EntitySeverityComputeData(new Double(entitySeverityComputeDataLowPercentageOfEntities)));

        return new EntitySeverityServiceImpl(severityToComputeDataMap, new OutputToCollectionNameTranslator());
    }

}
