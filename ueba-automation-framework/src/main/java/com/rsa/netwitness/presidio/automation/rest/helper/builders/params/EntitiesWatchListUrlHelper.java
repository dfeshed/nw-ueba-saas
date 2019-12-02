package com.rsa.netwitness.presidio.automation.rest.helper.builders.params;

public class EntitiesWatchListUrlHelper {

    private final String URL;

    public EntitiesWatchListUrlHelper(String url) {
        this.URL = url;
    }


    public PresidioUrl singleEntityAdd(String entityId) {
        return new EntitiesWatchListUrlBuilder.Builder(URL)
                .appendID(entityId)
                .setJsonPatchBodyNode("add")
                .build();
    }

    public PresidioUrl singleEntityRemove(String entityId) {
        return new EntitiesWatchListUrlBuilder.Builder(URL)
                .appendID(entityId)
                .setJsonPatchBodyNode("remove")
                .build();
    }

    public PresidioUrl multipleEntitiesAdd(String entityType, String severity) {
        return new EntitiesWatchListUrlBuilder.Builder(URL)
                .setJsonPatchBodyNode("add")
                .setEntityQueryBodyNode(entityType, severity, "")
                .build();
    }

    public PresidioUrl multipleEntitiesRemove(String entityType, String severity) {
        return new EntitiesWatchListUrlBuilder.Builder(URL)
                .setJsonPatchBodyNode("remove")
                .setEntityQueryBodyNode(entityType, severity,"\"watched\"")
                .build();
    }

}
