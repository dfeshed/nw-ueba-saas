package presidio.data.generators.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by YaronDL on 7/9/2017.
 */
public class StringUtils {

    /**
     * Create an array of strings in format <prefix>_<abc>_<suffix>
     * @param prefix       - constant prefix for generated strings
     * @param suffix       - constant suffix for generated strings
     * @param numberOfValues    - amount of strings need to generate
     * @return
     */
    public static String[] buildUniqueAlphabetStrings(String prefix, String suffix, int numberOfValues) {
        Random random = new Random(0);

        Set<String> uniqueStrings = new HashSet<>();
        String[] ret = new String[numberOfValues];

        String uniqueString = "";
        while(uniqueStrings.size()<numberOfValues){
            int charVal = 97+random.nextInt(26);
            uniqueString += (char) charVal;
            if(!uniqueStrings.contains(uniqueString)){
                ret[uniqueStrings.size()] = prefix + "_" + uniqueString + "_" + suffix;
                uniqueStrings.add(uniqueString);
                uniqueString = "";
            }
        }
        return ret;
    }

    public static String getFriendlyName(String name) {
        return name.toLowerCase().replaceAll("_", " ");
    }

}
