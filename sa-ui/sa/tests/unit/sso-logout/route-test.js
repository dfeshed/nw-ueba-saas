import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

module('Unit | Route | sso-logout', function(hooks) {
  setupTest(hooks);

  test('it exists', function(assert) {
    const route = this.owner.lookup('route:sso-logout');
    assert.ok(route);
  });
});
