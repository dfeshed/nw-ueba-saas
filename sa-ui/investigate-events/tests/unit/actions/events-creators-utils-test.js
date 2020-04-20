import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';

import {
  mergeMetaIntoEvent
} from 'investigate-events/actions/events-creators-utils';

module('Unit | Actions | events-creators-utils', function(hooks) {
  setupTest(hooks);

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
