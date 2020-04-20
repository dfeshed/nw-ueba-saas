package fortscale.domain.core.entityattributes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SslSubject extends EntityAttributes {

    public SslSubject(@JsonProperty("name") String name) {
        super(name);
    }
}
