import { module, test } from 'qunit';

import queryUtils from 'investigate-events/actions/utils';
import { encodeMetaFilterConditions } from 'investigate-events/actions/fetch/utils';

module('Unit | Helper | query utils');

const params = {
  et: 0,
  eid: 1,
  mf: 'a%3D"a/%3Db%3D/a"',
  mps: 'default',
  rs: 'max',
  sid: 2,
  st: 3
};

const conditions = [{
  meta: 'foo',
  operator: '=',
  value: 'bar'
}, {
  meta: 'foo',
  operator: 'exists',
  value: 'bar'
}, {
  meta: 'foo',
  operator: 'begins',
  value: '"//"'
}];

const complexConditions = [{
  complexFilter: 'foo="bar"||foo=baz'
}, {
  complexFilter: 'bar="foo"||baz=foo'
}];

test('parseQueryParams correctly parses URI', function(assert) {
  assert.expect(8);
  const result = queryUtils.parseQueryParams(params);
  assert.equal(result.endTime, params.et, '"et" was not parsed to "endTime"');
  assert.equal(result.sessionId, params.eid, '"eid" was not parsed to "sessionId"');
  assert.equal(result.metaFilter.uri, params.mf, '"mf" was not parsed to "metaFilter.uri"');
  assert.equal(result.metaFilter.conditions.length, 1, '"mf" was not parsed to "metaFilter.conditions"');
  assert.equal(result.metaPanelSize, params.mps, '"mps" was not parsed to "metaPanelSize"');
  assert.equal(result.reconSize, params.rs, '"rs" was not parsed to "reconSize"');
  assert.equal(result.serviceId, params.sid, '"sid" was not parsed to "serviceId"');
  assert.equal(result.startTime, params.st, '"st" was not parsed to "startTime"');
});

test('parseQueryParams correctly parses forward slashes and operators in text format conditions', function(assert) {
  assert.expect(3);
  const result = queryUtils.parseQueryParams(params);
  assert.equal(result.metaFilter.conditions[0].meta, 'a', 'forward slash was not parsed correctly');
  assert.equal(result.metaFilter.conditions[0].operator, '=', 'forward slash was not parsed correctly');
  assert.equal(result.metaFilter.conditions[0].value, '"a/=b=/a"', 'forward slash was not parsed correctly');
});

test('encodeMetaFilterConditions correctly encodes conditions', function(assert) {
  assert.expect(1);
  const result = encodeMetaFilterConditions(conditions);

  assert.equal(result, 'foo=bar && foo exists && foo begins "//"');
});

test('encodeMetaFilterConditions correctly encodes complex filters', function(assert) {
  assert.expect(1);
  const result = encodeMetaFilterConditions(complexConditions);

  assert.equal(result, 'foo="bar"||foo=baz && bar="foo"||baz=foo');
});
