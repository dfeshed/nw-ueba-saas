import initializePositions from 'respond/utils/force-layout/initialize-positions';
import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

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

    const groupInfos = initializePositions(nodes);
    assert.ok(groupInfos, 'groupInfos array is expected');
    assert.equal(groupInfos[0].x, 500, 'group center is on x=500 line');
    assert.equal(groupInfos[0].y, 0, 'group center is on y=0 line');

    equalWithinPrecision(assert, groupInfos[0].x, 500, 'group center is on x=500 line');
    equalWithinPrecision(assert, groupInfos[0].y, 0, 'group center is on y=0 line');
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

    const groupInfos = initializePositions(nodes);
    assert.ok(groupInfos, 'groupInfos array is expected');

    equalWithinPrecision(assert, groupInfos[0].x, 500, 'group 1 center is on x=500 line');
    equalWithinPrecision(assert, groupInfos[0].y, 0, 'group 1 center is on y=0 line');

    equalWithinPrecision(assert, groupInfos[1].x, -500, 'group 2 center is on x=-500 line');
    equalWithinPrecision(assert, groupInfos[1].y, 0, 'group 2 center is on y=0 line');
  });
});
