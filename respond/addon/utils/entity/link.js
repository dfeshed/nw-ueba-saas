import LinkTypes from './link-types';

// Delimiter used in link ids between link type, link source & link target.
const LINK_ID_DELIM = '||';

function getLinkDistance(type) {
  if (!type) {
    return 350;
  }
  switch (type) {
    case LinkTypes.BELONGS_TO:
      return 125;
    case LinkTypes.IS_NAMED:
      return 125;
    case LinkTypes.AS:
      return 125;
    case LinkTypes.HAS_FILE:
      return 235;
    case LinkTypes.COMMUNICATES_WITH:
      return 350;
    case LinkTypes.LAUNCHES:
      return 350;
    case LinkTypes.USES:
      return 350;
  }
}

function getLinkStrength(type) {
  switch (type) {
    case LinkTypes.BELONGS_TO:
      return 1;
    case LinkTypes.IS_NAMED:
      return 1;
    case LinkTypes.AS:
      return 1;
    case LinkTypes.HAS_FILE:
      return 1;
    case LinkTypes.COMMUNICATES_WITH:
      return 0.2;
    case LinkTypes.LAUNCHES:
      return 0.1;
    case LinkTypes.USES:
      return 0.5;
    default:
      return 1;
  }
}

/**
 * Generates a unique identifier for a given link type + source node + target node.
 *
 * Uses the following structure for link ids: '<type><delim><source.id><delim><target.id>'. This works well enough,
 * as long as we can safely assume that <type>, <source.id> & <target.id> have no <delim> characters in them.
 *
 * @param {string} type The link type.
 * @param {{ id: string }} source The link's source node, which is assumed to have a string `id`.
 * @param {{ id: string }} target The link's target node, which is assumed to have a string `id`.
 * @returns {string}
 * @public
 */

function makeLinkId(type, source, target) {
  return [type, source.id, target.id].join(LINK_ID_DELIM);
}

/**
 * Parses a given link id into the link type, source id & target id.  The inverse of `makeLinkId()`.
 *
 * Assumes link id has the following structure: '<type><delim><source_id><delim><target_id>', where <type>, <source_id>
 * and <target_id> have no <delim> characters in them.
 *
 * @param {string} id
 * @returns {{ type: string, sourceId: string, targetId: string }}
 * @public
 */
function parseLinkId(id = '') {
  const [ type, sourceId, targetId ] = id.split(LINK_ID_DELIM);
  return {
    type,
    sourceId,
    targetId
  };
}

/**
 * Generates a link for a given link type + source node + target node.
 * Uses the given type as the link text.
 *
 * @param {string} type The link type.
 * @param {object} source The source node.
 * @param {object} target The target node.
 * @returns {{ id: string, type: string, text: string, source: object, target: object }}
 * @public
 */
function makeLink(type, source, target) {
  return {
    id: makeLinkId(type, source, target),
    type,
    text: type,
    source,
    target,
    linkDistance: getLinkDistance(type),
    linkStrength: getLinkStrength(type),
    events: []
  };
}

export {
  makeLinkId,
  parseLinkId,
  makeLink
};