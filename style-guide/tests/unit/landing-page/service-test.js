import { moduleFor, test } from 'ember-qunit';

moduleFor('service:landing-page', 'Unit | Service | landing page', {
  // Specify the other units that are required for this test.
  // needs: ['service:foo']
});

// Replace this with your real tests.
test('it exists', function(assert) {
  const service = this.subject();
  assert.ok(service);
});

test('it provides the correct options', function(assert) {
  const service = this.subject();
  assert.equal(service.get('options.length'), 6);
  const options = service.get('options').map(function(option) {
    return option.key;
  });
  assert.ok(options.includes('protected.respond'));
  assert.ok(options.includes('/unified'));
  assert.ok(options.includes('/investigate'));
  assert.ok(options.includes('/admin'));
  assert.ok(options.includes('/live'));
  assert.ok(options.includes('protected.investigate'));
});
