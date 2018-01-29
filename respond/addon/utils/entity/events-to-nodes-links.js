import NodeTypes from './node-types';
import LinkTypes from './link-types';
import HashWithLimit from './hash-with-limit';
import { makeNodeId, makeNode } from './node';
import { makeLinkId, makeLink } from './link';
import arrayFromHashValues from 'respond/utils/array/from-hash-values';
import { isEmpty } from 'ember-utils';
import { isEmberArray } from 'ember-array/utils';
import set from 'ember-metal/set';

// Maps properties of a device POJO (from a normalized alert's source, destination or detector) to node types.
const DEVICE_PROPS_TO_NODE_TYPES = {
  dns_domain: NodeTypes.DOMAIN,
  dns_hostname: NodeTypes.HOST,
  ip_address: NodeTypes.IP,
  mac_address: NodeTypes.MAC
};

// Private cache of the last results.
// Optimization: Use caching to avoid unnecessary re-parsing of events when eventsToNodesAndLinks is called
// repeatedly with a stream of growing events.  By caching the results, we can avoid re-parsing the events that
// were already present in the past invocation, and instead only parse the newly-arrived events.
let cache = {
  events: null,
  output: null,
  nodeHash: null,
  linkHash: null
};

// Returns true if a given array matches the starting contents of another given array.
function isStartOfArray(arr1, arr2) {
  if (isEmberArray(arr1) && isEmberArray(arr2)) {
    if (arr1.length <= arr2.length) {
      const didNotMatch = arr1.find(function(item, index) {
        return item !== arr2[index];
      });
      return !didNotMatch;
    }
  }
  return false;
}

// Ensures a given node type & value is included in the given hash of nodes.
// If the node is not found already in the hash, a new node is created and added to the hash.
// Additionally, the given event POJO is added to the node's events array.
// Returns the corresponding node (if any).
function checkNode(nodeType, nodeValue, nodeHash, evt) {
  if (isEmpty(nodeValue)) {
    return null;
  }

  // Do we already have a node for these inputs?
  const nodeKey = makeNodeId(nodeType, nodeValue);
  let node = nodeHash.getItem(nodeKey);
  if (!node) {

    // No node exists yet for this attr value. Create a new node for it.
    node = makeNode(nodeType, nodeValue);
    if (!nodeHash.setItem(nodeKey, node)) {
      // Couldn't create new node (probably because of nodeHash size limits.
      // Cache the event id and the event's parent indicator id, for future reference.
      if (!isEmpty(evt.id)) {
        set(nodeHash.get('eventIdsNotRendered'), evt.id, true);
      }
      if (!isEmpty(evt.indicatorId)) {
        set(nodeHash.get('indicatorIdsNotRendered'), evt.indicatorId, true);
      }
      return null;
    }
  }

  // Now that we have a node, add this event to that node's event list.
  node.events.pushObject(evt);
  return node;
}

// Ensure a given link type + source + target is included in the given hash of links.
// If the link is not found already in the hash, a new link is created and added to the hash.
// Additionally, the given event POJO is added to the link's events array.
// Returns the corresponding link (if any).
function checkLink(linkType, sourceNode, targetNode, linkHash, evt) {
  if (!sourceNode || !targetNode || (sourceNode === targetNode)) {
    return null;
  }

  // Do we already have a link for these inputs?
  const linkKey = makeLinkId(linkType, sourceNode, targetNode);
  let link = linkHash[linkKey];
  if (!link) {

    // No link exists yet for this. Create a new link for it.
    link = linkHash[linkKey] = makeLink(linkType, sourceNode, targetNode);
  }

  // Now that we have a link, add this event to that link's event list.
  link.events.pushObject(evt);
}

// Make nodes from the properties (possibly empty) of a given device POJO and event POJO, if they are
// not already found in the given node hash.
// Returns the collection of found/made nodes, keyed by node type.
function checkDeviceNodes(device, nodeHash, evt) {
  const nodes = {};
  if (device) {
    Object.keys(DEVICE_PROPS_TO_NODE_TYPES).forEach((prop) => {
      const type = DEVICE_PROPS_TO_NODE_TYPES[prop];
      const node = checkNode(type, device[prop], nodeHash, evt);
      if (node) {
        nodes[type] = node;
      }
    });
  }
  return nodes;
}

