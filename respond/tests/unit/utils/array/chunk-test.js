import chunk from 'respond/utils/array/chunk';
import { module, test } from 'qunit';
import { isArray } from '@ember/array';

module('Unit | Utility | array/chunk');

test('it works', function(assert) {
  const longArray = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17];
  const chunkedArray = chunk(longArray, 3);
  assert.ok(isArray(chunkedArray), 'The output of the chunk function is an array');
  assert.equal(chunkedArray.length, 6, 'The array was turned into 6 chunks of 3');
});

test('chunk on an empty array returns an empty array', function(assert) {
  const array = [];
  const chunkedArray = chunk(array, 3);
  assert.ok(isArray(chunkedArray), 'The output of the chunk function is an array');
  assert.equal(chunkedArray.length, 0, 'Chunking an empty array returns an empty array');
});

test('when the chunksize is greater than the length of the array, all items are returned in one chunk', function(assert) {
  const longArray = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17];
  const chunkedArray = chunk(longArray, 100);
  assert.equal(chunkedArray.length, 1, 'The array was returned as 1 chunk');
  assert.equal(chunkedArray[0].length, longArray.length, 'The one chunk contains all of the items from the original array');
});

test('when a non-array item is provided, it is returned in an array', function(assert) {
  const notAnArray = 5;
  const chunkedArray = chunk(notAnArray, 100);
  assert.equal(chunkedArray[0][0], 5, 'The non-array value was added to an array and is part of the first chunk');
});
