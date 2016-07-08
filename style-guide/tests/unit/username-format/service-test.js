import { moduleFor, test } from 'ember-qunit';

moduleFor('service:username-format', 'Unit | Service | username format', {
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
  assert.equal(service.get('localStorageKey'), 'rsa::securityAnalytics::friendlyNamePreference');
});

test('it returns the friendlyUsername for username when friendlyUsername is set', function(assert) {
  let service = this.subject();
  service.set('systemUsername', 'Foo');
  service.set('friendlyUsername', 'Bar');
  assert.equal(service.get('username'), 'Bar');
});

test('it returns the systemUsername for username when friendlyName is not set', function(assert) {
  let service = this.subject();
  service.set('systemUsername', 'Foo');
  service.set('friendlyUsername', null);
  assert.equal(service.get('username'), 'Foo');
});

test('it sets the friendlyUsername when setting username', function(assert) {
  let service = this.subject();
  service.set('systemUsername', 'Foo');
  service.set('friendlyUsername', 'Bar');
  service.set('username', 'Baz');
  assert.equal(service.get('friendlyUsername'), 'Baz');
});
