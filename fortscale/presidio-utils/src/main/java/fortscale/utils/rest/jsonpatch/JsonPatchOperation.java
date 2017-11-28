package fortscale.utils.rest.jsonpatch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.JsonPatchMessages;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import fortscale.utils.rest.jsonpatch.operation.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "op")

@JsonSubTypes({
        @JsonSubTypes.Type(name = "ADD", value = AddOperation.class),
        @JsonSubTypes.Type(name = "copy", value = CopyOperation.class),
        @JsonSubTypes.Type(name = "move", value = MoveOperation.class),
        @JsonSubTypes.Type(name = "REMOVE", value = RemoveOperation.class),
        @JsonSubTypes.Type(name = "replace", value = ReplaceOperation.class)
})

/**
 * Base abstract class for one patch operation
 *
 * <p>Two more abstract classes extend this one according to the arguments of
 * the operation:</p>
 *
 * <ul>
 *     <li>{@link DualPathOperation} for operations taking a second pointer as
 *     an argument ({@code copy} and {@code move});</li>
 *     <li>{@link PathValueOperation} for operations taking a value as an
 *     argument ({@code add}, {@code replace} and {@code test}).</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class JsonPatchOperation
        implements JsonSerializable {
    protected static final MessageBundle BUNDLE
            = MessageBundles.getBundle(JsonPatchMessages.class);

    protected final String op;

    /*
     * Note: no need for a custom deserializer, Jackson will try and find a
     * constructor with a single string argument and use it.
     *
     * However, we need to serialize using .toString().
     */
    protected final JsonPointer path;

    /**
     * Constructor
     *
     * @param op   the operation name
     * @param path the JSON Pointer for this operation
     */
    protected JsonPatchOperation(final String op, final JsonPointer path) {
        this.op = op;
        this.path = path;
    }

    /**
     * Apply this operation to a JSON value
     *
     * @param node the value to patch
     * @return the patched value
     * @throws JsonPatchException operation failed to apply to this value
     */
    public abstract JsonNode apply(final JsonNode node)
            throws JsonPatchException;

    @Override
    public abstract String toString();

    public String getOp() {
        return op;
    }

    public JsonPointer getPath() {
        return path;
    }
}
