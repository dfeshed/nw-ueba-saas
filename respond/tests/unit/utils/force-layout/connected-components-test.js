import connectedComponents from 'respond/utils/force-layout/connected-components';
import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

module('Unit | Utility | Connected Components', function(hooks) {
  setupTest(hooks);

  test('connectedComponents for empty array returns 0', function(assert) {
    assert.equal(connectedComponents([]), 0, '0 is expected');
  });

  test('connectedComponents for node array without required properties returns 1', function(assert) {
    const nodes = [
      {
        value: 'A'
      },
      {
        value: 'B'
      }
    ];

    assert.equal(connectedComponents(nodes), 1, '1 is expected');
  });

  test('connectedComponents computes disjoint groups correctly', function(assert) {
    const nodes = [
      {
        value: 'A',
        incomingLinks: [],
        outgoingLinks: []
      },
      {
        value: 'B',
        incomingLinks: [],
        outgoingLinks: []
      },
      {
        value: 'C',
        incomingLinks: [],
        outgoingLinks: []
      }
    ];

    const links = [{
      source: nodes[0],
      target: nodes[1]
    }];

    nodes[0].outgoingLinks.push(links[0]);
    nodes[1].incomingLinks.push(links[0]);

    assert.equal(connectedComponents(nodes), 2, '2 is expected');
  });
});
