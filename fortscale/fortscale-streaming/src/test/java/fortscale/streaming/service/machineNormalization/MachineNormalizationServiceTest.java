package fortscale.streaming.service.machineNormalization;

import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.machineNormalization.MachineNormalizationConfig;
import fortscale.streaming.service.machineNormalization.MachineNormalizationFieldsConfig;
import fortscale.streaming.service.machineNormalization.MachineNormalizationService;
import net.minidev.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;

public class MachineNormalizationServiceTest {

    @Test
    public void normalizeEventTest()
    {
        List<MachineNormalizationFieldsConfig> machineNormalizationFieldsConfigList = new ArrayList();
        machineNormalizationFieldsConfigList.add(new MachineNormalizationFieldsConfig("source-hostname","normalized_src_machine"));
        machineNormalizationFieldsConfigList.add(new MachineNormalizationFieldsConfig("dest-hostname","normalized_dst_machine"));
        MachineNormalizationConfig machineNormalizationConfig = new MachineNormalizationConfig("datasource","laststate","outputtopic","partitionField",machineNormalizationFieldsConfigList);
        Map<StreamingTaskDataSourceConfigKey, MachineNormalizationConfig> machineNormalizationConfigs = new HashedMap();
        machineNormalizationConfigs.put(new StreamingTaskDataSourceConfigKey("datasource","laststate"),machineNormalizationConfig);
        MachineNormalizationService machineNormalizationService = new MachineNormalizationService(machineNormalizationConfigs);

        JSONObject event = new JSONObject();
        event.put("source-hostname","my-pc.my-domain.com");
        event.put("dest-hostname","my-server.my-domain.com");

        event=machineNormalizationService.normalizeEvent(machineNormalizationConfig,event);

        assertEquals("MY-PC",event.get("normalized_src_machine"));
        assertEquals("MY-SERVER",event.get("normalized_dst_machine"));
    }

}
