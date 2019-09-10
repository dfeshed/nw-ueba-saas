package fortscale.utils.json;

import fortscale.utils.hierarchy.HierarchyLeafFinder;
import fortscale.utils.hierarchy.HierarchyValidatingLeaf;
import org.apache.commons.lang3.ClassUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
public class JacksonUtils extends HierarchyLeafFinder<JSONObject> {

    private static final String PRIMITIVE_FIELD_NAME = "$$primitive$$";
    public static List<String> jsonArrayToList(JSONArray jsonArray) {
        ArrayList<String> toReturn = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                toReturn.add(jsonArray.getString(i));
            }
        }
        return toReturn;
    }

    public Object getFieldValue(JSONObject document, String fieldName, Object defaultValue) {
        Object toReturn = defaultValue;
        HierarchyValidatingLeaf<JSONObject> hierarchyValidatingLeaf = getLeaf(document, fieldName);
        if (!hierarchyValidatingLeaf.isHierarchyBroken()) {
            if (hierarchyValidatingLeaf.getValue() != null && hierarchyValidatingLeaf.getValue().has(PRIMITIVE_FIELD_NAME)) {
                toReturn = hierarchyValidatingLeaf.getValue().get(PRIMITIVE_FIELD_NAME);
            } else {
                toReturn = hierarchyValidatingLeaf.getValue();
            }
        }
        return toReturn;
    }

    /**
     * Sets the given object's field to the given value. This method works for nested objects also. If one would like to
     * change a nested object's field, the nested object delimiter should be found between objects in the field name.
     *
     * For example:
     * ============
     * setFieldValue(object = {
     *     "name": "Aaron",
     *     "cat": {
     *         "toy": "mouse"
     *     }
     * }, fieldName = "cat.toy", fieldValue = "rabbit")
     * Becomes:
     * ========
     * {
     *     "name": "Aaron",
     *     "cat": {
     *         "toy": "rabbit"
     *     }
     * }
     *
     * @param document     the given JSONOBJECT
     * @param fieldName  the field name belonging to the object which should be set to the given value
     * @param fieldValue the value to set the object's field to
     */
    public void setFieldValue(JSONObject document, String fieldName, Object fieldValue) {
        HierarchyValidatingLeaf<JSONObject> hierarchyValidatingLeaf = getLeaf(document, fieldName);
        hierarchyValidatingLeaf.validateHierarchyNotBroken();
        hierarchyValidatingLeaf.getParent().put(hierarchyValidatingLeaf.getFieldName(), fieldValue);
    }

    @Override
    protected boolean isNull(JSONObject object) {
        return object == null || object == JSONObject.NULL;
    }

    @Override
    protected JSONObject getChild(JSONObject parent, String subFieldName) throws JSONException {
        Object nestedObject = parent.get(subFieldName);
        if (nestedObject == JSONObject.NULL) return null;
        if (ClassUtils.isPrimitiveOrWrapper(nestedObject.getClass()) || nestedObject instanceof String) {
            return new JSONObject(format("{\"%s\": \"%s\"}", PRIMITIVE_FIELD_NAME, nestedObject));
        }
        return parent.getJSONObject(subFieldName);
    }
}
