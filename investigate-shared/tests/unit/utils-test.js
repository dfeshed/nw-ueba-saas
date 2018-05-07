import { module, test } from 'qunit';

import { serializeQueryParams } from 'investigate-shared/utils/query-utils';

module('Unit | Util | serialize query params');

const params = {
  et: 0,
  eid: 1,
  mf: 'a%3D\'a/%3Db%3D/a\'',
  mps: 'default',
  rs: 'max',
  sid: 2,
  st: 3
};

test('serializeQueryParams gives the correct URI string', function(assert) {
  assert.expect(1);
  const result = serializeQueryParams(params);
  assert.equal(result, 'et=0&eid=1&mf=a%3D\'a/%3Db%3D/a\'&mps=default&rs=max&sid=2&st=3', 'serializeQueryParams gives the correct URL string');
});

