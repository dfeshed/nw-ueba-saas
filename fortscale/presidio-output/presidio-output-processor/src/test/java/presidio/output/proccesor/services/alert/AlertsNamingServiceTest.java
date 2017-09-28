package presidio.output.proccesor.services.alert;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.processor.services.alert.AlertClassificationService;
import presidio.output.processor.spring.AlertClassificationPriorityConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes = {AlertClassificationPriorityConfig.class})
public class AlertsNamingServiceTest {


    @Autowired
    private AlertClassificationService alertClassificationService;


    @Test
    public void classificationsAsTowOptions() {
        List indicators = new ArrayList(Arrays.asList("high_number_of_distinct_src_computers", "high_number_of_distinct_dst_computers"));
        List<String> names = alertClassificationService.getAlertClassificationsFromIndicatorsByPriority(indicators);
        assertEquals(2, names.size());
    }

    @Test
    public void classificationsAsOneOption() {
        List indicators = new ArrayList(Arrays.asList("high_number_of_distinct_src_computers", "high_number_of_distinct_dst_computers"));
        List<String> names = alertClassificationService.getAlertClassificationsFromIndicatorsByPriority(indicators);
        assertEquals("user_logged_into_multiple_hosts", names.get(0));
    }
}


