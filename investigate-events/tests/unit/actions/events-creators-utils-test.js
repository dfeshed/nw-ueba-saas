import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

import {
  calculateNewStartForNextBatch,
  calculateNextStartTimeAfterFailure,
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


    // 75.78 milliseconds/event, (15000 - 11211) / 50
    // target is 25 events
    // 1894.5 milliseconds, 75.78 * 25
    // 9316.5, 11211 - 1894.5
    // is rounded because does not divide evenly by 60
    result = calculateNewStartForNextBatch(
      11211, 11212, 90000, maxEvents
    );
    assert.equal(result, 11209, 'calculated correctly, is previous begin date minus 1 + minus 1');
  });

  test('calculateNextStartTimeAfterFailure calculations', function(assert) {

    const binarySearchData = {
      tooMany: 0,
      noResults: 0
    };

    let newTime = calculateNextStartTimeAfterFailure(100000, 120000, binarySearchData, true);
    // cut in half then rounded down to even minutes
    assert.equal(newTime, 110040, 'gap cut in half and rounded to a minute');
    assert.equal(binarySearchData.tooMany, 20000, 'tracking too many');
    assert.equal(binarySearchData.noResults, 0, 'tracking too many');

    // new check, when immediately 0 results
    binarySearchData.tooMany = 0;
    binarySearchData.noResults = 0;
    newTime = calculateNextStartTimeAfterFailure(100000, 120000, binarySearchData, false);
    assert.equal(newTime, 80040, 'gap doubled');
    assert.equal(binarySearchData.tooMany, 0, 'tracking too many');
    assert.equal(binarySearchData.noResults, 20000, 'tracking too many');

    binarySearchData.tooMany = 100;
    binarySearchData.noResults = 110;
    newTime = calculateNextStartTimeAfterFailure(9895, 10000, binarySearchData, true);
    assert.equal(newTime, 9892, 'gap shrunk, not rounded');
    assert.equal(binarySearchData.tooMany, 105, 'tracking too many');
    assert.equal(binarySearchData.noResults, 110, 'tracking too many');

    binarySearchData.tooMany = 100;
    binarySearchData.noResults = 110;
    newTime = calculateNextStartTimeAfterFailure(9895, 10000, binarySearchData, false);
    assert.equal(newTime, 9897, 'gap shrunk, not rounded');
    assert.equal(binarySearchData.tooMany, 100, 'tracking too many');
    assert.equal(binarySearchData.noResults, 105, 'tracking too many');

  });

  test('mergeMetaIntoEvent stuffs', function(assert) {
    let event = {
      foo: 1,
      bar: 2
    };
    mergeMetaIntoEvent(false)(event);
    assert.equal(Object.keys(event).length, 2, 'same two keys');
    assert.equal(event.foo, 1, 'same two keys');
    assert.equal(event.bar, 2, 'same two keys');

    event = {
      time: 60000,
      sessionid: 1,
      metas: [['foo', 1], ['bar', 10], ['bar', 5]]
    };
    mergeMetaIntoEvent(false)(event);
    assert.equal(Object.keys(event).length, 4, 'correct number of object keys');
    assert.equal(event.metas, undefined, 'metas is gone');
    assert.equal(event.sessionid, undefined, 'sessionid is gone');
    assert.equal(event.foo, 1, 'foo flattened in');
    assert.equal(event.bar, 10, 'first bar flattened in');
    assert.equal(event.time, 60000, 'time still there');
    assert.deepEqual(event.timeAsNumber, new Date(60000).getTime() / 1000, 'time added as date object');

    event = {
      time: 60000,
      sessionid: 1,
      metas: [['foo', 1], ['bar', 10]]
    };
    mergeMetaIntoEvent(true)(event);
    assert.equal(event.sessionid, 1, 'sessionid is still there');
  });
});
