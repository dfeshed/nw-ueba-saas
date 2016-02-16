package fortscale.ml.model;

/**
 * Created by baraks on 2/11/2016.
 */
public class InvalidEntityEventConfNameException extends RuntimeException {
    public InvalidEntityEventConfNameException(String entityEventConfName) {
        super(String.format("entityEventConfName: %s configuration does not exist in entity_events.json configuration", entityEventConfName));
    }
}
