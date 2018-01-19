package presidio.output.commons.services.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import presidio.output.commons.services.user.UserSeverityComputeData;
import presidio.output.commons.services.user.UserSeverityService;
import presidio.output.commons.services.user.UserSeverityServiceImpl;
import presidio.output.domain.records.users.UserSeverity;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class UserSeverityServiceConfig {

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

    @Bean
    public UserSeverityService userSeverityService() {

        Map<UserSeverity, UserSeverityComputeData> severityToComputeDataMap = new LinkedHashMap<>();
        severityToComputeDataMap.put(UserSeverity.CRITICAL, new UserSeverityComputeData(new Double(userSeverityComputeDataCriticalPercentageOfUsers),
                new Double(userSeverityComputeDataCriticalMinimumDeltaFactor), new Double(userSeverityComputeDataCriticalMaximumUsers)));
        severityToComputeDataMap.put(UserSeverity.HIGH, new UserSeverityComputeData(new Double(userSeverityComputeDataHighPercentageOfUsers),
                new Double(userSeverityComputeDataHighMinimumDeltaFactor), new Double(userSeverityComputeDataHighMaximumUsers)));
        severityToComputeDataMap.put(UserSeverity.MEDIUM, new UserSeverityComputeData(new Double(userSeverityComputeDataMediumPercentageOfUsers),
                new Double(userSeverityComputeDataMediumDeltaFactor)));

        severityToComputeDataMap.put(UserSeverity.LOW, new UserSeverityComputeData(new Double(userSeverityComputeDataLowPercentageOfUsers)));

        return new UserSeverityServiceImpl(severityToComputeDataMap);
    }

}
