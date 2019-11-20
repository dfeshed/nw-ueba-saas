import { module, test } from 'qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';

import queryUtils from 'investigate-events/actions/utils';
import { DEFAULT_LANGUAGES } from '../../helpers/redux-data-helper';

const params = {
  et: 0,
  eid: 1,
  mf: 'filename%20%3D%20<reston%3D\'virginia.sys>',
  mps: 'default',
  rs: 'max',
  sid: 2,
  st: 3,
  pdhash: 'foo,bar,baz',
  sortField: 'time',
  sortDir: 'Ascending'
};

module('Unit | Helper | Actions Utils', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('*', 'i18n', 'service:i18n');

    initialize(this.owner);
  });

  test('parseBasicQueryParams correctly parses URI', function(assert) {
    assert.expect(9);
    const result = queryUtils.parseBasicQueryParams(params, DEFAULT_LANGUAGES);
    assert.equal(result.endTime, params.et, '"et" was not parsed to "endTime"');
    assert.equal(result.sessionId, params.eid, '"eid" was not parsed to "sessionId"');
    assert.equal(result.metaPanelSize, params.mps, '"mps" was not parsed to "metaPanelSize"');
    assert.equal(result.reconSize, params.rs, '"rs" was not parsed to "reconSize"');
    assert.equal(result.serviceId, params.sid, '"sid" was not parsed to "serviceId"');
    assert.equal(result.startTime, params.st, '"st" was not parsed to "startTime"');
    assert.equal(result.sortField, params.sortField, '"sortField" was not parsed to "sortField"');
    assert.equal(result.sortDir, params.sortDir, '"sortDir" was not parsed to "sortDir"');
    assert.deepEqual(result.pillDataHashes, ['foo', 'bar', 'baz'], '"pdhash" was not parsed to proper hashes');
  });

  test('parseBasicQueryParams correctly parses URI', function(assert) {
    assert.expect(1);

    const modParams = {
      ...params,
      pdhash: ['foo', 'a', 'z']
    };

    const result = queryUtils.parseBasicQueryParams(modParams, DEFAULT_LANGUAGES);
    assert.deepEqual(result.pillDataHashes, ['foo', 'a', 'z'], '"pdhash" handled array');
  });

  test('parseBasicQueryParams leaves hashes undefined if there are none', function(assert) {
    const result = queryUtils.parseBasicQueryParams({ rs: 'max' }, DEFAULT_LANGUAGES);
    assert.equal(result.pillDataHashes, undefined, '"pdhash" was not parsed to proper hashes');
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

  test('updateErrorMsgIfMaxMemory does not return with text appended without match', function(assert) {
    assert.expect(1);
    const result = queryUtils.updateErrorMsgIfMaxMemory('foo');
    assert.equal(result, 'foo');
  });

  test('updateErrorMsgIfMaxMemory does return with text appended with match', function(assert) {
    assert.expect(1);
    const result = queryUtils.updateErrorMsgIfMaxMemory('max.query.memory warning.');
    assert.equal(result, 'max.query.memory warning. To avoid this error try limiting your query by narrowing the time range, adding further filters, removing complex filter operations, or decreasing the number of columns in your column group.');
  });

});
