import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Unit | Component | Pill Operator', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('PowerSelect filter function filters properly', function(assert) {
    const comp = this.owner.lookup('component:query-container/pill-operator');
    const o1 = { displayName: 'foobar' };
    const o2 = { displayName: 'barbaz' };
    const o3 = { displayName: '!exists' };

    assert.equal(comp._matcher(o1, 'foo'), 0, 'Unable to find item');
    assert.equal(comp._matcher(o1, '   foo'), 0, 'Did not ignore leading spaces');
    assert.equal(comp._matcher(o2, 'foo'), -1, 'Found item, but should not have');
    assert.equal(comp._matcher(o3, 'e'), -1, 'Leading "!" in displayName was not ignored');
  });
});