// Makes links among a given set of nodes for a device & user, if they are not already found in the given link hash.
// Additionally, the given event POJO is added to the events array of each found/created link.
function checkDeviceAndUserLinks(deviceNodes, userNode, linkHash, evt) {
  checkLink(LinkTypes.AS, deviceNodes[NodeTypes.HOST], deviceNodes[NodeTypes.IP], linkHash, evt);
  checkLink(LinkTypes.BELONGS_TO, deviceNodes[NodeTypes.MAC], deviceNodes[NodeTypes.HOST] || deviceNodes[NodeTypes.IP] || deviceNodes[NodeTypes.DOMAIN], linkHash, evt);
  checkLink(LinkTypes.BELONGS_TO, deviceNodes[NodeTypes.HOST] || deviceNodes[NodeTypes.IP], deviceNodes[NodeTypes.DOMAIN], linkHash, evt);
  checkLink(LinkTypes.USES, userNode, deviceNodes[NodeTypes.HOST] || deviceNodes[NodeTypes.IP] || deviceNodes[NodeTypes.MAC] || deviceNodes[NodeTypes.DOMAIN], linkHash, evt);
}

// Searches the given node & link hashes for nodes & links that represent the entities mentioned in the given event.
// If not found, creates the missing nodes & links and adds them to the given hashes.
// Additionally, adds the given event to the `events` arrays of the found/created nodes & links.
function parseEventNodesAndLinks(evt, nodeHash, linkHash) {
  const { source, destination, detector } = evt;
  const sourceDevice = (source) ? source.device : undefined;
  const srcUser = (source) ? source.user : {};
  const sourceUsername = (srcUser && srcUser.username) ? srcUser.username : undefined;

  const destinationDevice = (destination) ? destination.device : undefined;
  const dstUser = (destination) ? destination.user : {};
  const destinationUsername = (dstUser && dstUser.username) ? dstUser.username : undefined;
  const data = (evt.data) ? evt.data : [];

  // Generate nodes for the source & dest devices, if any.
  // For the source device, first try parsing nodes from 'source.device'.
  let sourceDeviceNodes = checkDeviceNodes(sourceDevice, nodeHash, evt);
  if (!Object.keys(sourceDeviceNodes).length) {
    // No nodes parsed from 'source.device', so try parsing from 'detector'.
    sourceDeviceNodes = checkDeviceNodes(detector, nodeHash, evt);
  }
  // For the destination device, first try parsing nodes from 'destination.device'.
  const destDeviceNodes = checkDeviceNodes(destinationDevice, nodeHash, evt);
  if (!destDeviceNodes[NodeTypes.HOST]) {
    // No host parsed from 'destination.device', so try parsing host name from 'event.domain'.
    // Ironically, the 'event.domain' field is actually a misnomer. Normalization scripts populate it from the
    // 'alias.host' meta key, which is actually read from the HTTP HOST header in Network events.
    destDeviceNodes[NodeTypes.HOST] = checkNode(NodeTypes.HOST, evt.domain, nodeHash, evt);
  }

  // Generate nodes for the source & dest users, if any.
  const sourceUserNode = checkNode(NodeTypes.USER, sourceUsername, nodeHash, evt);
  const destUserNode = checkNode(NodeTypes.USER, destinationUsername, nodeHash, evt);

  // Generate links among the nodes for source device & source user.
  checkDeviceAndUserLinks(sourceDeviceNodes, sourceUserNode, linkHash, evt);

  // Generate links among the nodes for destination device & destination user.
  checkDeviceAndUserLinks(destDeviceNodes, destUserNode, linkHash, evt);

  // Generate a link between the source & dest "anchor" nodes.
  const sourceAnchorNode = sourceDeviceNodes[NodeTypes.IP] ||
    sourceDeviceNodes[NodeTypes.MAC] ||
    sourceDeviceNodes[NodeTypes.HOST] ||
    sourceDeviceNodes[NodeTypes.DOMAIN] ||
    sourceUserNode;
  const destAnchorNode = destDeviceNodes[NodeTypes.IP] ||
    destDeviceNodes[NodeTypes.MAC] ||
    destDeviceNodes[NodeTypes.HOST] ||
    destDeviceNodes[NodeTypes.DOMAIN] ||
    destUserNode;
  checkLink(LinkTypes.COMMUNICATES_WITH, sourceAnchorNode, destAnchorNode, linkHash, evt);

  // Generate nodes & links for the filenames & hashes, if any.
  const fileNameNodes = [];
  const fileHashNodes = [];
  data.forEach(({ filename, hash }) => {

    // Generate nodes for filename & hash, if any.
    const fileNameNode = checkNode(NodeTypes.FILE_NAME, filename, nodeHash, evt);
    fileNameNodes.push(fileNameNode);

    const fileHashNode = checkNode(NodeTypes.FILE_HASH, hash, nodeHash, evt);
    fileHashNodes.push(fileHashNode);

    // Link the 2 nodes for filename & corresponding hash, if found.
    if (fileHashNode && fileNameNode) {
      checkLink(LinkTypes.IS_NAMED, fileHashNode, fileNameNode, linkHash, evt);
    }

    // Link either filename or hash to source & dest "anchor" nodes, if any.
    checkLink(LinkTypes.HAS_FILE, sourceAnchorNode, fileHashNode || fileNameNode, linkHash, evt);
    checkLink(LinkTypes.HAS_FILE, destAnchorNode, fileHashNode || fileNameNode, linkHash, evt);
  });
}

