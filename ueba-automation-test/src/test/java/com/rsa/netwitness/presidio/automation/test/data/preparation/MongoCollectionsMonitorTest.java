package com.rsa.netwitness.presidio.automation.test.data.preparation;

import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.repository.AdapterTlsStoredDataRepository;
import com.rsa.netwitness.presidio.automation.domain.tls.AdapterTlsStoredData;
import com.rsa.netwitness.presidio.automation.log_player.MongoCollectionsMonitor;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;


@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class MongoCollectionsMonitorTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private AdapterTlsStoredDataRepository tlsRepository;

    MongoCollectionsMonitor task;


    @Test
    public void tls_input_table_query_test() {
        Sort sort = new Sort(Sort.Direction.DESC, "dateTime");
        AdapterTlsStoredData result = tlsRepository
                .findTopByDateTimeBetween(Instant.now().minus(15, DAYS), Instant.now(), sort);

        assertThat(result).isNotNull();
    }

    @Test
    public void mongo_monitoring_test() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        task = new MongoCollectionsMonitor(Lists.newArrayList(tlsRepository));

        setField("TIME_UNITS", SECONDS);
        setField("DELAY_BEFORE_FIRST_TASK_STARTED", 3);
        setField("TASK_STATUS_CHECK_FREQUENCY", 150);
        setField("TASK_FREQUENCY_MINUTES", 150);

        task.createTasks(Instant.now().minus(15, DAYS), Instant.now());
        task.execute();
        boolean result = task.waitForResult();
        task.shutdown();

        System.out.println("BLA");

    }

    private void setField(String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        Class<? extends MongoCollectionsMonitor> aClass = task.getClass();
        Field f1 = aClass.getDeclaredField(field);
        f1.setAccessible(true);
        f1.set(task, value);
    }
}