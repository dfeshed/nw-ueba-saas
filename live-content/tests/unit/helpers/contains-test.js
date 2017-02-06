import { contains } from 'live-content/helpers/contains';
import { module, test } from 'qunit';

module('Unit | Helper | contains');

// Replace this with your real tests.
test('it works', function(assert) {
  const wonka = { firstName: 'Willy', lastName: 'Wonka', emailAddress: undefined };

  assert.ok(contains([42, [32, 33, 42]]), 'The value is in the array');
  assert.ok(contains(['firstName', wonka]), 'The value is a key in the object');
  assert.ok(contains([wonka, [{}, {}, wonka]]), 'The object reference is in the array');
  assert.notOk(contains([88, [32, 33, 42]]), 'The value is not in the array');
  assert.notOk(contains(['phoneNumber', wonka]), 'The value is not a key in the object');
  assert.notOk(contains(['emailAddress', wonka]), 'The value is undefined despite the key existing');
  assert.notOk(contains([{ firstName: 'Willy', lastName: 'Wonka' }, [{}, wonka, {}]]), 'Equality determined by object reference not properties');
});
