/**
 * Lookup of known link types and the event attrs that they correspond to.
 *
 * Each hash key in this lookup is a link type (e.g., 'uses', 'communicates with', etc).
 * Each hash value specifies the relevant event attrs for that link type:
 *
 * `sourceAttrName`: (required) the event attr which specifies the link's source value; if an event does not define a value
 * for this attr, then the event does not represent this type of link;
 *
 * `targetAttrName`: (required) the event attr which specifies the link's target value; if an event does not define a value
 * for this attr, then the event does not represent this type of link;
 *
 * `requiredAttrName`: (optional) an additional event attr which must be defined in order for this type of link
 * to be present; the event's value under that attr is irrelevant, it just needs to be truthy; if `requiredAttrName`
 * is not specified, then the only required event attrs are the ones listed under `sourceAttrName` & `targetAttrName`;
 *
 * `prohibitedAttrName`: (optional) an additional event attr which must NOT be defined in order for this type of link
 * to be present; the event's value under that attr is irrelevant, it just needs to be falsey.
 *
 * @type {object}
 * @public
 */
export default {
  uses: [{
    sourceAttrName: 'user',
    targetAttrName: 'host'
  }],
  as: [{
    sourceAttrName: 'host',
    targetAttrName: 'sourceIp'
  }, {
    sourceAttrName: 'domain',
    targetAttrName: 'destinationIp'
  }],
  'communicates with': [{
    sourceAttrName: 'sourceIp',
    targetAttrName: 'destinationIp',
    prohibitedAttrName: 'file'
  }],
  sends: [{
    sourceAttrName: 'sourceIp',
    targetAttrName: 'file',
    requiredAttrName: 'destinationIp'
  }],
  'to': [{
    sourceAttrName: 'file',
    targetAttrName: 'destinationIp',
    requiredAttrName: 'sourceIp'
  }],
  has: [{
    sourceAttrName: 'sourceIp',
    targetAttrName: 'file',
    prohibitedAttrName: 'destinationIp'
  }]
};