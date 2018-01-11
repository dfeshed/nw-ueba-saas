import queryUtils from 'investigate-events/actions/utils';
import { module, test } from 'qunit';

module('Unit | Helper | query utils');

const params = {
  et: 0,
  eid: 1,
  mf: 'a%3Db',
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
