import { module, test } from 'qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';
import queryUtils from 'investigate-events/actions/utils';
import { DEFAULT_LANGUAGES } from '../../helpers/redux-data-helper';
import { CLOSE_PAREN, OPEN_PAREN, QUERY_FILTER, TEXT_FILTER } from 'investigate-events/constants/pill';

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

  test('selectPillsFromPosition returns an array with pills selected in right direction, including itself', function(assert) {
    assert.expect(2);
    const pills = [{
      a: 'foo'
    }, {
      a: 'bar'
    }, {
      a: 'baz'
    }, {
      a: 'bang'
    }, {
      a: 'boom'
    }];
    const selectedPills = queryUtils.selectPillsFromPosition(pills, 1, 'right');

    assert.equal(selectedPills.length, 4, 'Should output all objects from that position to its right');
    assert.deepEqual(selectedPills, [
      {
        a: 'bar'
      }, {
        a: 'baz'
      }, {
        a: 'bang'
      }, {
        a: 'boom'
      }
    ]);
  });

  test('selectPillsFromPosition returns an array with pills selected in left direction, including itself', function(assert) {
    assert.expect(2);
    const pills = [{
      a: 'foo'
    }, {
      a: 'bar'
    }, {
      a: 'baz'
    }, {
      a: 'bang'
    }, {
      a: 'boom'
    }];
    const selectedPills = queryUtils.selectPillsFromPosition(pills, 3, 'left');

    assert.equal(selectedPills.length, 4, 'Should output all objects from that position to its left');
    assert.deepEqual(selectedPills, [
      {
        a: 'foo'
      }, {
        a: 'bar'
      }, {
        a: 'baz'
      }, {
        a: 'bang'
      }
    ]);
  });

  test('hasEmptyParensAt properly detects empty parentheses', function(assert) {
    let result;
    let pillsData = [];

    result = queryUtils.hasEmptyParensAt(pillsData, 0);
    assert.notOk(result, 'no pills');

    pillsData = [
      { type: OPEN_PAREN },
      { type: CLOSE_PAREN }
    ];
    result = queryUtils.hasEmptyParensAt(pillsData, 1);
    assert.ok(result, 'just parens');
    result = queryUtils.hasEmptyParensAt(pillsData, 0);
    assert.notOk(result, 'just parens with incorrect index');
    result = queryUtils.hasEmptyParensAt(pillsData, 2);
    assert.notOk(result, 'just parens with out of bounds index');

    pillsData = [
      { type: OPEN_PAREN },
      { type: QUERY_FILTER },
      { type: CLOSE_PAREN }
    ];
    result = queryUtils.hasEmptyParensAt(pillsData, 1);
    assert.notOk(result, 'query filter wrapped in parens');
    result = queryUtils.hasEmptyParensAt(pillsData, 0);
    assert.notOk(result, 'query filter wrapped in parens with incorrect index');

    pillsData = [
      { type: OPEN_PAREN },
      { type: OPEN_PAREN },
      { type: CLOSE_PAREN },
      { type: CLOSE_PAREN }
    ];
    result = queryUtils.hasEmptyParensAt(pillsData, 2);
    assert.ok(result, 'nested parens');
    result = queryUtils.hasEmptyParensAt(pillsData, 0);
    assert.notOk(result, 'nested parens with incorrect index');
  });

  test('findEmptyParensAtPosition properly detects empty parentheses', function(assert) {
    let result;
    let pillsData = [];
    result = queryUtils.findEmptyParensAtPosition(pillsData, 0);
    assert.deepEqual(result, [], 'no pills');

    pillsData = [
      { type: OPEN_PAREN, twinId: 1 },
      { type: CLOSE_PAREN, twinId: 1 }
    ];
    result = queryUtils.findEmptyParensAtPosition(pillsData, 1);
    assert.deepEqual(result, pillsData, 'just parens');
    result = queryUtils.findEmptyParensAtPosition(pillsData, 2);
    assert.deepEqual(result, [], 'just parens with out of bounds index');

    pillsData = [
      { type: OPEN_PAREN, twinId: 1 },
      { type: QUERY_FILTER },
      { type: CLOSE_PAREN, twinId: 1 }
    ];
    result = queryUtils.findEmptyParensAtPosition(pillsData, 1);
    assert.deepEqual(result, [], 'query filter wrapped in parens');
    result = queryUtils.findEmptyParensAtPosition(pillsData, 2);
    assert.deepEqual(result, [], 'query filter wrapped in parens with incorrect index');

    pillsData = [
      { type: OPEN_PAREN, twinId: 1 },
      { type: OPEN_PAREN, twinId: 2 },
      { type: OPEN_PAREN, twinId: 3 },
      { type: CLOSE_PAREN, twinId: 3 }, // <-- 3
      { type: CLOSE_PAREN, twinId: 2 },
      { type: CLOSE_PAREN, twinId: 1 }
    ];
    result = queryUtils.findEmptyParensAtPosition(pillsData, 3);
    assert.deepEqual(result, [
      { type: OPEN_PAREN, twinId: 3 },
      { type: CLOSE_PAREN, twinId: 3 },
      { type: OPEN_PAREN, twinId: 2 },
      { type: CLOSE_PAREN, twinId: 2 },
      { type: OPEN_PAREN, twinId: 1 },
      { type: CLOSE_PAREN, twinId: 1 }
    ], 'deeply nested parens');

    pillsData = [
      { type: OPEN_PAREN, twinId: 1 },
      { type: OPEN_PAREN, twinId: 2 },
      { type: CLOSE_PAREN, twinId: 2 }, // <-- 2
      { type: CLOSE_PAREN, twinId: 1 },
      { type: OPEN_PAREN, twinId: 3 },
      { type: CLOSE_PAREN, twinId: 3 }
    ];
    result = queryUtils.findEmptyParensAtPosition(pillsData, 2);
    assert.deepEqual(result, [
      { type: OPEN_PAREN, twinId: 2 },
      { type: CLOSE_PAREN, twinId: 2 },
      { type: OPEN_PAREN, twinId: 1 },
      { type: CLOSE_PAREN, twinId: 1 }
    ], 'nested parens with trailing empty parens');

    pillsData = [
      { type: OPEN_PAREN, twinId: 1 },
      { type: CLOSE_PAREN, twinId: 1 }, // <-- 1
      { type: OPEN_PAREN, twinId: 2 },
      { type: OPEN_PAREN, twinId: 3 },
      { type: CLOSE_PAREN, twinId: 3 }, // <-- 4
      { type: CLOSE_PAREN, twinId: 2 }
    ];
    result = queryUtils.findEmptyParensAtPosition(pillsData, 4);
    assert.deepEqual(result, [
      { type: OPEN_PAREN, twinId: 3 },
      { type: CLOSE_PAREN, twinId: 3 },
      { type: OPEN_PAREN, twinId: 2 },
      { type: CLOSE_PAREN, twinId: 2 }
    ], 'nested parens with leading empty parens, index 4');
    result = queryUtils.findEmptyParensAtPosition(pillsData, 1);
    assert.deepEqual(result, [
      { type: OPEN_PAREN, twinId: 1 },
      { type: CLOSE_PAREN, twinId: 1 }
    ], 'nested parens with leading empty parens, index 1');
  });

  test('it returns all pills between a set of parens including themselves', function(assert) {
    const pillsData = [
      { type: OPEN_PAREN, twinId: 1, id: 1 },
      { type: QUERY_FILTER, id: 2 },
      { type: TEXT_FILTER, id: 3 },
      { type: CLOSE_PAREN, twinId: 1, id: 4 },
      { type: QUERY_FILTER, id: 5 }
    ];
    const result = queryUtils.contentBetweenParens([pillsData[0]], pillsData);
    assert.equal(result.length, 4, 'Did not find the correct number of pills');
    assert.deepEqual(result,
      [
        { type: OPEN_PAREN, twinId: 1, id: 1 },
        { type: QUERY_FILTER, id: 2 },
        { type: TEXT_FILTER, id: 3 },
        { type: CLOSE_PAREN, twinId: 1, id: 4 }
      ],
      'Did not find the correct pills');
  });

  test('It returns all unique pills from between a set of parens', function(assert) {
    // (pill (text) pill)
    const pillsData = [
      { type: OPEN_PAREN, twinId: 1, id: 1 },
      { type: QUERY_FILTER, id: 2 },
      { type: OPEN_PAREN, twinId: 2, id: 3 },
      { type: TEXT_FILTER, id: 4 },
      { type: CLOSE_PAREN, twinId: 2, id: 5 },
      { type: QUERY_FILTER, id: 6 },
      { type: CLOSE_PAREN, twinId: 1, id: 7 }
    ];

    let result = queryUtils.contentBetweenParens([pillsData[0], pillsData[2]], pillsData);
    assert.equal(result.length, 7, 'Did not find the correct number of pills');
    assert.deepEqual(result,
      pillsData,
      'Did not find the correct pills');

    result = queryUtils.contentBetweenParens([pillsData[2]], pillsData);
    assert.equal(result.length, 3, 'Did not find the correct number of pills');
    assert.deepEqual(result,
      [
        { type: OPEN_PAREN, twinId: 2, id: 3 },
        { type: TEXT_FILTER, id: 4 },
        { type: CLOSE_PAREN, twinId: 2, id: 5 }
      ],
      'Did not find the correct pills');
  });

  // (pill (text) pill) -> (pill pill)
  test('pillsSetDifference returns everything from pillsData except the enclosed parens contents', function(assert) {
    const pillsData = [
      { type: OPEN_PAREN, twinId: 1, id: 1 },
      { type: QUERY_FILTER, id: 2 },
      { type: OPEN_PAREN, twinId: 2, id: 3 },
      { type: TEXT_FILTER, id: 4 },
      { type: CLOSE_PAREN, twinId: 2, id: 5 },
      { type: QUERY_FILTER, id: 6 },
      { type: CLOSE_PAREN, twinId: 1, id: 7 }
    ];

    // send open paren's position
    let result = queryUtils.pillsSetDifference(2, pillsData);
    assert.deepEqual(result,
      [
        { type: OPEN_PAREN, twinId: 1, id: 1 },
        { type: QUERY_FILTER, id: 2 },
        { type: QUERY_FILTER, id: 6 },
        { type: CLOSE_PAREN, twinId: 1, id: 7 }
      ],
      'Did not find the correct pills');

    // send close paren's position, result should be the same
    result = queryUtils.pillsSetDifference(4, pillsData);
    assert.deepEqual(result,
      [
        { type: OPEN_PAREN, twinId: 1, id: 1 },
        { type: QUERY_FILTER, id: 2 },
        { type: QUERY_FILTER, id: 6 },
        { type: CLOSE_PAREN, twinId: 1, id: 7 }
      ],
      'Did not find the correct pills');
  });
});