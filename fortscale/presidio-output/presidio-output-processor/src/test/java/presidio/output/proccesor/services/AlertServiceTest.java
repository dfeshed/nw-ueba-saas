package presidio.output.proccesor.services;

import fortscale.domain.SMART.EntityEvent;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import net.minidev.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.store.smart.SmartDataStore;
import presidio.ade.domain.store.smart.SmartPageIterator;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.processor.services.OutputExecutionServiceImpl;
import presidio.output.processor.services.alert.AlertServiceImpl;
import presidio.output.processor.spring.AlertServiceElasticConfig;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

/**
 * Created by efratn on 24/07/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes=AlertServiceElasticConfig.class)
public class AlertServiceTest {

    @MockBean
    private AlertPersistencyService alertPersistencyService;

    @MockBean
    private SmartDataStore smartDataStore;

    @Autowired
    private AlertServiceImpl alertService;

    private Instant startTime = Instant.parse("2017-06-06T10:00:00Z");
    private Instant endTime = Instant.parse("2017-06-06T11:00:00Z");
    private TimeRange timeRange = new TimeRange(startTime, endTime);

    public void setup(int smartListSize, int numOfSmartsBelowScoreThreshold, int scoreThreshold) {
        List<EntityEvent> smarts = new ArrayList<EntityEvent>();
        for (int i = 0; i <= numOfSmartsBelowScoreThreshold - 1; i++) {
            smarts.add(generateSingleSmart(scoreThreshold - 1));
        }

        for (int i = numOfSmartsBelowScoreThreshold + 1; i <= smartListSize; i++) {
            smarts.add(generateSingleSmart(scoreThreshold + 1));
        }

        Mockito.when(smartDataStore.readSmarts(timeRange, scoreThreshold)).thenReturn(smarts);
    }

    @Test
    public void allSmartsAboveScoreThreshold() {
        int smartSize = 3;
        int numOfSmartsBelowScoreThreshold = 0;
        setup(smartSize, numOfSmartsBelowScoreThreshold, OutputExecutionServiceImpl.SMART_SCORE_THRESHOLD);
        PageIterator<EntityEvent> smarts = new SmartPageIterator<EntityEvent>(smartDataStore, timeRange, OutputExecutionServiceImpl.SMART_SCORE_THRESHOLD);
        alertService.generateAlerts(smarts);

        ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        Mockito.verify(alertPersistencyService, VerificationModeFactory.times(1)).save(Mockito.anyList());
        Mockito.verify(alertPersistencyService).save(argument.capture());
        final int generatedAlertsListSize = argument.getValue().size();
        assertEquals(smartSize, generatedAlertsListSize);
    }

    private EntityEvent generateSingleSmart(int score) {
        List<FeatureScore> feature_scores = new ArrayList<FeatureScore>();
        Map<String, String> context = new HashMap<String, String>();
        List<JSONObject> aggregated_feature_events = new ArrayList<JSONObject>();

        EntityEvent smart = new EntityEvent(Instant.now().getEpochSecond(), 5.0, score, feature_scores,
                80.0, context, "user_id", Instant.now().getEpochSecond(),
        Instant.now().getEpochSecond(), "entity_event_type", Instant.now().getEpochSecond(),
        aggregated_feature_events, "smart_name");

        return smart;
    }
}
