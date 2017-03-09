
import { dataSourceEnabled } from 'dummy/helpers/data-source-enabled';
import { module, test } from 'qunit';

module('Unit | Helper | data source enabled');

test('Should Return true if Data source group is there in configured list.', function(assert) {
  const result = dataSourceEnabled([['LIST'], 'LIST']);
  assert.ok(result);
});

test('Should Return true if Data source group name is overview.', function(assert) {
  const result = dataSourceEnabled([['LIST'], 'overview']);
  assert.ok(result);
});

test('Should Return false if Data source group name is not there in configured data source list', function(assert) {
  const result = dataSourceEnabled([['LIST'], 'Users']);
  assert.ok(false == result);
});

