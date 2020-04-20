package com.rsa.netwitness.presidio.automation.converter.formatters;

import com.rsa.netwitness.presidio.automation.converter.events.AwsEvent;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import org.assertj.core.util.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AwsFormatter implements EventFormatter<NetwitnessEvent, Map<String, List<Map<String, Object>>>>  {

    @Override
    public Map<String, List<Map<String, Object>>> format(NetwitnessEvent event) {
        AwsEvent awsEvent = new AwsEvent(event);
        Map<String, List<Map<String, Object>>> converted = new HashMap<>();
        converted.put("records", Lists.newArrayList(awsEvent.getEvent()));
        return converted;
    }
}
