import { module, test } from 'qunit';
import { buildBaseUrl } from 'streaming-data/services/data-access/utils/util';

module('Unit | Utils | util');

test('generating url from the base url when socketUrlPostfix is passed', function(assert) {
  assert.expect(1);
  const newUrl = buildBaseUrl('endpoint/socket', '123', 'endpoint');
  assert.equal(newUrl, 'endpoint/socket/123', 'new url got generated');
});

test('generating url from the base url when socketUrlPostfix is undefined', function(assert) {
  assert.expect(1);
  const newUrl = buildBaseUrl('endpoint/socket', null, 'endpoint');
  assert.equal(newUrl, 'endpoint/socket');
});

test('generating url when only base url is passed', function(assert) {
  assert.expect(1);
  const newUrl = buildBaseUrl('endpoint/socket');
  assert.equal(newUrl, 'endpoint/socket');
});

test('generating url when only base url and socketUrlPostfix are passed', function(assert) {
  assert.expect(1);
  const newUrl = buildBaseUrl('endpoint/socket', '123');
  assert.equal(newUrl, 'endpoint/socket');
});
