import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

module('Unit | Component | Pill Meta', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('PowerSelect filter function filters properly', function(assert) {
    const comp = this.owner.lookup('component:query-container/pill-meta');
    const o1 = { displayName: 'foo', metaName: 'bar' };
    const o2 = { displayName: 'bar', metaName: 'baz' };
    assert.equal(comp._matcher(o1, 'foo'), 0, 'Should find item in "displayName"');
    assert.equal(comp._matcher(o2, 'foo'), -1, 'Should not find item');
    assert.equal(comp._matcher(o1, 'baz'), -1, 'Should not find item');
    assert.equal(comp._matcher(o2, 'baz'), 0, 'Should find item in "metaName"');
  });
});