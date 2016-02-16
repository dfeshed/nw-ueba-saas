package fortscale.ml.model.Exceptions;


public class InvalidEntityEventConfNameException extends RuntimeException {
    public InvalidEntityEventConfNameException(String entityEventConfName) {
        super(String.format("entityEventConfName: %s configuration does not exist in entity_events.json configuration", entityEventConfName));
    }
}
