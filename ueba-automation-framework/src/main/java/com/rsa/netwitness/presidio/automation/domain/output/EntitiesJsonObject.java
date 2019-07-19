package com.rsa.netwitness.presidio.automation.domain.output;

import com.google.gson.annotations.Expose;

import java.util.List;

public class EntitiesJsonObject {
    @Expose
    private List<EntitiesStoredRecord> entities;

    public List<EntitiesStoredRecord> getEntities() {
        return entities;
    }

    public void setEntities(List<EntitiesStoredRecord> entities) {
        this.entities = entities;
    }

    @Override
    public String toString() {
        return "EntitiesJsonObject{" +
                "alerts=" + entities +
                '}';
    }
}
