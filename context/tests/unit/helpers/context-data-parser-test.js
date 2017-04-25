
import { contextDataParser } from 'dummy/helpers/context-data-parser';
import { module, test } from 'qunit';
import alertData from '../../data/alert-data';

module('Unit | Helper | context data parser');

// Replace this with your real tests.
test('it works', function(assert) {
  const lookupData = contextDataParser([[alertData], []]);
  assert.ok(lookupData.Alerts);
  assert.ok(lookupData.Alerts.resultList.length == 8);
});