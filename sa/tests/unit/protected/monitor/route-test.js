import { setupTest } from 'ember-qunit';
import { module, test } from 'qunit';

module('Unit | Route | protected/monitor', function(hooks) {
  setupTest(hooks);

  test('it exists', function(assert) {
    const route = this.owner.lookup('route:protected/monitor');
    assert.ok(route);
  });
});
