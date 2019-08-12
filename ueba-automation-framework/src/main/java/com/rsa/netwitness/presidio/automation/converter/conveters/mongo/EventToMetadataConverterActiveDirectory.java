package com.rsa.netwitness.presidio.automation.converter.conveters.mongo;

import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.StringCyclicValuesGenerator;

import java.util.HashMap;
import java.util.Map;

public class EventToMetadataConverterActiveDirectory implements EventToMetadataConverter<ActiveDirectoryEvent> {
    private static final String[] referenceIds = new String[]{"4741", "4742", "4733", "4734", "4740", "4794", "5376", "5377", "5136", "4764", "4670", "4743", "4739", "4727", "4728", "4754", "4756", "4757", "4758", "4720", "4722", "4723", "4724", "4725", "4726", "4738", "4767", "4717", "4729", "4730", "4731", "4732"};
    private static IStringGenerator referenceIdGenerator =  new StringCyclicValuesGenerator(referenceIds);

    @Override
    public Map<String, Object> convert(ActiveDirectoryEvent event) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("event_source_id", event.getEventId());
        metadata.put("event_time", String.valueOf(event.getDateTime().toEpochMilli()));
        metadata.put("mongo_source_event_time", event.getDateTime());
        metadata.put("user_dst", event.getUser().getUserId());
        metadata.put("reference_id", chooseReferenceId(event, referenceIdGenerator.getNext()));
        metadata.put("event_type", (event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE"));
        putObjectId(event, metadata);
        putSecondaryObjectId(event, metadata);
        metadata.put("device_type", "winevent_nic");
        return metadata;
    }

    public static String chooseReferenceId(ActiveDirectoryEvent event, String customReferenceId) {
        String operationType = event.getOperation().getOperationType().getName();
        // noinspection IfCanBeSwitch
        if      (operationType.equals(AD_OPERATION_TYPE.ATTEMPT_MADE_TO_SET_DIRECTORY_SERVICES_RESTORE_MODE_ADMINISTRATOR_PASSWORD.value)) return("4794");
        if (operationType.equals(AD_OPERATION_TYPE.COMPUTER_ACCOUNT_CHANGED.value)) return("4742");
        if (operationType.equals(AD_OPERATION_TYPE.COMPUTER_ACCOUNT_CREATED.value)) return("4741");
        if (operationType.equals(AD_OPERATION_TYPE.COMPUTER_ACCOUNT_DELETED.value)) return("4743");
        if (operationType.equals(AD_OPERATION_TYPE.CREDENTIAL_MANAGER_CREDENTIALS_BACKED_UP.value)) return("5376");
        if (operationType.equals(AD_OPERATION_TYPE.CREDENTIAL_MANAGER_CREDENTIALS_RESTORED_FROM_BACKUP.value)) return("5377");
        if (operationType.equals(AD_OPERATION_TYPE.DIRECTORY_SERVICE_OBJECT_MODIFIED.value)) return("5136");
        if (operationType.equals(AD_OPERATION_TYPE.DOMAIN_POLICY_CHANGED.value)) return("4739");
        if (operationType.equals(AD_OPERATION_TYPE.GROUP_TYPE_CHANGED.value)) return("4764");
        if (operationType.equals(AD_OPERATION_TYPE.MEMBER_ADDED_TO_SECURITY_ENABLED_GLOBAL_GROUP.value)) return("4728");
        if (operationType.equals(AD_OPERATION_TYPE.MEMBER_ADDED_TO_SECURITY_ENABLED_LOCAL_GROUP.value)) return("4732");
        if (operationType.equals(AD_OPERATION_TYPE.MEMBER_ADDED_TO_SECURITY_ENABLED_UNIVERSAL_GROUP.value)) return("4756");
//        else if (operationType.equals(AD_OPERATION_TYPE.MEMBER_REMOVED_FROM_GROUP.value)) return("4757"); /** CA operation type - from generated events scenario **/
        if (operationType.equals(AD_OPERATION_TYPE.MEMBER_REMOVED_FROM_SECURITY_ENABLED_GLOBAL_GROUP.value)) return("4729");
        if (operationType.equals(AD_OPERATION_TYPE.MEMBER_REMOVED_FROM_SECURITY_ENABLED_LOCAL_GROUP.value)) return("4733");
        if (operationType.equals(AD_OPERATION_TYPE.MEMBER_REMOVED_FROM_SECURITY_ENABLED_UNIVERSAL_GROUP.value)) return("4757");
        if (operationType.equals(AD_OPERATION_TYPE.PERMISSIONS_ON_OBJECT_CHANGED.value)) return("4670");
        if (operationType.equals(AD_OPERATION_TYPE.SECURITY_ENABLED_GLOBAL_GROUP_CREATED.value)) return("4727");
        if (operationType.equals(AD_OPERATION_TYPE.SECURITY_ENABLED_GLOBAL_GROUP_DELETED.value)) return("4730");
        if (operationType.equals(AD_OPERATION_TYPE.SECURITY_ENABLED_LOCAL_GROUP_CREATED.value)) return("4731");
        if (operationType.equals(AD_OPERATION_TYPE.SECURITY_ENABLED_LOCAL_GROUP_DELETED.value)) return("4734");
        if (operationType.equals(AD_OPERATION_TYPE.SECURITY_ENABLED_LOCAL_GROUP_CHANGED.value)) return("4735");
        if (operationType.equals(AD_OPERATION_TYPE.SECURITY_ENABLED_UNIVERSAL_GROUP_CREATED.value)) return("4754");
        if (operationType.equals(AD_OPERATION_TYPE.SECURITY_ENABLED_UNIVERSAL_GROUP_DELETED.value)) return("4758");
        if (operationType.equals(AD_OPERATION_TYPE.SYSTEM_SECURITY_ACCESS_GRANTED_TO_ACCOUNT.value)) return("4717");
        if (operationType.equals(AD_OPERATION_TYPE.USER_ACCOUNT_CHANGED.value)) return("4738");
        if (operationType.equals(AD_OPERATION_TYPE.USER_ACCOUNT_CREATED.value)) return("4720");
        if (operationType.equals(AD_OPERATION_TYPE.USER_ACCOUNT_DELETED.value)) return("4726");
        if (operationType.equals(AD_OPERATION_TYPE.USER_ACCOUNT_DISABLED.value)) return("4725");
        if (operationType.equals(AD_OPERATION_TYPE.USER_ACCOUNT_ENABLED.value)) return("4722");
        if (operationType.equals(AD_OPERATION_TYPE.USER_ACCOUNT_LOCKED.value)) return("4740");
        if (operationType.equals(AD_OPERATION_TYPE.USER_ACCOUNT_UNLOCKED.value)) return("4767");
        if (operationType.equals(AD_OPERATION_TYPE.USER_PASSWORD_CHANGED.value)) return("4723");
        if (operationType.equals(AD_OPERATION_TYPE.USER_PASSWORD_RESET.value)) return("4724");
        return customReferenceId;
    }

    private static void putObjectId(ActiveDirectoryEvent event, Map<String, Object> metadata) {
        Object referenceId = metadata.get("reference_id");
        if (referenceId == null) return;
        String objectId = event.getObjectName(); // TODO: No getObjectId()?
        if (referenceId.equals("4741")) metadata.put("user_src", objectId);
        else if (referenceId.equals("4742")) metadata.put("user_src", objectId);
        else if (referenceId.equals("4733")) metadata.put("user_src", objectId);
        else if (referenceId.equals("4734")) metadata.put("group", objectId);
        else if (referenceId.equals("4740")) metadata.put("user_src", objectId);
        else if (referenceId.equals("5136")) metadata.put("obj_name", objectId);
        else if (referenceId.equals("4764")) metadata.put("group", objectId);
        else if (referenceId.equals("4670")) metadata.put("obj_name", objectId);
        else if (referenceId.equals("4743")) metadata.put("user_src", objectId);
        else if (referenceId.equals("4727")) metadata.put("group", objectId);
        else if (referenceId.equals("4728")) metadata.put("user_src", objectId);
        else if (referenceId.equals("4754")) metadata.put("group", objectId);
        else if (referenceId.equals("4756")) metadata.put("user_src", objectId);
        else if (referenceId.equals("4757")) metadata.put("user_src", objectId);
        else if (referenceId.equals("4758")) metadata.put("group", objectId);
        else if (referenceId.equals("4720")) metadata.put("user_src", objectId);
        else if (referenceId.equals("4722")) metadata.put("user_src", objectId);
        else if (referenceId.equals("4723")) metadata.put("user_src", objectId);
        else if (referenceId.equals("4724")) metadata.put("user_src", objectId);
        else if (referenceId.equals("4725")) metadata.put("user_src", objectId);
        else if (referenceId.equals("4726")) metadata.put("user_src", objectId);
        else if (referenceId.equals("4738")) metadata.put("user_src", objectId);
        else if (referenceId.equals("4767")) metadata.put("user_src", objectId);
        else if (referenceId.equals("4717")) metadata.put("user_src", objectId);
        else if (referenceId.equals("4729")) metadata.put("user_src", objectId);
        else if (referenceId.equals("4730")) metadata.put("group", objectId);
        else if (referenceId.equals("4731")) metadata.put("group", objectId);
        else if (referenceId.equals("4732")) metadata.put("user_src", objectId);
    }

    private static void putSecondaryObjectId(ActiveDirectoryEvent event, Map<String, Object> metadata) {
        Object referenceId = metadata.get("reference_id");
        if (referenceId == null) return;
        String secondaryObjectId = "temporary"; // TODO: event.getAdditionalInfo().get("secondaryObjectId");
        if (referenceId.equals("4733")) metadata.put("group", secondaryObjectId);
        else if (referenceId.equals("4728")) metadata.put("group", secondaryObjectId);
        else if (referenceId.equals("4756")) metadata.put("group", secondaryObjectId);
        else if (referenceId.equals("4757")) metadata.put("group", secondaryObjectId);
        else if (referenceId.equals("4717")) metadata.put("accesses", secondaryObjectId);
        else if (referenceId.equals("4729")) metadata.put("group", secondaryObjectId);
        else if (referenceId.equals("4732")) metadata.put("group", secondaryObjectId);
    }
}
