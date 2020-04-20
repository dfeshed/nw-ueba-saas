import { module, test } from 'qunit';
import { thousandFormat } from 'component-lib/utils/numberFormats';

module('Unit | Util | quote');

test('properly convert number to be comma separated by thousands', function(assert) {
  assert.equal(thousandFormat(55000), '55,000', 'Comma separated number');
  assert.equal(thousandFormat(0), '0', 'Comma separated number');
  assert.equal(thousandFormat(1100000), '1,100,000', 'Comma separated number');
  assert.equal(thousandFormat(200), '200', 'Comma separated number');
});