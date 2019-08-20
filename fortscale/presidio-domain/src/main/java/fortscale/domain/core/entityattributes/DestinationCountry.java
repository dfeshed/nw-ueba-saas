package fortscale.domain.core.entityattributes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DestinationCountry extends EntityAttributes {

    @JsonCreator
    public DestinationCountry(@JsonProperty("name") String name) {
        super(name);
    }
}
