import Query from 'sa/utils/query';
import { module, test } from 'qunit';

module('Unit | Utility | query');

test('it is defined', function(assert) {
  let result = Query.create();
  assert.ok(result);
});
