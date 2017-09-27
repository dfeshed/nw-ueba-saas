package presidio.data.domain.event.file;

import java.util.Arrays;

/**
 * Created by presidio on 7/13/17.
 */
public enum FILE_OPERATION_TYPE{

    FOLDER_OPENED ("FOLDER_OPENED"),
    FILE_DELETED ("FILE_DELETED"),
    FILE_OPENED ("FILE_OPENED"),
    FILE_RENAMED ("FILE_RENAMED"),
    FILE_MOVED ("FILE_MOVED"),
    FILE_CREATED ("FILE_CREATED"),
    LOCAL_SHARE_PERMISSIONS_CHANGED ("LOCAL_SHARE_PERMISSIONS_CHANGED"),
    FOLDER_ACCESS_RIGHTS_CHANGED ("FOLDER_ACCESS_RIGHTS_CHANGED"),
    FILE_ACCESS_RIGHTS_CHANGED ("FILE_ACCESS_RIGHTS_CHANGED"),
    FILE_CLASSIFICATION_CHANGED ("FILE_CLASSIFICATION_CHANGED"),
    FOLDER_CLASSIFICATION_CHANGED ("FOLDER_CLASSIFICATION_CHANGED"),
    FILE_CENTRAL_ACCESS_POLICY_CHANGED ("FILE_CENTRAL_ACCESS_POLICY_CHANGED");

    public final String value;
    FILE_OPERATION_TYPE(String value){
            this.value = value;
        }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
