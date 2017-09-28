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
    public void classificationsAsTowOptionsTest() {
        List indicators = new ArrayList(Arrays.asList("high_number_of_distinct_src_computers", "high_number_of_distinct_dst_computers"));
        List<String> names = alertClassificationService.getAlertClassificationsFromIndicatorsByPriority(indicators);
        assertEquals(2, names.size());
    }

    @Test
    public void classificationsAsOneOptionTest() {
        List indicators = new ArrayList(Arrays.asList("high_number_of_distinct_src_computers", "high_number_of_distinct_dst_computers"));
        List<String> names = alertClassificationService.getAlertClassificationsFromIndicatorsByPriority(indicators);
        assertEquals(2, names.size());
    }

    @Test
    public void classificationsAsFourOptionsTest() {
        List indicators = new ArrayList(Arrays.asList("high_number_of_file_move_operations_to_shared_drive",
                "abnormal_file_action_operation_type",
                "high_number_of_successful_file_action_operations",
                "abnormal_active_directory_day_time_operation"));
        List<String> names = alertClassificationService.getAlertClassificationsFromIndicatorsByPriority(indicators);
        assertEquals("snooping_user", names.get(0));
        assertEquals(4, names.size());
    }

    @Test
    public void classificationsAsThreeOptionsTest() {
        List indicators = new ArrayList(Arrays.asList("high_number_of_failed_file_action_attempts",
                "high_number_of_successful_file_action_operations",
                "abnormal_file_action_operation_type",
                "high_number_of_file_move_operations_to_shared_drive"));
        List<String> names = alertClassificationService.getAlertClassificationsFromIndicatorsByPriority(indicators);
        assertEquals("snooping_user", names.get(0));
        assertEquals(3, names.size());
    }

    @Test
    public void classificationsAsSixOptionsTest() {
        List indicators = new ArrayList(Arrays.asList("high_number_of_distinct_src_computer_clusters",
                "high_number_of_successful_file_permission_change",
                "high_number_of_distinct_src_computers",
                "high_number_of_senesitive_group_membership_events",
                "high_number_of_failed_file_permission_change_attempts",
                "abnormal_remote_destination_machine",
                "high_number_of_successful_file_rename_operations",
                "high_number_of_distinct_src_computer_clusters"));
        List<String> names = alertClassificationService.getAlertClassificationsFromIndicatorsByPriority(indicators);
        assertEquals("mass_changes_to_critical_enterprise_groups", names.get(0));
        assertEquals(6, names.size());
    }
}


