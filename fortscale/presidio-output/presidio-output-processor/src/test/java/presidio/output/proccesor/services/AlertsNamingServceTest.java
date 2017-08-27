package presidio.output.proccesor.services;


import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.processor.services.alert.AlertClassificationService;
import presidio.output.processor.spring.AlertNamingConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes = {AlertNamingConfig.class, AlertsNamingServceTest.SpringConfig.class})
public class AlertsNamingServceTest {


    @Autowired
    private AlertClassificationService alertClassificationService;

    @Configuration
    @EnableSpringConfigured
    public static class SpringConfig {
        @Bean
        public static TestPropertiesPlaceholderConfigurer testPropertiesPlaceholderConfigurer() {
            Properties properties = new Properties();
            properties.put("alerts.list", "Elevated_Privileges_Granted,Elevated_Privileges_Granted,Mass_Changes_to_Groups,Mass_Changes_to_Critical_Enterprise_Groups");
            properties.put("indicators.list", "Abnormal_event_day_time,Abnormal_Active_Directory_day_time_operation,Abnormal_logon_day_time,Abnormal_file_action_operation_type");
            properties.put("alerts.names.by.priority", "Mass_Changes_to_Critical_Enterprise_Groups,Mass_Changes_to_Groups,Elevated_Privileges_Granted");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }


    @Test
    public void classiticationsAsTowOptions() {
        List indicators = new ArrayList(Arrays.asList("Abnormal file action operation type", "Abnormal logon day time"));
        List<String> names = alertClassificationService.getAlertClassificationsFromIndicatorsByPriority(indicators);
        assertEquals(2, names.size());
    }

    @Test
    public void classiticationsAsOneOption() {
        List indicators = new ArrayList(Arrays.asList("Abnormal event day time", "Abnormal Active Directory day time operation"));
        List<String> names = alertClassificationService.getAlertClassificationsFromIndicatorsByPriority(indicators);
        assertEquals(1, names.size());
    }
}


