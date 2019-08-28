package fortscale.utils.reflection;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

// Keep the class package-private.
final class Leaf {
    private final Object parent;
    private final Field field;
    private final Object value;
    private final String brokenHierarchyMessage;

    public Leaf(@NotNull Object parent, @NotNull Field field, Object value) {
        this.parent = parent;
        this.field = field;
        this.value = value;
        this.brokenHierarchyMessage = null;
    }

    public Leaf(@NotNull String brokenHierarchyMessage) {
        this.parent = null;
        this.field = null;
        this.value = null;
        this.brokenHierarchyMessage = brokenHierarchyMessage;
    }

    public Object getParent() {
        return parent;
    }

    public Field getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public boolean isHierarchyBroken() {
        return brokenHierarchyMessage != null;
    }

    public void assertHierarchyNotBroken() {
        if (isHierarchyBroken()) {
            throw new NullPointerException(brokenHierarchyMessage);
        }
    }
}
