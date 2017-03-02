import Ember from 'ember';
import NodeTypes from './node-types';
import LinkTypes from './link-types';
import { makeNodeId, makeNode } from './node';
import { makeLinkId, makeLink } from './link';
import arrayFromHashValues from 'respond/utils/array/from-hash-values';

const { get, isEmpty, set } = Ember;

const NodeTypeKeys = Object.keys(NodeTypes);
const LinkTypeKeys = Object.keys(LinkTypes);

// Looks for a value in a given event attr. If found, ensures it is included in the given hash of nodes as a node
// of a given type.
// Returns the resultant node object, if any; null otherwise.
function parseEventAttrNode(evt, attrName, nodeType, nodeHash) {

  // Does this event have a value for that attr name?
  const nodeValue = get(evt, attrName);
  if (isEmpty(nodeValue)) {
    return null;
  }

  // Do we already have a node for that attr value?
  const nodeKey = makeNodeId(nodeType, nodeValue);
  let node = nodeHash[nodeKey];
  if (!node) {

    // No node exists yet for this attr value. Create a new node for it.
    node = nodeHash[nodeKey] = makeNode(nodeType, nodeValue);
  }

  // Now that we have a node, add this event to that node's event list.
  if (!node.events) {
    set(node, 'events', []);
  }
  node.events.pushObject(evt);
  return node;
}

// Find/create the nodes mentioned in the given event; add any newly created nodes to the given hash.
// Returns a hash of all the nodes found/created, keyed by the event attr name that each node was found under.
function parseEventNodes(evt, nodeHash) {
  const nodesByAttrName = {};

  // For each known node type...
  NodeTypeKeys.forEach((nodeType) => {

    // For each attr name that corresponds to that node type...
    const attrNames = NodeTypes[nodeType];
    attrNames.forEach((attrName) => {

      // Make a node from that attr value.
      const node = parseEventAttrNode(evt, attrName, nodeType, nodeHash);
      if (node) {
        nodesByAttrName[attrName] = node;
      }
    });
  });

  return nodesByAttrName;
}

// Find/create the links mentioned in the given event; add any newly created links to the given hash.
function parseEventLinks(evt, nodesByAttrName, linkHash) {

  // For each known link type...
  LinkTypeKeys.forEach((linkType) => {

    // For each set of attrs that can define that type of link...
    const attrSets = LinkTypes[linkType];
    attrSets.forEach(({ sourceAttrName, targetAttrName, requiredAttrName, prohibitedAttrName }) => {

      // Skip this link if:
      // (a) we are missing a source or target, or
      // (b) the required attr is defined and its value is empty, or
      // (c) if the prohibited attr is defined and its value is non-empty.
      const source = nodesByAttrName[sourceAttrName];
      const target = nodesByAttrName[targetAttrName];
      const matchesProhibited = prohibitedAttrName ? !!nodesByAttrName[prohibitedAttrName] : false;
      const matchesRequired = requiredAttrName ? !!nodesByAttrName[requiredAttrName] : true;
      if (!source || !target || !matchesRequired || matchesProhibited) {
        return;
      }

      // Do we already have a link for this?
      const linkKey = makeLinkId(linkType, source, target);
      let link = linkHash[linkKey];
      if (!link) {

        // No link exists yet for this. Create a new link for it.
        link = linkHash[linkKey] = makeLink(linkType, source, target);
      }

      // Now that we have a link, add this event to that link's event list.
      if (!link.events) {
        set(link, 'events', []);
      }
      link.events.pushObject(evt);
    });

  });
}

// Infer the entities & links mentioned in the given event, and ensure that there are nodes & links
// for them in the given hashes, generating new nodes & links as needed.
function parseEventNodesAndLinks(evt, nodeHash, linkHash) {
  const nodesByAttrName = parseEventNodes(evt, nodeHash);
  parseEventLinks(evt, nodesByAttrName, linkHash);
}

/**
 * Generates a hash of node objects & a hash of link objects from a given array of normalized event objects.
 * The node & link objects are intended to be compatible with a d3 force-layout.
 *
 * Generally speaking, an event's properties can mention 1 or more entities, where an "entity" is defined as known
 * identifier, such as a user, host, domain, ip address, file name or file hash.  By inspecting known properties of
 * a given event object, we can find the entities for that event, and generate a node object for each found entity.
 *
 * We can also infer a few basic "links" between entities that are found in the same event. For example:
 * (i) if a user & a host are found in the same event, we infer that the user "uses" the host -- that's a link of
 * type = "uses" with source entity = the user and target entity = the host;
 * (ii) if a host & a source ip are found in the same event, we infer that the host "communicates as" that ip;
 * (iii) if an event mentions a source ip and a destination ip, then we infer that the source ip  "communicates with"
 * the destination ip;
 * (iv) if a domain & a destination ip are found in the same event, we infer that the domain "communicates as" that ip.
 *
 * Each event object is expected to be normalized, meaning it is not a raw event from NetWitness, but rather
 * an easy-to-consume UI object, with attrs like `time`, `user`, `host`, `domain`, `sourceIp`, `destinationIp` & `file`.
 * For more details about this data structure, see the `normalizedEvents` attr of the Indicator class in
 * `respond/utils/indicator/indicator.js`.
 *
 * Each node generated by this function will be a POJO with the following attrs:
 * `type`: {string} either 'user', 'host', 'domain', 'ip' or 'file';
 * `value`: {string} either a user name, host name, domain name, ip address, or file hash;
 * `id`: {string} unique identifier for the `type` + `value` pair, e.g., `${type}::${value}`;
 * `events`: {object[]} the subset of the given events in which this node was found.
 *
 * Each link generated by this function will be a POJO with the following attrs:
 * `type`: {string} either 'uses', 'communicates as', 'communicates with', 'sends' or 'to';
 * `source`: {object} one of the generated nodes;
 * `target`: {object} one of the generated nodes;
 * `id`: {string} unique identifier for the link, e.g., `${type}||${source.id}||${pair.id}`;
 * `events`: {object[]} the subset of the given events in which this link was found.
 *
 * @param {object[]} events Array of normalized event objects, possibly empty.
 * @param {number} [defaultNodeRadius] Optional node radius to be applied to new (radius-less) nodes.
 * @public
 */
export default function(events = [], defaultNodeRadius) {
  const nodeHash = {};
  const linkHash = {};

  events.forEach((evt) => {
    parseEventNodesAndLinks(evt, nodeHash, linkHash);
  });

  const arrs = {
    nodes: arrayFromHashValues(nodeHash),
    links: arrayFromHashValues(linkHash)
  };

  if (defaultNodeRadius) {
    // Ensure all the given nodes (if any) have a radius.
    // Doing this here, rather than later, ensures our data doesn't get accessed/rendering initially without a radius.
    arrs.nodes.forEach(function(node) {
      if (!get(node, 'r')) {
        set(node, 'r', defaultNodeRadius);
      }
    });
  }

  return arrs;
}