/**
 * Generates a hash of node objects & a hash of link objects from a given array of normalized event objects.
 * The node & link objects are intended to be compatible with a d3 force-layout.
 *
 * Generally speaking, an event's properties can mention 1 or more entities, where an "entity" is defined as known
 * identifier, such as a user, host, domain, ip address, mac address, file name or file hash.  By inspecting known properties of
 * a given event object, we can find the entities for that event, and generate a node object for each found entity.
 *
 * We can also infer a few basic "links" between entities that are found in the same event. For example:
 * (i) if a user & a host are found in the same event's side, we infer that the user "uses" the host -- that's a link of
 * type = "uses" with source entity = the user and target entity = the host;
 * (ii) if a host & a source ip are found in the same event's side, we infer that the host "communicates as" that ip;
 * (iii) if an event mentions a source ip and a destination ip, then we infer that the source ip  "communicates with"
 * the destination ip;
 * (iv) if a domain & a destination ip are found in the same event, we infer that the domain "communicates as" that ip.
 *
 * Each event object is expected to be a normalized alert event POJO.
 * @see https://wiki.na.rsa.net/pages/viewpage.action?spaceKey=ITSRM&title=Normalized+Alert+format
 *
 * Each node generated by this function will be a POJO with the following attrs:
 * `type`: {string} either 'user', 'host', 'domain', 'ip' or 'file';
 * `value`: {string} either a user name, host name, domain name, ip address, or file hash;
 * `id`: {string} unique identifier for the `type` + `value` pair, e.g., `${type}::${value}`;
 * `events`: {object[]} the subset of the given events in which this node was found.
 *
 * Each link generated by this function will be a POJO with the following attrs:
 * `source`: {object} one of the generated nodes;
 * `target`: {object} one of the generated nodes;
 * `type`: {string} either 'uses', 'communicates as', 'communicates with', 'sends' or 'to';
 * `id`: {string} unique identifier for the link, e.g., `${type}||${source.id}||${destination.id}`;
 * `events`: {object[]} the subset of the given events in which this link was found.
 *
 * @param {object[]} events Array of normalized event objects, possibly empty.
 * @param {Object} [options={}] Config settings to customize the behavior of this call.
 * @param {Boolean} [options.ignoreCache=false] If true, will not read/write cache. Forces the parsing of all given events.
 * Otherwise, this function will compare the given events to a cache from the previous invocation (if any). If the
 * cached events array is a subset of the given events array, then this function will re-use the cached results and
 * only parse the new events that are not already cached.
 * @param {Boolean} [options.nodeLimit] If given, specifies the maximum number of nodes which should be generated.
 * @public
 */
export default function eventsToNodesAndLinks(events, options = {}) {
  const { nodeLimit } = options;
  let nodeHash = HashWithLimit.create({
    limit: nodeLimit,
    indicatorIdsNotRendered: {},
    eventIdsNotRendered: {}
  });
  let linkHash = {};
  let newEvents = events;

  // Don't utilize the cache for the trivial empty use-case.
  let { ignoreCache } = options;
  ignoreCache = ignoreCache || !events || !events.length;

  if (!ignoreCache) {

    // Check if the given events array is just the same cached events plus some more events appended
    // at the end (e.g., we could be getting called repeatedly with an accumulating stream of events).
    if (isStartOfArray(cache.events, events)) {

      // Reuse cached results.
      nodeHash = cache.nodeHash;
      linkHash = cache.linkHash;

      // Only parse the events which were not already in the cache.
      newEvents = events.slice(cache.events.length);
    }
  }

  (newEvents || []).forEach((evt) => {
    parseEventNodesAndLinks(evt, nodeHash, linkHash);
  });

  const output = {
    nodes: arrayFromHashValues(nodeHash.get('allItems')),
    links: arrayFromHashValues(linkHash),
    nodeLimit,
    hasExceededNodeLimit: nodeHash.get('hasExceededLimit'),
    indicatorIdsNotRendered: nodeHash.get('indicatorIdsNotRendered'),
    eventIdsNotRendered: nodeHash.get('eventIdsNotRendered')
  };

  // Save results to cache for next time.
  if (!ignoreCache) {
    cache = {
      events,
      output,
      nodeHash,
      linkHash
    };
  }

  return output;
}
