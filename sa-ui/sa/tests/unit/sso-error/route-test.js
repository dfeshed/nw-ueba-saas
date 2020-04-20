import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

module('Unit | Route | sso-error', function(hooks) {
  setupTest(hooks);

  test('it exists', function(assert) {
    const route = this.owner.lookup('route:sso-error');
    assert.ok(route);
  });
});
