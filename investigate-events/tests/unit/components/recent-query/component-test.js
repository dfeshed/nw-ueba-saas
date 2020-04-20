import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Unit | Component | Recent Query', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('PowerSelect filter function for recent-query filters properly in recent queries tab', function(assert) {
    const comp = this.owner.lookup('component:query-container/recent-query');
    const m1 = { query: 'foo = 1', displayName: 'bar' };
    const m2 = { query: 'bar != 3', displayName: 'baz' };
    const m3 = { query: 'bar contains baz', displayName: 'baz' };

    assert.equal(comp._matcher(m1, 'foo '), 0, 'Did not find recent query');
    assert.equal(comp._matcher(m1, 'foo !'), -1, 'Found item but should not have');
    assert.equal(comp._matcher(m2, 'bar !'), 0, 'Found item but should not have');
    assert.equal(comp._matcher(m2, 'foo'), -1, 'Found item but should not have');
    assert.equal(comp._matcher(m3, '   bar'), 0, 'Did not ignore leading spaces');
  });
});