import normalizeLinks from 'respond/utils/force-layout/normalize-links';
import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

module('Unit | Utility | Normalize Links', function(hooks) {
  setupTest(hooks);

  test('normalizeLinks for empty array returns 0', function(assert) {
    assert.notOk(normalizeLinks([]), 'undefined is expected');
  });

  test('normalizeLinks works as expected', function(assert) {
    const nodes = [
      {
        value: 'A',
        incomingLinks: [],
        outgoingLinks: [],
        r: 10
      },
      {
        value: 'B',
        incomingLinks: [],
        outgoingLinks: [],
        r: 20
      },
      {
        value: 'C',
        incomingLinks: [],
        outgoingLinks: []
      }
    ];

    const links = [{
      type: 'is named',
      source: nodes[0],
      target: nodes[1]
    }];
    nodes[0].outgoingLinks.push(links[0]);
    nodes[1].incomingLinks.push(links[0]);

    normalizeLinks(links);

    assert.equal(links[0].linkDistance, 55, 'expect normalized distance to be 55');
  });
});
