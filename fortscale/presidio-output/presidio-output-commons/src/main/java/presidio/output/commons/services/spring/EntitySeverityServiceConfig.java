package presidio.output.commons.services.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import presidio.output.commons.services.entity.EntityPropertiesUpdateService;
import presidio.output.commons.services.entity.EntitySeverityComputeData;
import presidio.output.commons.services.entity.EntitySeverityService;
import presidio.output.commons.services.entity.EntitySeverityServiceImpl;
import presidio.output.domain.records.entity.EntitySeverity;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@Import(EntityUpdatePropertiesServiceConfig.class)
public class EntitySeverityServiceConfig {

    @Value("${user.severity.compute.data.critical.percentage.of.users:1}")
    private String userSeverityComputeDataCriticalPercentageOfUsers;

    @Value("${user.severity.compute.data.critical.minimum.delta.factor:1.5}")
    private String userSeverityComputeDataCriticalMinimumDeltaFactor;

    @Value("${user.severity.compute.data.critical.maximum.users:5}")
    private String userSeverityComputeDataCriticalMaximumUsers;

    @Value("${user.severity.compute.data.high.percentage.of.users:4}")
    private String userSeverityComputeDataHighPercentageOfUsers;

    @Value("${user.severity.compute.data.high.minimum.delta.factor:1.3}")
    private String userSeverityComputeDataHighMinimumDeltaFactor;

    @Value("${user.severity.compute.data.high.maximum.users:10}")
    private String userSeverityComputeDataHighMaximumUsers;

    @Value("${user.severity.compute.data.medium.percentage.of.users:10}")
    private String userSeverityComputeDataMediumPercentageOfUsers;

    @Value("${user.severity.compute.data.medium.minimum.delta.factor:1.1}")
    private String userSeverityComputeDataMediumDeltaFactor;

    @Value("${user.severity.compute.data.low.percentage.of.users:80}")
    private String userSeverityComputeDataLowPercentageOfUsers;

    @Autowired
    private EntityPropertiesUpdateService entityPropertiesUpdateService;

    @Bean
    public EntitySeverityService entitySeverityService() {

        Map<EntitySeverity, EntitySeverityComputeData> severityToComputeDataMap = new LinkedHashMap<>();
        severityToComputeDataMap.put(EntitySeverity.CRITICAL, new EntitySeverityComputeData(new Double(userSeverityComputeDataCriticalPercentageOfUsers),
                new Double(userSeverityComputeDataCriticalMinimumDeltaFactor), new Double(userSeverityComputeDataCriticalMaximumUsers)));
        severityToComputeDataMap.put(EntitySeverity.HIGH, new EntitySeverityComputeData(new Double(userSeverityComputeDataHighPercentageOfUsers),
                new Double(userSeverityComputeDataHighMinimumDeltaFactor), new Double(userSeverityComputeDataHighMaximumUsers)));
        severityToComputeDataMap.put(EntitySeverity.MEDIUM, new EntitySeverityComputeData(new Double(userSeverityComputeDataMediumPercentageOfUsers),
                new Double(userSeverityComputeDataMediumDeltaFactor)));

        severityToComputeDataMap.put(EntitySeverity.LOW, new EntitySeverityComputeData(new Double(userSeverityComputeDataLowPercentageOfUsers)));

        return new EntitySeverityServiceImpl(severityToComputeDataMap, entityPropertiesUpdateService);
    }

}
