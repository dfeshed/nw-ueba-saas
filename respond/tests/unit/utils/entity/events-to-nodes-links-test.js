import eventsToNodesLinks from 'respond/utils/entity/events-to-nodes-links';
import { module, test } from 'qunit';

module('Unit | Utility | entity/events to nodes and links');

const event1 = {
  source: {
    device: {
      ip_address: 'ip1',
      dns_domain: 'domain1',
      dns_hostname: 'host1'
    },
    user: {
      username: 'user1'
    }
  },
  destination: {
    device: {
      ip_address: 'ip2',
      dns_domain: 'domain2',
      dns_hostname: 'host2'
    },
    user: {
      username: 'user2'
    }
  }
};

const event2 = {
  detector: {
    ip_address: 'ip3',
    dns_domain: 'domain3',
    dns_hostname: 'host3'
  }
};

test('it parses normalized alert events as expected', function(assert) {
  const result = eventsToNodesLinks([ event1 ]);
  assert.equal(result.nodes.length, 8, 'Expected nodes for source & destination values');
  assert.equal(result.links.length, 7, 'Expected links for source & destination values');
});

test('it responds to empty input with empty arrays', function(assert) {
  const result = eventsToNodesLinks();
  assert.equal(result.nodes.length, 0, 'Expected an empty array of nodes');
  assert.equal(result.links.length, 0, 'Expected an empty array of links');
});

test('it utilizes caching to avoid re-constructing nodes & links when possible', function(assert) {
  const { nodes, links } = eventsToNodesLinks([ event1 ]);
  const [ firstNode ] = nodes;
  const [ firstLink ] = links;

  const { nodes: nodes2, links: links2 } = eventsToNodesLinks([ event1, event2 ]);

  assert.equal(firstNode, nodes2[0], 'Expected node from previous call to be re-used in second call');
  assert.equal(firstLink, links2[0], 'Expected link from previous call to be re-used in second call');
  assert.equal(nodes2.length, 11, 'Expected second call to yield additional results');

  const { nodes: nodes3 } = eventsToNodesLinks([ event1, event2 ], { ignoreCache: true });
  assert.notEqual(firstNode, nodes3[0], 'Expected node from previous call to NOT be re-used when ignoreCache is set to true');
});

test('it does not parse the detector into nodes if a non-empty source.device is given', function(assert) {
  const event = {
    source: {
      device: {
        ip_address: 'IP_SOURCE'
      }
    },
    detector: {
      ip_address: 'IP_DETECTOR'
    }
  };
  const result = eventsToNodesLinks([ event ]);
  assert.equal(result.nodes.length, 1, 'Expected node for source device but not detector');
  assert.equal(result.nodes[0].value, 'IP_SOURCE');
});

test('it parses the detector into nodes if a non-null empty source.device is given', function(assert) {
  const event = {
    source: {
      device: {
        ip_address: ''
      }
    },
    detector: {
      ip_address: 'IP_DETECTOR'
    }
  };

  const result = eventsToNodesLinks([ event ]);
  assert.equal(result.nodes.length, 1, 'Expected nodes for detector but not for source device');
  assert.equal(result.nodes[0].value, 'IP_DETECTOR');
});

test('it parses the domain field if the destination.device has no host', function(assert) {
  const event = {
    source: {
      device: {
        ip_address: ''
      }
    },
    destination: {
      device: {
        ip_address: 'IP1',
        dns_hostname: ''
      }
    },
    domain: 'HOST1'
  };

  const result = eventsToNodesLinks([ event ]);
  assert.equal(result.nodes.length, 2, 'Expected node for host');
  assert.equal(result.nodes[1].value, 'HOST1');
  assert.equal(result.nodes[1].type, 'host');
});

test('it stops parsing nodes & links if a node limit is given and exceeded', function(assert) {
  const nodeLimitRequested = 1;
  const { nodes, links, nodeLimit, hasExceededNodeLimit } = eventsToNodesLinks([ event1 ], { nodeLimit: nodeLimitRequested });
  assert.equal(nodes.length, nodeLimit, 'Expected result to include only one node');
  assert.notOk(links.length, 'Expected no links in result');
  assert.equal(nodeLimit, nodeLimitRequested, 'Expected result to reflect the given node limit');
  assert.ok(hasExceededNodeLimit, 'Expected result to indicate that limit was exceeded');
});