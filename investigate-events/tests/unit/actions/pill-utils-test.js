import { module, test } from 'qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import pillUtils from 'investigate-events/actions/pill-utils';
import { CLOSE_PAREN, OPEN_PAREN, QUERY_FILTER, TEXT_FILTER, COMPLEX_FILTER } from 'investigate-events/constants/pill';

module('Unit | Helper | Pill Actions Utils', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
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
    const selectedPills = pillUtils.selectPillsFromPosition(pills, 1, 'right');

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
    const selectedPills = pillUtils.selectPillsFromPosition(pills, 3, 'left');

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

  test('_hasEmptyParensAt properly detects empty parentheses', function(assert) {
    let result;
    let pillsData = [];

    result = pillUtils._hasEmptyParensAt(pillsData, 0);
    assert.notOk(result, 'no pills');

    pillsData = [
      { type: OPEN_PAREN },
      { type: CLOSE_PAREN }
    ];
    result = pillUtils._hasEmptyParensAt(pillsData, 1);
    assert.ok(result, 'just parens');
    result = pillUtils._hasEmptyParensAt(pillsData, 0);
    assert.notOk(result, 'just parens with incorrect index');
    result = pillUtils._hasEmptyParensAt(pillsData, 2);
    assert.notOk(result, 'just parens with out of bounds index');

    pillsData = [
      { type: OPEN_PAREN },
      { type: QUERY_FILTER },
      { type: CLOSE_PAREN }
    ];
    result = pillUtils._hasEmptyParensAt(pillsData, 1);
    assert.notOk(result, 'query filter wrapped in parens');
    result = pillUtils._hasEmptyParensAt(pillsData, 0);
    assert.notOk(result, 'query filter wrapped in parens with incorrect index');

    pillsData = [
      { type: OPEN_PAREN },
      { type: OPEN_PAREN },
      { type: CLOSE_PAREN },
      { type: CLOSE_PAREN }
    ];
    result = pillUtils._hasEmptyParensAt(pillsData, 2);
    assert.ok(result, 'nested parens');
    result = pillUtils._hasEmptyParensAt(pillsData, 0);
    assert.notOk(result, 'nested parens with incorrect index');
  });

  test('findEmptyParensAtPosition properly detects empty parentheses', function(assert) {
    let result;
    let pillsData = [];
    result = pillUtils.findEmptyParensAtPosition(pillsData, 0);
    assert.deepEqual(result, [], 'no pills');

    pillsData = [
      { type: OPEN_PAREN, twinId: 1 },
      { type: CLOSE_PAREN, twinId: 1 }
    ];
    result = pillUtils.findEmptyParensAtPosition(pillsData, 1);
    assert.deepEqual(result, pillsData, 'just parens');
    result = pillUtils.findEmptyParensAtPosition(pillsData, 2);
    assert.deepEqual(result, [], 'just parens with out of bounds index');

    pillsData = [
      { type: OPEN_PAREN, twinId: 1 },
      { type: QUERY_FILTER },
      { type: CLOSE_PAREN, twinId: 1 }
    ];
    result = pillUtils.findEmptyParensAtPosition(pillsData, 1);
    assert.deepEqual(result, [], 'query filter wrapped in parens');
    result = pillUtils.findEmptyParensAtPosition(pillsData, 2);
    assert.deepEqual(result, [], 'query filter wrapped in parens with incorrect index');

    pillsData = [
      { type: OPEN_PAREN, twinId: 1 },
      { type: OPEN_PAREN, twinId: 2 },
      { type: OPEN_PAREN, twinId: 3 },
      { type: CLOSE_PAREN, twinId: 3 }, // <-- 3
      { type: CLOSE_PAREN, twinId: 2 },
      { type: CLOSE_PAREN, twinId: 1 }
    ];
    result = pillUtils.findEmptyParensAtPosition(pillsData, 3);
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
    result = pillUtils.findEmptyParensAtPosition(pillsData, 2);
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
    result = pillUtils.findEmptyParensAtPosition(pillsData, 4);
    assert.deepEqual(result, [
      { type: OPEN_PAREN, twinId: 3 },
      { type: CLOSE_PAREN, twinId: 3 },
      { type: OPEN_PAREN, twinId: 2 },
      { type: CLOSE_PAREN, twinId: 2 }
    ], 'nested parens with leading empty parens, index 4');
    result = pillUtils.findEmptyParensAtPosition(pillsData, 1);
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
    const result = pillUtils.contentBetweenParens([pillsData[0]], pillsData);
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

    let result = pillUtils.contentBetweenParens([pillsData[0], pillsData[2]], pillsData);
    assert.equal(result.length, 7, 'Did not find the correct number of pills');
    assert.deepEqual(result,
      pillsData,
      'Did not find the correct pills');

    result = pillUtils.contentBetweenParens([pillsData[2]], pillsData);
    assert.equal(result.length, 3, 'Did not find the correct number of pills');
    assert.deepEqual(result,
      [
        { type: OPEN_PAREN, twinId: 2, id: 3 },
        { type: TEXT_FILTER, id: 4 },
        { type: CLOSE_PAREN, twinId: 2, id: 5 }
      ],
      'Did not find the correct pills');
  });

  test('findSelectedPills will fetch selected parens and their contents with any selected pill outside those parens', function(assert) {
    const pillsData = [
      { type: OPEN_PAREN, twinId: 1, id: 1 },
      { type: QUERY_FILTER, id: 2 },
      { type: QUERY_FILTER, id: 3, isSelected: true },
      { type: OPEN_PAREN, twinId: 2, id: 4, isSelected: true },
      { type: QUERY_FILTER, id: 5 },
      { type: CLOSE_PAREN, twinId: 2, id: 6 },
      { type: OPEN_PAREN, twinId: 3, id: 7 },
      { type: QUERY_FILTER, id: 8, isSelected: true },
      { type: CLOSE_PAREN, twinId: 3, id: 9 },
      { type: CLOSE_PAREN, twinId: 1, id: 10 },
      { type: QUERY_FILTER, id: 11, isSelected: true }
    ];

    const result = pillUtils.findSelectedPills(pillsData);
    assert.deepEqual(result,
      [
        { type: QUERY_FILTER, id: 3, isSelected: true },
        { type: OPEN_PAREN, twinId: 2, id: 4, isSelected: true },
        { type: QUERY_FILTER, id: 5 },
        { type: CLOSE_PAREN, twinId: 2, id: 6 },
        { type: QUERY_FILTER, id: 8, isSelected: true },
        { type: QUERY_FILTER, id: 11, isSelected: true }
      ],
      'Did not find the correct pills');
  });

  test('isValidToWrapWithParens return true if parens are balanced', function(assert) {
    // ( pill pill ( pill ) ( pill )) pill
    const pillsData = [
      { type: OPEN_PAREN },
      { type: QUERY_FILTER },
      { type: QUERY_FILTER },
      { type: OPEN_PAREN },
      { type: QUERY_FILTER },
      { type: CLOSE_PAREN },
      { type: OPEN_PAREN },
      { type: QUERY_FILTER, id: 8, isSelected: true },
      { type: CLOSE_PAREN, twinId: 3, id: 9 },
      { type: CLOSE_PAREN, twinId: 1, id: 10 },
      { type: QUERY_FILTER, id: 11, isSelected: true }
    ];

    assert.ok(pillUtils.isValidToWrapWithParens(pillsData, 0, pillsData.length));
    assert.notOk(pillUtils.isValidToWrapWithParens(pillsData, 2, 4)); // pill ( pill
    assert.notOk(pillUtils.isValidToWrapWithParens(pillsData, 2, 7)); // pill ( pill ) ( pill
    assert.notOk(pillUtils.isValidToWrapWithParens(pillsData, 2, 10)); // pill ( pill ) ( pill )) pill
    assert.notOk(pillUtils.isValidToWrapWithParens(pillsData, 1, 10)); // pill pill ( pill ) ( pill )) pill
    assert.ok(pillUtils.isValidToWrapWithParens(pillsData, 1, 2)); // pill pill
  });

  test('selectedPillIndexes return start and end indexes for selected pills (only QF, Complex, Text )', function(assert) {
    const pillsData = [
      { type: OPEN_PAREN, isSelected: true },
      { type: QUERY_FILTER },
      { type: QUERY_FILTER, isSelected: true },
      { type: OPEN_PAREN, isSelected: true },
      { type: QUERY_FILTER },
      { type: CLOSE_PAREN },
      { type: OPEN_PAREN },
      { type: QUERY_FILTER, isSelected: true },
      { type: CLOSE_PAREN },
      { type: COMPLEX_FILTER, isSelected: true },
      { type: CLOSE_PAREN, isSelected: true }
    ];

    const result = pillUtils.selectedPillIndexes(Immutable.from(pillsData));
    assert.equal(result.startIndex, 2, 'Index of first selected pill');
    assert.equal(result.endIndex, 9, 'Index of last selected pill');
  });
});
