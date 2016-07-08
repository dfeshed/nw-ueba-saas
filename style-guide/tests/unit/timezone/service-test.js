import { moduleFor, test } from 'ember-qunit';

moduleFor('service:timezone', 'Unit | Service | timezone', {
  // Specify the other units that are required for this test.
  // needs: ['service:foo']
});

// Replace this with your real tests.
test('it exists', function(assert) {
  let service = this.subject();
  assert.ok(service);
});

test('it uses the correct localStorageKey', function(assert) {
  let service = this.subject();
  assert.equal(service.get('localStorageKey'), 'rsa::securityAnalytics::timeZonePreference');
});

test('it has the correct default', function(assert) {
  let service = this.subject();
  assert.equal(service.get('defaultSelection'), 'America/New_York');
});
