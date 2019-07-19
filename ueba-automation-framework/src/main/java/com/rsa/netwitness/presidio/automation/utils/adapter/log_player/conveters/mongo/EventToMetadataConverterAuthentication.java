package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.conveters.mongo;

import com.rsa.netwitness.presidio.automation.utils.adapter.ReferenceIdGeneratorFactory;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.IStringGenerator;

import java.util.*;

public class EventToMetadataConverterAuthentication implements EventToMetadataConverter {
    /** NOTE: it is decided at the moment not to include events 4776 **/
    private static final String[] successReferenceIds = new String[]{"4769", "4624", "4648","rsaacesrv"};
    private static final String[] failureReferenceIds = new String[]{"4769", "4625", "4648","rsaacesrv"};


    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> convert(Map<String, String> config, List<? extends Event> events) {
        IStringGenerator successReferenceIdGenerator = ReferenceIdGeneratorFactory.create(config, successReferenceIds);
        IStringGenerator failureReferenceIdGenerator = ReferenceIdGeneratorFactory.create(config, failureReferenceIds);
        String referenceId = null;

        List<Map<String, Object>> metadataList = new ArrayList<>(events.size());

        for (AuthenticationEvent event : (List<AuthenticationEvent>)events) {

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("event_time", String.valueOf(event.getDateTime().toEpochMilli()));
            metadata.put("mongo_source_event_time", event.getDateTime());
            metadata.put("user_dst", event.getUser().getUserId());
            metadata.put("result_code", event.getResultCode());

            // reference id does not exist for rhlinux events - device.type = 'rhlinux'
            if (event.getSrcMachineEntity() != null &&
                    event.getSrcMachineEntity().getOsVersion() != null &&
                    event.getSrcMachineEntity().getOsVersion().equalsIgnoreCase("rhlinux")) {
                // used in query, with action
                metadata.put("device_type", "rhlinux");

                metadata.put("sessionid", event.getEventId()); //eventId, e.g. "780521460486"
                metadata.put("action", event.getDataSource());  // dataSource, e.g. "/usr/sbin/sshd"
                metadata.put("user_src", event.getUser().getUserId());         // userId, userName, userDisplayName
                metadata.put("event_type", event.getAuthenticationOperation().getOperationType().getName());   // operationType, filter:cred_acq,user_auth,user_login
                metadata.put("result", event.getResult());         // result, resultCode
                metadata.put("host_src", event.getSrcMachineEntity().getMachineId()); // srcMachineId, srcMachineName
            }
            else {
                if(event.getResult().equalsIgnoreCase("success")){
                    referenceId = successReferenceIdGenerator.getNext();
                } else{
                    referenceId = failureReferenceIdGenerator.getNext();
                }
                // not rhlinux events - referenceId defines the field mapping
                metadata.put("reference_id", referenceId);
                if (referenceId.equalsIgnoreCase("4769")){
                    metadata.put("service_name", "service$");
                }
                if (referenceId.equalsIgnoreCase("4648"))
                    metadata.put("host_dst", event.getDstMachineEntity().getMachineId());
                if (referenceId.equalsIgnoreCase("4624") || referenceId.equalsIgnoreCase("4625"))
                    putLogonType(event, metadata);
                metadata.put("device_type", referenceId.equalsIgnoreCase("rsaacesrv") ? "rsaacesrv" : "winevent_nic");
                if (referenceId.equalsIgnoreCase("rsaacesrv")) metadata.put("sessionid", event.getEventId());
                else metadata.put("event_source_id", event.getEventId());
                if (referenceId.equalsIgnoreCase("rsaacesrv")) metadata.put("ec_outcome", event.getResult());
                else metadata.put("event_type", event.getResult());
                putSrcMachineId(event, metadata);
            }

            metadataList.add(metadata);
        }

        return metadataList;
    }

    private static void putLogonType(AuthenticationEvent event, Map<String, Object> metadata) {
        /** Operations 4625 and 4624 have mandatory logon_type. If it not filled, the event will be filtered by adapter/
        * For scenario events with ...REMOTE_COMPUTER... operation types it will be 10,
        * for all other operation types - 2
         * Set rsaacesrv Logon type to be Logon
         **/
        String operationType = event.getAuthenticationOperation().getOperationType().getName();
        if (operationType.equals("USER_LOGGED_ON_INTERACTIVELY_FROM_A_REMOTE_COMPUTER")) metadata.put("logon_type", "10");
        else if (operationType.equals("USER_FAILED_TO_LOG_ON_INTERACTIVELY_FROM_A_REMOTE_COMPUTER")) metadata.put("logon_type", "10");
        else if (operationType.equals("REMOTE_INTERACTIVE")) metadata.put("logon_type", "10");
        else metadata.put("logon_type", "2");
    }

    private static void putSrcMachineId(AuthenticationEvent event, Map<String, Object> metadata) {
        Object referenceId = metadata.get("reference_id");
        String srcMachineId = event.getSrcMachineEntity().getMachineId();
        if (referenceId.equals("4776")) metadata.put("host_src", srcMachineId); /** not generated, out of scope currently **/
        else if (referenceId.equals("4624")) metadata.put("alias_host", Collections.singletonList(srcMachineId));
        else if (referenceId.equals("4625")) metadata.put("alias_host", Collections.singletonList(srcMachineId));
        else if (referenceId.equals("4648")) metadata.put("alias_host", Collections.singletonList(srcMachineId));
        else if (referenceId.equals("rsaacesrv")) metadata.put("host_src", srcMachineId);
    }
}
