/**
 * Lookup of known node types and the event attrs they correspond to.
 *
 * Each hash key is a known type of node, i.e. an entity that can be mentioned in a normalized event object.
 * For more details about the normalized event data structure, see the `normalizedEvents` attr of the Indicator class in
 * `respond/utils/indicator/indicator.js`.
 *
 * Each hash value is an array of attr names. These are the attrs of the normalized event object where that type of
 * entity may appear.  Some entity types could appear in multiple attrs (e.g., "ip" entities can appear in either
 * the source or the destination IP properties of an event).
 *
 * @type {object}
 * @public
 */
export default {
  user: [ 'user' ],
  host: [ 'host' ],
  domain: [ 'domain' ],
  file: [ 'file' ],
  ip: [ 'sourceIp', 'destinationIp' ]
};
