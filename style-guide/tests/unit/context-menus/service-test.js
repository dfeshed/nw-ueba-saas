import { moduleFor, test } from 'ember-qunit';

moduleFor('service:context-menus', 'Unit | Service | context menus', {
  // Specify the other units that are required for this test.
  // needs: ['service:foo']
});

// Replace this with your real tests.
test('it exists', function(assert) {
  const service = this.subject();
  assert.ok(service);
});

test('it uses the correct localStorageKey', function(assert) {
  const service = this.subject();
  assert.equal(service.get('localStorageKey'), 'rsa::securityAnalytics::contextMenuPreference');
});

test('it has the correct default', function(assert) {
  const service = this.subject();
  assert.equal(service.get('defaultSelection'), true);
});
