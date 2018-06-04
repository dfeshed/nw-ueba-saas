import { module, test } from 'qunit';

import queryUtils from 'investigate-events/actions/utils';

module('Unit | Helper | query utils');

const params = {
  et: 0,
  eid: 1,
  mf: 'filename%3Dreston\'virginia.sys',
  mps: 'default',
  rs: 'max',
  sid: 2,
  st: 3
};

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
  assert.equal(result.metaFilter.conditions[0].meta, 'filename', 'forward slash was not parsed correctly');
  assert.equal(result.metaFilter.conditions[0].operator, '=', 'forward slash was not parsed correctly');
  assert.equal(result.metaFilter.conditions[0].value, 'reston\'virginia.sys', 'forward slash was not parsed correctly');
});

test('transformTextToFilters returns filter object', function(assert) {
  assert.expect(2);
  const freeFormText = 'medium = 1';
  const result = queryUtils.transformTextToFilters(freeFormText);

  assert.deepEqual(result, { meta: 'medium ', operator: '=', value: ' 1' });
  assert.equal(result.complexFilter, undefined, 'Complex Filter doesnt exist');

});

test('transformTextToFilters returns complex filter object', function(assert) {
  assert.expect(4);
  const freeFormText = 'medium = 1 || medium = 32';
  const result = queryUtils.transformTextToFilters(freeFormText);

  assert.deepEqual(result, { complexFilter: 'medium = 1 || medium = 32' });
  assert.equal(result.meta, undefined, 'meta doesnt exist');
  assert.equal(result.operator, undefined, 'operator doesnt exist');
  assert.equal(result.value, undefined, 'value doesnt exist');

});

test('filterIsPresent return false when filters array and freeFormText are different', function(assert) {
  assert.expect(1);
  const freeFormText = 'medium = 1';
  const filters = [{ meta: 'medium', operator: '=', value: '2' }];

  const result = queryUtils.filterIsPresent(filters, freeFormText);

  assert.notOk(result, 'Filter is not present');
});

test('filterIsPresent return true when filters array and freeFormText are same', function(assert) {
  assert.expect(1);
  const freeFormText = 'medium = 1';
  const filters = [{ meta: 'medium', operator: '=', value: '1' }];

  const result = queryUtils.filterIsPresent(filters, freeFormText);

  assert.ok(result, 'Filter is present');
});
