import { contains } from 'respond/helpers/contains';
import { module, test } from 'qunit';

module('Unit | Helper | contains');

test('it returns true if the value is in the collection (array)', function(assert) {
  const names = ['george', 'john', 'ringo', 'paul'];
  assert.equal(contains('ringo', names), true, 'contains returns true when the value is in the array');
});

test('it returns false if the value is not in the collection (array)', function(assert) {
  const names = ['george', 'john', 'ringo', 'paul'];
  assert.equal(contains('yoko', names), false, 'contains returns false when the value is not in the array');
});

test('it returns true if the value is in the (object)', function(assert) {
  const names = { george: 'lead guitar', john: 'rhythm guitar', ringo: 'drums', paul: 'bass' };
  assert.equal(contains('ringo', names), true, 'contains returns true when the value serves as a key in the object');
});

test('it returns false if the value is not in the (object)', function(assert) {
  const names = { george: 'lead guitar', john: 'rhythm guitar', ringo: 'drums', paul: 'bass' };
  assert.equal(contains('yoko', names), false, 'contains returns false when the value returns nothing as a lookup key against object');
});

test('array functions are not considered a positive in lookup', function(assert) {
  const names = ['george', 'john', 'ringo', 'paul'];
  assert.equal(contains('slice', names), false, 'contains returns false for function names on array');
});

test('prototype functions are not considered a positive in lookup', function(assert) {
  const names = { george: 'lead guitar', john: 'rhythm guitar', ringo: 'drums', paul: 'bass' };
  assert.equal(contains('constructor', names), false, 'contains returns false for inherited properties');
});
