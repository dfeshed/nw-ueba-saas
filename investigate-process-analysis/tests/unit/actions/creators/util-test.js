import { module, test } from 'qunit';

import { getQueryNode, hasherizeEventMeta } from 'investigate-process-analysis/actions/creators/util';

module('Unit | Selectors | process-tree', function() {

  test('getQueryNode returns query for getting the events', function(assert) {

    const input = {
      et: 1234567890,
      st: 1234566790,
      vid: '1',
      pn: 'test',
      sid: 1,
      aid: 2
    };
    let queryNode = getQueryNode(input);

    assert.equal(queryNode.serviceId, 1);
    assert.equal(queryNode.metaFilter.conditions.length, 5, 'should contains 5 conditions');

    queryNode = getQueryNode(input, '1');
    assert.equal(queryNode.metaFilter.conditions[3].value, '(vid.src = \'1\')', 'should use passed process name');
  });

  test('hasherizeEventMeta returns object of the events', function(assert) {

    const event = { metas: [ ['agent.id', '12345'], ['filename.dst', 'testFileName.exe'] ] };
    hasherizeEventMeta(event);

    assert.equal(event.agentId, 12345);
    assert.equal(event.processName, 'testFileName.exe');

  });

});
