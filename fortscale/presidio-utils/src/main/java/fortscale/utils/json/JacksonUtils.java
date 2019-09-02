package fortscale.utils.json;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class JacksonUtils {
    public static List<String> jsonArrayToList(JSONArray jsonArray) {
        ArrayList<String> toReturn = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                toReturn.add(jsonArray.getString(i));
            }
        }
        return toReturn;
    }
}
