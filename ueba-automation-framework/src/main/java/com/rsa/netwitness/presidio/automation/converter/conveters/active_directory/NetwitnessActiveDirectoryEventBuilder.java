package com.rsa.netwitness.presidio.automation.converter.conveters.active_directory;

import com.rsa.netwitness.presidio.automation.converter.events.CefHeader;
import com.rsa.netwitness.presidio.automation.converter.events.WindowsEvent;
import fortscale.common.general.Schema;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;


class NetwitnessActiveDirectoryEventBuilder extends WindowsEvent {

    private final ActiveDirectoryEvent event;


    NetwitnessActiveDirectoryEventBuilder(ActiveDirectoryEvent event) {
        super(event.getDateTime(), Schema.ACTIVE_DIRECTORY);
        this.event = event;
    }

    NetwitnessActiveDirectoryEventBuilder getByRefId(String refId) {
        cefHeader = getCefHeader();
        setCommonFields();
        mapObjectId(refId);
        mapSecondaryObjectId(refId);
        reference_id = refId;
        return this;
    }

    private void setCommonFields() {
        event_source_id = event.getEventId();
        user_dst = event.getUser().getUserId();
        device_type = "winevent_snare";
        event_type = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS") ? "SUCCESS" : "FAILURE";
    }

    private void mapObjectId(String referenceId) {
        String objectId = event.getObjectName();

        switch (referenceId) {
            case "4741":
            case "4742":
            case "4733":
            case "4740":
            case "4743":
            case "4728":
            case "4756":
            case "4757":
            case "4720":
            case "4722":
            case "4723":
            case "4724":
            case "4725":
            case "4726":
            case "4738":
            case "4767":
            case "4717":
            case "4729":
            case "4732":
                user_src = objectId;
                break;
            case "4734":
            case "4764":
            case "4727":
            case "4754":
            case "4758":
            case "4730":
            case "4731":
                group = objectId;
                break;
            case "5136":
            case "4670":
                obj_name = objectId;
                break;
        }
    }

    private void mapSecondaryObjectId(String referenceId) {
        String objectId2 = "temporary";

        switch (referenceId) {
            case "4733":
            case "4728":
            case "4756":
            case "4757":
            case "4729":
            case "4732":
                group = objectId2;
                break;
            case "4717":
                accesses = objectId2;
                break;
        }
    }


    private CefHeader getCefHeader() {
        String cefVendor = "Microsoft";
        String cefProduct = "Windows Snare";
        String cefEventDesc = "Active directory event test";

        String cefEventType = event.getOperation().getOperationResult()
                .equalsIgnoreCase("SUCCESS") ? "SUCCESS" : "FAILURE";

        return new CefHeader(cefVendor, cefProduct, cefEventType, cefEventDesc);
    }
}
