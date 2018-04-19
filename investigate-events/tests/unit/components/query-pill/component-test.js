import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from '../../../helpers/engine-resolver';

module('Unit | Component | Query Pill', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('Handles messages from children properly', function(assert) {
    const comp = this.owner.lookup('component:query-container/query-pill');
    comp._metaSelected('a');
    assert.equal(comp.get('selectedMeta'), 'a', 'Wrong meta value');
    assert.notOk(comp.get('isMetaActive'), 'Should be false');
    assert.ok(comp.get('isOperatorActive'), 'Should be true');
    assert.notOk(comp.get('isValueActive'), 'Should be false');
  });
});