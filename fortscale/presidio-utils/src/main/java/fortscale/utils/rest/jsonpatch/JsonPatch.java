package fortscale.utils.rest.jsonpatch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.JsonPatchMessages;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.List;

/**
 * Implementation of JSON Patch
 * <p>
 * <p><a href="http://tools.ietf.org/html/draft-ietf-appsawg-json-patch-10">JSON
 * Patch</a>, as its name implies, is an IETF draft describing a mechanism to
 * apply a patch to any JSON value. This implementation covers all operations
 * according to the specification; however, there are some subtle differences
 * with regards to some operations which are covered in these operations'
 * respective documentation.</p>
 * <p>
 * <p>An example of a JSON Patch is as follows:</p>
 * <p>
 * <pre>
 *     [
 *         {
 *             "op": "add",
 *             "path": "/-",
 *             "value": {
 *                 "productId": 19,
 *                 "name": "Duvel",
 *                 "type": "beer"
 *             }
 *         }
 *     ]
 * </pre>
 * <p>
 * <p>This patch contains a single operation which adds an item at the end of
 * an array. A JSON Patch can contain more than one operation; in this case, all
 * operations are applied to the input JSON value in their order of appearance,
 * until all operations are applied or an error condition is encountered.</p>
 * <p>
 * <p>The main point where this implementation differs from the specification
 * is initial JSON parsing. The draft says:</p>
 * <p>
 * <pre>
 *     Operation objects MUST have exactly one "op" member
 * </pre>
 * <p>
 * <p>and:</p>
 * <p>
 * <pre>
 *     Additionally, operation objects MUST have exactly one "path" member.
 * </pre>
 * <p>
 * <p>However, obeying these to the letter forces constraints on the JSON
 * <b>parser</b>. Here, these constraints are not enforced, which means:</p>
 * <p>
 * <pre>
 *     [ { "op": "add", "op": "remove", "path": "/x" } ]
 * </pre>
 * <p>
 * <p>is parsed (as a {@code remove} operation, since it appears last).</p>
 * <p>
 * <p><b>IMPORTANT NOTE:</b> the JSON Patch is supposed to be VALID when the
 * constructor for this class ({@link JsonPatch#fromJson(JsonNode)} is used.</p>
 */
public final class JsonPatch
        implements JsonSerializable {
    private static final MessageBundle BUNDLE
            = MessageBundles.getBundle(JsonPatchMessages.class);

    /**
     * List of operations
     */
    private final List<JsonPatchOperation> operations;

    /**
     * Constructor
     * <p>
     * <p>Normally, you should never have to use it.</p>
     *
     * @param operations the list of operations for this patch
     * @see JsonPatchOperation
     */
    @JsonCreator
    public JsonPatch(final List<JsonPatchOperation> operations) {
        this.operations = ImmutableList.copyOf(operations);
    }

    /**
     * Static factory method to build a JSON Patch out of a JSON representation
     *
     * @param node the JSON representation of the generated JSON Patch
     * @return a JSON Patch
     * @throws IOException          input is not a valid JSON patch
     * @throws NullPointerException input is null
     */
    public static JsonPatch fromJson(final JsonNode node)
            throws IOException {
        BUNDLE.checkNotNull(node, "jsonPatch.nullInput");
        return JacksonUtils.getReader().withType(JsonPatch.class)
                .readValue(node);
    }

    /**
     * Apply this patch to a JSON value
     *
     * @param node the value to apply the patch to
     * @return the patched JSON value
     * @throws JsonPatchException   failed to apply patch
     * @throws NullPointerException input is null
     */
    public JsonNode apply(final JsonNode node)
            throws JsonPatchException {
        BUNDLE.checkNotNull(node, "jsonPatch.nullInput");
        JsonNode ret = node;
        for (final JsonPatchOperation operation : operations)
            ret = operation.apply(ret);

        return ret;
    }

    public List<JsonPatchOperation> getOperations() {
        return operations;
    }

    @Override
    public String toString() {
        return operations.toString();
    }

    @Override
    public void serialize(final JsonGenerator jgen,
                          final SerializerProvider provider)
            throws IOException {
        jgen.writeStartArray();
        for (final JsonPatchOperation op : operations)
            op.serialize(jgen, provider);
        jgen.writeEndArray();
    }

    @Override
    public void serializeWithType(final JsonGenerator jgen,
                                  final SerializerProvider provider, final TypeSerializer typeSer)
            throws IOException {
        serialize(jgen, provider);
    }
}

