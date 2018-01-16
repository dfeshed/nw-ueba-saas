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

/**
 * Created by Efrat Noam on 12/4/17.
 */
@Configuration
public class UserSeverityServiceConfig {

    public static final String COMMA = ",";

    @Value("${user.severity.compute.data.critical:1,1.5,5}")
    private String userSeverityComputeDataCritical;

    @Value("${user.severity.compute.data.high:4,1.3,10}")
    private String userSeverityComputeDataHigh;

    @Value("${user.severity.compute.data.medium:10,1.1,9999}")
    private String userSeverityComputeDataMedium;

    @Value("${user.severity.compute.data.low:80,0,9999}")
    private String userSeverityComputeDataLow;

    @Bean
    public UserSeverityService userSeverityService() {

        Map<UserSeverity, UserSeverityComputeData> severityToComputeDataMap = new LinkedHashMap<>();

        String[] criticalData = userSeverityComputeDataCritical.split(COMMA);
        severityToComputeDataMap.put(UserSeverity.CRITICAL, new UserSeverityComputeData(new Double(criticalData[0]), new Double(criticalData[1]), new Double(criticalData[2])));

        String[] highData = userSeverityComputeDataHigh.split(COMMA);
        severityToComputeDataMap.put(UserSeverity.HIGH, new UserSeverityComputeData(new Double(highData[0]), new Double(highData[1]), new Double(highData[2])));

        String[] mediumData = userSeverityComputeDataMedium.split(COMMA);
        severityToComputeDataMap.put(UserSeverity.MEDIUM, new UserSeverityComputeData(new Double(mediumData[0]), new Double(mediumData[1]), new Double(mediumData[2])));

        String[] lowData = userSeverityComputeDataLow.split(COMMA);
        severityToComputeDataMap.put(UserSeverity.LOW, new UserSeverityComputeData(new Double(lowData[0]), new Double(lowData[1]), new Double(lowData[2])));

        return new UserSeverityServiceImpl(severityToComputeDataMap);
    }

}
