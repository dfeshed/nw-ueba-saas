import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

import {
  calculateNewStartForNextBatch,
  calculateNextGapAfterFailure,
  mergeMetaIntoEvent
} from 'investigate-events/actions/events-creators-utils';

module('Unit | Actions | events-creators-utils', function(hooks) {
  setupTest(hooks);

  test('calculateNewStartForNextBatch calculations', function(assert) {
    const maxEvents = 100;
    const totalEvents = 50;

    // 100 milliseconds/event (15000 - 10000) / 50
    // target is 25 events
    // 2500 milliseconds, 25 * 100
    // 7500, 10000 - 2500
    // is not rounded because divides evenly by 60
    let result = calculateNewStartForNextBatch(
      10000, 15000, totalEvents, maxEvents
    );
    assert.equal(result, 7500, 'calculated correctly');

    // 75.78 milliseconds/event, (15000 - 11211) / 50
    // target is 25 events
    // 1894.5 milliseconds, 75.78 * 25
    // 9316.5, 11211 - 1894.5
    // is rounded because does not divide evenly by 60
    result = calculateNewStartForNextBatch(
      11211, 15000, totalEvents, maxEvents
    );
    assert.equal(result, 9300, 'calculated correctly');
  });

  test('calculateNextGapAfterFailure calculations', function(assert) {
    const binarySearchData = {
      tooMany: 0,
      noResults: 0
    };

    let gap = calculateNextGapAfterFailure(binarySearchData, 10000, true);
    // cut in half then rounded down to even minutes
    assert.equal(gap, 4980, 'gap cut in half');
    assert.equal(binarySearchData.tooMany, 10000, 'tracking too many');
    assert.equal(binarySearchData.noResults, 0, 'tracking too many');

    gap = calculateNextGapAfterFailure(binarySearchData, 4980, true);
    assert.equal(gap, 2460, 'gap cut in half');
    assert.equal(binarySearchData.tooMany, 4980, 'tracking too many');
    assert.equal(binarySearchData.noResults, 0, 'tracking too many');

    gap = calculateNextGapAfterFailure(binarySearchData, 2460, false);
    assert.equal(gap, 3720, 'gap cut in half');
    assert.equal(binarySearchData.tooMany, 4980, 'tracking too many');
    assert.equal(binarySearchData.noResults, 2460, 'tracking too many');

    // new check, when immediately 0 results
    binarySearchData.tooMany = 0;
    binarySearchData.noResults = 0;
    gap = calculateNextGapAfterFailure(binarySearchData, 10000, false);
    assert.equal(gap, 19980, 'gap doubled');
    assert.equal(binarySearchData.tooMany, 0, 'tracking too many');
    assert.equal(binarySearchData.noResults, 10000, 'tracking too many');
  });

  test('mergeMetaIntoEvent stuffs', function(assert) {
    let event = {
      foo: 1,
      bar: 2
    };
    mergeMetaIntoEvent(event);
    assert.equal(Object.keys(event).length, 2, 'same two keys');
    assert.equal(event.foo, 1, 'same two keys');
    assert.equal(event.bar, 2, 'same two keys');

    event = {
      time: 60000,
      sessionid: 1,
      metas: [['foo', 1], ['bar', 10]]
    };
    mergeMetaIntoEvent(event);
    assert.equal(Object.keys(event).length, 4, 'correct number of object keys');
    assert.equal(event.metas, undefined, 'metas is gone');
    assert.equal(event.sessionid, undefined, 'sessionid is gone');
    assert.equal(event.foo, 1, 'foo flattened in');
    assert.equal(event.bar, 10, 'bar flattened in');
    assert.equal(event.time, 60000, 'time still there');
    assert.deepEqual(event.timeAsNumber, new Date(60000).getTime() / 1000, 'time added as date object');
  });
});