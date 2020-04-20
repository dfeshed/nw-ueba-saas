import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Unit | Component | Power Select Tabs', function(hooks) {
  setupTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('power select tabs computed adds a plus when count is 100', function(assert) {
    const comp = this.owner.lookup('component:query-container/power-select-tabs');
    const queryCounter = this.owner.lookup('service:queryCounter');
    queryCounter.setRecentQueryTabCount(100);

    assert.equal(comp.get('recentTabCount'), '100+', 'Did not find recent tab with the correct count');
  });

  test('power select tabs computed adds a plus when count is more than 100', function(assert) {
    const comp = this.owner.lookup('component:query-container/power-select-tabs');
    const queryCounter = this.owner.lookup('service:queryCounter');
    queryCounter.setRecentQueryTabCount(101);

    assert.equal(comp.get('recentTabCount'), '100+', 'Did not find recent tab with the correct count');
  });

  test('no plus for counts less than 100', function(assert) {
    const comp = this.owner.lookup('component:query-container/power-select-tabs');
    const queryCounter = this.owner.lookup('service:queryCounter');
    queryCounter.setRecentQueryTabCount(99);

    assert.equal(comp.get('recentTabCount'), '99', 'Did not find recent tab with the correct count');
  });
});