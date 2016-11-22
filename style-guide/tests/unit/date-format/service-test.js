import { moduleFor, test } from 'ember-qunit';

moduleFor('service:date-format', 'Unit | Service | date format', {
  // Specify the other units that are required for this test.
  // needs: ['service:foo']
});

test('it provides the correct options', function(assert) {
  const service = this.subject();
  assert.equal(service.get('options.length'), 3);
  const options = service.get('options').map(function(option) {
    return option.key;
  });
  assert.ok(options.includes('MM/DD/YYYY'));
  assert.ok(options.includes('DD/MM/YYYY'));
  assert.ok(options.includes('YYYY/MM/DD'));
});
