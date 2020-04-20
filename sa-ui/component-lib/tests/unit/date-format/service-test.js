import { moduleFor, test } from 'ember-qunit';

moduleFor('service:date-format', 'Unit | Service | date format', {
  // Specify the other units that are required for this test.
  needs: ['service:i18n', 'service:moment', 'service:request']
});

test('it provides the correct options', function(assert) {
  const service = this.subject();
  assert.equal(service.get('options.length'), 3);
  const options = service.get('options').map(function(option) {
    return option.key;
  });
  assert.ok(options.includes('MM/dd/yyyy'));
  assert.ok(options.includes('dd/MM/yyyy'));
  assert.ok(options.includes('yyyy/MM/dd'));
});
