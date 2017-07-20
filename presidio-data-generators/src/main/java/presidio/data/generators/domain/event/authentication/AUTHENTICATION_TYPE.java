package presidio.data.generators.domain.event.authentication;

import java.util.Arrays;

/**
 * Created by presidio on 7/13/17.
 */
public enum AUTHENTICATION_TYPE {

    AUTHENTICATION_TYPE_TBD ("AUTHENTICATION_TYPE_TBD");

    public final String value;
    AUTHENTICATION_TYPE(String value){
            this.value = value;
        }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
