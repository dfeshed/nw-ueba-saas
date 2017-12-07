import { module, test } from 'qunit';

import { parseQueryString } from 'investigate-hosts/actions/utils/query-util';

module('Unit | Util');

test('Parse the query string', function(assert) {
  let queryString = 'ip.src%20%3D%201.1.1.1';
  const [parsedString] = parseQueryString(queryString);
  assert.equal(parsedString.propertyName, 'machine.networkInterfaces.ipv4');
  assert.equal(parsedString.propertyValues[0].value, '1.1.1.1');

  queryString = 'alias.host%20%3D%20%27Test_Machine%27';
  const [newParsedString] = parseQueryString(queryString);
  assert.equal(newParsedString.propertyName, 'machine.machineName');
  assert.equal(newParsedString.propertyValues[0].value, 'Test_Machine');
});
