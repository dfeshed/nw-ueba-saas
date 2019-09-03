package fortscale.utils.hierarchy;

import javax.validation.constraints.NotNull;

import static org.apache.commons.lang3.Validate.notNull;

public abstract class HierarchyLeafFinder <T> {

    protected static final String NULL_FIELD_NAME_EXCEPTION_MESSAGE =
            "'fieldName' cannot be null.";

    protected abstract boolean isNull(T object);
    protected abstract T getChild(T parent, String subFieldName) throws Exception;

    public T getFieldValue(T object, String fieldName) {
        HierarchyValidatingLeaf<T> hierarchyValidatingLeaf = getLeaf(object, fieldName);
        hierarchyValidatingLeaf.validateHierarchyNotBroken();
        return hierarchyValidatingLeaf.getValue();
    }

    protected @NotNull HierarchyValidatingLeaf <T> getLeaf(T value, @NotNull String fieldName) {
        notNull(fieldName, NULL_FIELD_NAME_EXCEPTION_MESSAGE);
        T parent = value;
        String lastFieldName = fieldName;
        try {
            for (String subfieldName : new HierarchyIterator(fieldName)) {
                if (value == null) {
                    return new HierarchyValidatingLeaf<>(fieldName, subfieldName);
                }

                parent = value;
                value = getChild(parent, subfieldName);
                lastFieldName = subfieldName;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new HierarchyValidatingLeaf<>(value, parent, lastFieldName);
    }
}
