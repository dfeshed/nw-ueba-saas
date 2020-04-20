package fortscale.utils.rest.jsonpatch.operation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonpatch.JsonPatchException;
import com.google.common.collect.Iterables;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON Path {@code remove} operation
 * <p>
 * <p>This operation only takes one pointer ({@code path}) as an argument. It
 * is an error condition if no JSON value exists at that pointer.</p>
 * <p>If the remove action is applied on a list - the relevant value will be removed
 * from the list</p>
 */
public final class RemoveOperation
        extends PathValueOperation {

    @JsonCreator
    public RemoveOperation(@JsonProperty("path") final JsonPointer path,
                           @JsonProperty("value") final JsonNode value) {
        super("remove", path, value);
    }

    @Override
    public JsonNode apply(final JsonNode node)
            throws JsonPatchException {
        if (path.isEmpty()) {
            return MissingNode.getInstance();
        }

        final JsonNode ret = node.deepCopy();
        final JsonNode parentNode = path.parent().get(ret);

        if (path.path(node).isMissingNode() && !parentNode.isArray()) {
            throw new JsonPatchException(BUNDLE.getMessage(
                    "jsonPatch.noSuchPath"));
        }

        final String raw = Iterables.getLast(path).getToken().getRaw();
        // Remove item from array
        if (parentNode.isArray()) {
            List<JsonNode> list = IteratorUtils.toList(parentNode.elements());
            List<Integer> indexToRemove = new ArrayList<>();

            // Get all the indexes to remove
            list.forEach(jsonNode -> {
                if (jsonNode.asText().equals(value.asText())) {
                    indexToRemove.add(list.indexOf(jsonNode));
                }
            });

            // Remove the values
            if (CollectionUtils.isNotEmpty(indexToRemove)) {
                indexToRemove.forEach(index -> {
                    ((ArrayNode) parentNode).remove(index);
                });
            }
        } else if (parentNode.isObject())
            ((ObjectNode) parentNode).remove(raw);
        else
            ((ArrayNode) parentNode).remove(Integer.parseInt(raw));

        return ret;
    }

}
