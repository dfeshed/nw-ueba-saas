import { moduleFor, test } from 'ember-qunit';

moduleFor('service:date-format', 'Unit | Service | date format', {
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
  assert.equal(service.get('localStorageKey'), 'rsa::securityAnalytics::dateFormatPreference');
});

test('it provides the correct options', function(assert) {
  let service = this.subject();
  assert.equal(service.get('options.length'), 3);
  let options = service.get('options').map(function(option) {
    return option.key;
  });
  assert.ok(options.includes('MM/DD/YYYY'));
  assert.ok(options.includes('DD/MM/YYYY'));
  assert.ok(options.includes('YYYY/MM/DD'));
});

test('it has the correct default', function(assert) {
  let service = this.subject();
  assert.equal(service.get('defaultSelection'), 'MM/DD/YYYY');
});
