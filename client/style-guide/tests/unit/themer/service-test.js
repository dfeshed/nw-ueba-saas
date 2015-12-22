import { moduleFor, test } from 'ember-qunit';

moduleFor('service:themer', 'Unit | Service | themer', {
  // Specify the other units that are required for this test.
  // needs: ['service:foo']
});

// Replace this with your real tests.
test('it exists', function(assert) {
  let service = this.subject();
  assert.ok(service);

  service.set('selected', 'light');
  assert.equal(service.get('selected'), 'light');
});
