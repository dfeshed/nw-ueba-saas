import { moduleFor, test } from 'ember-qunit';

moduleFor('service:landing-page', 'Unit | Service | landing page', {
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
  assert.equal(service.get('localStorageKey'), 'rsa::securityAnalytics::landingPagePreference');
});

test('it provides the correct options', function(assert) {
  let service = this.subject();
  assert.equal(service.get('options.length'), 4);
  let options = service.get('options').map(function(option) {
    return option.key;
  });
  assert.ok(options.includes('protected.respond'));
  assert.ok(options.includes('protected.monitor'));
  assert.ok(options.includes('protected.admin'));
  assert.ok(options.includes('protected.investigate'));
});

test('it has the correct default', function(assert) {
  let service = this.subject();
  assert.equal(service.get('defaultSelection'), 'protected.respond');
});
