import { module, test } from 'qunit';

import { getQueryNode, getMetaFilterFor, hasherizeEventMeta } from 'investigate-process-analysis/actions/creators/util';

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
    const queryNode = getQueryNode(input);

    assert.equal(queryNode.serviceId, 1);
    assert.equal(queryNode.metaFilter.conditions.length, 3, 'should contains 3 conditions');
  });

  test('hasherizeEventMeta returns object of the events', function(assert) {

    const event = { metas: [ ['agent.id', '12345'], ['filename.dst', 'testFileName.exe'] ] };
    hasherizeEventMeta(event);

    assert.equal(event.agentId, 12345);
    assert.equal(event.processName, 'testFileName.exe');

  });

  test('getMetaFilterFor returns correct conditions for parent and child', function(assert) {
    const { conditions } = getMetaFilterFor('PARENT_CHILD', '1', '2');
    assert.equal(conditions[4].value, '(process.vid.src = \'2\' || process.vid.dst = \'2\')');
  });

  test('getMetaFilterFor returns correct conditions for filters', function(assert) {
    const { conditions } = getMetaFilterFor('PARENT_CHILD', '1', '2', [ {}, {} ]);
    assert.equal(conditions.length, 5);
  });

});
