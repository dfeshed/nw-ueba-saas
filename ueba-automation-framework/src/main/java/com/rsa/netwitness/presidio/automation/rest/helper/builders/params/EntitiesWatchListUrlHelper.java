package com.rsa.netwitness.presidio.automation.rest.helper.builders.params;

public class EntitiesWatchListUrlHelper {

    private final String URL;

    public EntitiesWatchListUrlHelper(String url) {
        this.URL = url;
    }


    public PresidioUrl entityAdd(String entityId) {
        return new EntitiesWatchListUrlBuilder.Builder(URL)
                .appendID(entityId)
                .setJsonPatchBodyNode("add")
                .build();
    }

    public PresidioUrl entityRemove(String entityId) {
        return new EntitiesWatchListUrlBuilder.Builder(URL)
                .appendID(entityId)
                .setJsonPatchBodyNode("remove")
                .build();
    }

    public PresidioUrl bulkAdd(String entityType, String severity) {
        return new EntitiesWatchListUrlBuilder.Builder(URL)
                .setJsonPatchBodyNode("add")
                .setEntityQueryBodyNode(entityType, severity, false)
                .build();
    }

    public PresidioUrl bulkRemove(String entityType, String severity) {
        return new EntitiesWatchListUrlBuilder.Builder(URL)
                .setJsonPatchBodyNode("remove")
                .setEntityQueryBodyNode(entityType, severity,true)
                .build();
    }

}
