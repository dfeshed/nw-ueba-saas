package com.rsa.netwitness.presidio.automation.converter.conveters.active_directory;

import com.rsa.netwitness.presidio.automation.converter.conveters.EventConverter;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.mapping.operation_type.ActiveDirectoryOperationTypeMapping;
import org.assertj.core.util.Lists;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.common.random.RandomListElementGenerator;

import java.util.List;
import java.util.Optional;


public class NetwitnessActiveDirectoryEventConverter implements EventConverter<ActiveDirectoryEvent> {

    private static final List<String> refIds = Lists.newArrayList("4741", "4742", "4733", "4734", "4740",
            "4794", "5376", "5377", "5136", "4764", "4670", "4743", "4739", "4727", "4728", "4754", "4756", "4757",
            "4758", "4720", "4722", "4723", "4724", "4725", "4726", "4738", "4767", "4717", "4729", "4730", "4731", "4732");

    @Override
    public NetwitnessEvent convert(ActiveDirectoryEvent event) {
        RandomListElementGenerator<String> referenceIdsGen = new RandomListElementGenerator<>(refIds);
        NetwitnessActiveDirectoryEventBuilder builder = new NetwitnessActiveDirectoryEventBuilder(event);

        Optional<Integer> eventCode = ActiveDirectoryOperationTypeMapping.getInstance()
                .getEventCode(event.getOperation().getOperationType().getName());

        int refId = eventCode.orElse(Integer.valueOf(referenceIdsGen.getNext()));
        return builder.getByRefId(String.valueOf(refId));
    }
}


