
import { contextDataParser } from 'dummy/helpers/context-data-parser';
import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import alertData from '../../data/alert-data';

module('Unit | Helper | context data parser');
const lookupdata = Immutable.from([{}]);

// Replace this with your real tests.
test('it works', function(assert) {
  const lookupData = contextDataParser([[alertData], lookupdata]);
  assert.ok(lookupData.Alerts);
  assert.ok(lookupData.Alerts.resultList.length == 8);
});
