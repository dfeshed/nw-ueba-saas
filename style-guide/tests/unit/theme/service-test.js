import { moduleFor, test } from 'ember-qunit';

moduleFor('service:theme', 'Unit | Service | theme', {
});

test('it exists and works', function(assert) {
  let service = this.subject();
  assert.ok(service, 'Service not defined.');
});

test('it uses the correct localStorageKey', function(assert) {
  let service = this.subject();
  assert.equal(service.get('localStorageKey'), 'rsa::securityAnalytics::themePreference');
});

test('it provides the correct options', function(assert) {
  let service = this.subject();
  assert.equal(service.get('options.length'), 1);
  let options = service.get('options').map(function(option) {
    return option.key;
  });
  assert.ok(options.contains('dark'));
});

test('it has the correct default', function(assert) {
  let service = this.subject();
  assert.equal(service.get('defaultSelection'), 'dark');
});
