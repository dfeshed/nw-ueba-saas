package org.apache.flume.interceptor.presidio.transform;

import org.apache.commons.lang3.Validate;


public abstract class AbstractJsonObjectTransformer implements IJsonObjectTransformer {

    private String name;

    public AbstractJsonObjectTransformer(String name){
        this.name = Validate.notBlank(name, "name cannot be blank, empty or null.");
    }

    @Override
    public String getName(){
        return name;
    }
}
