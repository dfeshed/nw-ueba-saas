import { module, test } from 'qunit';

import { getQueryNode, getMetaFilterFor, hasherizeEventMeta } from 'investigate-process-analysis/actions/creators/util';

module('Unit | Selectors | creators | utils', function() {

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
    assert.equal(queryNode.metaFilter.conditions.length, 5, 'should contains 5 conditions');
  });

  test('hasherizeEventMeta returns object of the events', function(assert) {

    const event = { metas: [ ['agent.id', '12345'], ['filename.dst', 'testFileName.exe'] ] };
    hasherizeEventMeta(event);

    assert.equal(event.agentId, 12345);
    assert.equal(event.processName, 'testFileName.exe');

  });

  test('hasherizeEventMeta returns empty if no events', function(assert) {

    const event = null;
    hasherizeEventMeta(event);
    assert.equal(event, null);
  });

  test('hasherizeEventMeta returns empty if no metas', function(assert) {

    const event = { metas: null };
    hasherizeEventMeta(event);

    assert.deepEqual(event, { metas: null });

  });


  test('getMetaFilterFor returns correct conditions for parent and child', function(assert) {
    const { conditions } = getMetaFilterFor('PARENT_CHILD', '1', '2');
    assert.equal(conditions[4].value, '(category=\'Process Event\' || category = \'Registry Event\' || category = \'File Event\' || category = \'Network Event\' || category = \'Console Event\')');
  });

  test('getMetaFilterFor returns correct conditions for filters', function(assert) {
    const { conditions } = getMetaFilterFor('PARENT_CHILD', '1', '2', [ {}, {} ]);
    assert.equal(conditions.length, 9);
  });

});
