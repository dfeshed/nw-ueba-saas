package presidio.output.proccesor.services;


import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import net.minidev.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.processor.services.alert.AlertNamingService;
import presidio.output.processor.spring.AlertNamingConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes = {AlertNamingConfig.class, AlertsNamingServceTest.SpringConfig.class})
public class AlertsNamingServceTest {


    @Autowired
    private AlertNamingService alertNamingService;

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
        Map<String, String> map = new HashMap<>();
        map.put("aggregated_feature_name", "Abnormal file action operation type");
        JSONObject obj1 = new JSONObject(map);
        map.clear();
        map.put("aggregated_feature_name", "Abnormal logon day time");
        JSONObject obj2 = new JSONObject(map);
        List<JSONObject> indicators = new ArrayList();
        indicators.add(obj1);
        indicators.add(obj2);
        List<String> names = alertNamingService.alertNamesFromIndictorsByPriority(indicators);
        assertEquals(2, names.size());
    }

    @Test
    public void classiticationsAsOneOption() {
        Map<String, String> map = new HashMap<>();
        map.put("aggregated_feature_name", "Abnormal event day time");
        JSONObject obj1 = new JSONObject(map);
        map.clear();
        map.put("aggregated_feature_name", "Abnormal Active Directory day time operation");
        JSONObject obj2 = new JSONObject(map);
        List<JSONObject> indicators = new ArrayList();
        indicators.add(obj1);
        indicators.add(obj2);
        List<String> names = alertNamingService.alertNamesFromIndictorsByPriority(indicators);
        assertEquals(1, names.size());
    }
}


