package com.rsa.netwitness.presidio.automation.converter.conveters.active_directory;

import com.rsa.netwitness.presidio.automation.converter.conveters.INetwitnessEventConverter;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.mapping.operation_type.ActiveDirectoryOperationTypeMapping;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.common.StringCyclicValuesGenerator;

import java.util.Optional;


public class NetwitnessActiveDirectoryEventConverter implements INetwitnessEventConverter<ActiveDirectoryEvent> {

    private static final String[] referenceIds = new String[]{"4741", "4742", "4733", "4734", "4740", "4794",
            "5376", "5377", "5136", "4764", "4670", "4743", "4739", "4727", "4728", "4754", "4756", "4757", "4758",
            "4720", "4722", "4723", "4724", "4725", "4726", "4738", "4767", "4717", "4729", "4730", "4731", "4732"};

    private static StringCyclicValuesGenerator counter = new StringCyclicValuesGenerator(referenceIds);


    @Override
    public NetwitnessEvent convert(ActiveDirectoryEvent event) {
        NetwitnessActiveDirectoryEventBuilder builder = new NetwitnessActiveDirectoryEventBuilder(event);

        Optional<Integer> eventCode = ActiveDirectoryOperationTypeMapping.getInstance()
                .getEventCode(event.getOperation().getOperationType().getName());

        int refId = eventCode.orElse(Integer.valueOf(counter.getNext()));
        return builder.getByRefId(String.valueOf(refId));
    }

}
