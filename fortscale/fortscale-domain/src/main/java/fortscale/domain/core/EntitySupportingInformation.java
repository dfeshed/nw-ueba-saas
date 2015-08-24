package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;

/**
 * Created by galiar on 20/08/2015.
 */
@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.WRAPPER_OBJECT,property="type")
@JsonSubTypes({@JsonSubTypes.Type(value = fortscale.domain.core.UserSupprotingInformation.class,name="userSupprotingInformation")})
public class EntitySupportingInformation {

    public EntitySupportingInformation() {
    }

    public  EntitySupportingInformation getSupportingInformation(){
        EntitySupportingInformation dummyVAR = null;
        return dummyVAR;
    }

}
