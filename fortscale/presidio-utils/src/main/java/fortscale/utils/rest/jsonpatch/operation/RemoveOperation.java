/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

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

        if (path.path(node).isMissingNode() && parentNode.isArray()) {
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
