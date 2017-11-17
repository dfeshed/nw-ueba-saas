import queryUtils from 'investigate-hosts/actions/utils/query-util';
import { module, test } from 'qunit';

module('Unit | Helper | query util');

const meta = 'ip.src';
const value = '1.1.1.1';
const queryString = `${meta}%20%3d%20${value}`;

test('parseQueryString correctly parses query', function(assert) {

  assert.expect(4);

  const result = queryUtils.parseQueryString(queryString);
  assert.equal(result.length, 1, 'Only one propertyName/propertyValue extracted for queryString');
  assert.equal(result[0].propertyName, 'machine.networkInterfaces.ipv4', 'PropertyName key extracted for queryString');
  assert.equal(result[0].restrictionType, 'EQUAL', 'Operator extracted for queryString');
  assert.equal(result[0].propertyValues[0].value, value, 'Value extracted for queryString');

});
