package presidio.data.generators.event;

/**
 * Created by presidio on 7/13/17.
 */
public enum OPERATION_RESULT {

    SUCCESS ("Success"),
    FAILURE ("Failure");

    public final String value;
    OPERATION_RESULT(String value){
        this.value = value;
    }

    }
