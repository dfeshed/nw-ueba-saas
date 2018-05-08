import { module, test } from 'qunit';

import { getQueryNode } from 'investigate-process-analysis/actions/creators/util';

module('Unit | Selectors | process-tree', function() {

  test('getQueryNode returns query for getting the events', function(assert) {

    const input = {
      et: 1234567890,
      st: 1234566790,
      pn: 'test',
      sid: 1,
      aid: 2
    };
    let queryNode = getQueryNode(input);

    assert.equal(queryNode.serviceId, 1);
    assert.equal(queryNode.metaFilter.conditions.length, 4, 'should contains 4 conditions');

    queryNode = getQueryNode(input, 'test-2');
    assert.equal(queryNode.metaFilter.conditions[3].value, '\'test-2\'', 'should use passed process name');
  });

});
