package fortscale.utils.hierarchy;


import static java.lang.String.format;

public class HierarchyValidatingLeaf<T> {

    private static final String BROKEN_HIERARCHY_MESSAGE_FORMAT =
            "Cannot get the leaf of field '%s' because the instance of the class declaring subfield '%s' is null.";
    private final T value;
    private final T parent;

    private final String fieldName;

    private final String brokenHierarchyMessage;

    public HierarchyValidatingLeaf(T value, T parent, String fieldName) {
        this.value = value;
        this.parent = parent;
        this.fieldName = fieldName;
        this.brokenHierarchyMessage = null;
    }

    public HierarchyValidatingLeaf(String fieldName, String subFieldName) {
        this.value = null;
        this.parent = null;
        this.fieldName = fieldName;
        this.brokenHierarchyMessage = format(BROKEN_HIERARCHY_MESSAGE_FORMAT, fieldName, subFieldName);
    }

    public T getValue() {
        return value;
    }

    public T getParent() {
        return parent;
    }
    public String getFieldName() {
        return fieldName;
    }

    public boolean isHierarchyBroken() {
        return brokenHierarchyMessage != null;
    }

    public void validateHierarchyNotBroken() {
        if (isHierarchyBroken()) {
            throw new NullPointerException(brokenHierarchyMessage);
        }
    }
}
