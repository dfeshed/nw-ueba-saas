
import { listTab } from 'dummy/helpers/list-tab';
import { module, test } from 'qunit';
import data from '../../data/list';

module('Unit | Helper | list tab');

// Replace this with your real tests.
test('Display list data selected', function(assert) {
  const result = listTab(['all', data]);
  assert.equal(result.length, 4);
});

