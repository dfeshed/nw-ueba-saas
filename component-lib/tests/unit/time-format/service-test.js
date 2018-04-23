import { moduleFor, test } from 'ember-qunit';

moduleFor('service:time-format', 'Unit | Service | time format', {
  needs: ['service:request']
});

test('it provides the correct options', function(assert) {
  const service = this.subject();
  assert.equal(service.get('options.length'), 2);
  const options = service.get('options').map(function(option) {
    return option.key;
  });
  assert.ok(options.includes('HR12'));
  assert.ok(options.includes('HR24'));
});
