import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

module('Unit | Component | query-container', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('computes freeFormText when filters array is altered', function(assert) {
    const comp = this.owner.lookup('component:query-container');
    const obj = { meta: 'foo', operator: '=', value: 'bar' };
    comp.set('filters', [obj]);
    assert.equal(comp.get('freeFormText'), 'foo = bar', 'Expected Filter');
  });
});