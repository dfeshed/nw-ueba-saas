import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

module('Unit | Route | sso-oauth', function(hooks) {
  setupTest(hooks);

  test('it exists', function(assert) {
    const route = this.owner.lookup('route:sso-oauth');
    assert.ok(route);
  });
});
