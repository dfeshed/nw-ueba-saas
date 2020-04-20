import initializePositions from 'respond/utils/force-layout/initialize-positions';
import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import createAdjacency from 'respond/utils/entity/adjacency';

/*
* Compare 2 numbers within 5 decimal places.
* */
function equalWithinPrecision(assert, left, right, message) {
  assert.equal(left.toFixed(5), right.toFixed(5), message);
}

module('Unit | Utility | Initialize positions', function(hooks) {
  setupTest(hooks);

  test('initializePositions for empty array returns 0', function(assert) {
    assert.notOk(initializePositions([]), 'false is expected');
  });

  test('initializePositions for a single group', function(assert) {
    const nodes = [
      {
        value: 'A',
        ccGroup: 1
      },
      {
        value: 'B',
        ccGroup: 1
      }
    ];
    const links = [{
      id: 'asd',
      source: nodes[0],
      target: nodes[1]
    }];
    createAdjacency(nodes, links);

    const { groupInfos } = initializePositions(nodes);
    assert.ok(groupInfos, 'groupInfos array is expected');
    assert.equal(groupInfos.length, 1, 'only a single group is detected');

    equalWithinPrecision(assert, groupInfos[0].x, 0, 'group center is at origin');
    equalWithinPrecision(assert, groupInfos[0].y, 0, 'group center is at origin');
  });

  test('initializePositions for multiple groups', function(assert) {
    const nodes = [
      {
        value: 'A',
        ccGroup: 1
      },
      {
        value: 'B',
        ccGroup: 2
      }
    ];
    createAdjacency(nodes, []);

    const { groupInfos } = initializePositions(nodes);
    assert.ok(groupInfos, 'groupInfos array is expected');

    equalWithinPrecision(assert, groupInfos[0].x, 1000, 'group 1 center is on x=1000 line');
    equalWithinPrecision(assert, groupInfos[0].y, 0, 'group 1 center is on y=0 line');

    equalWithinPrecision(assert, groupInfos[1].x, -1000, 'group 2 center is on x=-1000 line');
    equalWithinPrecision(assert, groupInfos[1].y, 0, 'group 2 center is on y=0 line');
  });
});
