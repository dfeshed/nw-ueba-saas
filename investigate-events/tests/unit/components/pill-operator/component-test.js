import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

module('Unit | Component | Pill Operator', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('PowerSelect filter function filters properly', function(assert) {
    const comp = this.owner.lookup('component:query-container/pill-operator');
    const input = 'foo';
    const o1 = { displayName: 'foobar' };
    const o2 = { displayName: 'barbaz' };
    assert.equal(comp._matcher(o1, input), 0, 'Should find item');
    assert.equal(comp._matcher(o2, input), -1, 'Should not find item');
  });
});