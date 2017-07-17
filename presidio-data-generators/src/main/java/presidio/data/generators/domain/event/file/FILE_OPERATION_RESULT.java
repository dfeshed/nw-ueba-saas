package presidio.data.generators.domain.event.file;

/**
 * Created by presidio on 7/13/17.
 */
public enum FILE_OPERATION_RESULT {

    SUCCESS ("SUCCESS"),
    FAILURE ("FAILURE");

    public final String value;
    FILE_OPERATION_RESULT (String value){
        this.value = value;
    }

    }
