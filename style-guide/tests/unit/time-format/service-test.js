import { moduleFor, test } from 'ember-qunit';

moduleFor('service:time-format', 'Unit | Service | time format', {
  // Specify the other units that are required for this test.
  // needs: ['service:foo']
});

test('it provides the correct options', function(assert) {
  const service = this.subject();
  assert.equal(service.get('options.length'), 2);
  const options = service.get('options').map(function(option) {
    return option.key;
  });
  assert.ok(options.includes('12hr'));
  assert.ok(options.includes('24hr'));
});
