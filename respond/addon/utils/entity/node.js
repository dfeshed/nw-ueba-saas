// Delimiter used in node ids between node type & node value.
const NODE_ID_DELIM = '::';

/**
 * Generates a node id for a given node type & node value.
 *
 * Uses the following structure for node ids: '<type><delim><value>'. This works well enough, as long as we can safely
 * assume that <type> has no <delim> characters in it.
 *
 * @param {string} type
 * @param {string} value
 * @returns {string}
 * @public
 */
function makeNodeId(type, value) {
  return [type, value].join(NODE_ID_DELIM);
}

/**
 * Parses a given node id into a node type & node value. The inverse of `makeNodeId()`.
 *
 * Assumes node id's following the structure: '<type><delim><value>', where <type> has no <delim> characters in it.
 *
 * @param {string} id
 * @returns {{ type: string, value: string }}
 * @public
 */
function parseNodeId(id = '') {
  let type = '';
  let value = '';
  const idx = id.indexOf(NODE_ID_DELIM);
  if (idx > -1) {
    type = id.substr(0, idx);
    value = id.substr(idx + 1);
  }
  return {
    type,
    value
  };
}

/**
 * Generates a node object for a given node type + id.
 * Uses the given value as the node text.
 *
 * @returns {{ id: string, type: string, value: string, text: string }}
 * @public
 */
function makeNode(type, value) {
  return {
    id: makeNodeId(type, value),
    type,
    value,
    text: value,
    events: []
  };
}

export {
  makeNodeId,
  parseNodeId,
  makeNode
};