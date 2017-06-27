import { module, test } from 'qunit';
import { storyEventCountExpected } from 'respond/selectors/storyline';

module('Unit | Mixin | Storyline Selector');

test('storyEventCountExpected returns 0 if neither the incident info nor storyline has been loaded yet', function(assert) {

  const state = {
    respond: {
      incident: {
        info: null,
        storyline: null
      }
    }
  };

  const result = storyEventCountExpected(state);
  assert.equal(result, 0, 'Expected a zero result');
});

test('storyEventCountExpected returns the original eventCount when the storyline hasnt been loaded yet', function(assert) {

  const eventCount = 100;
  const state = {
    respond: {
      incident: {
        info: {
          eventCount
        },
        storyline: null
      }
    }
  };

  const result = storyEventCountExpected(state);
  assert.equal(result, eventCount, 'Expected the incident.info.eventCount to be returned');
});

test('storyEventCountExpected returns the sum of the indicators numEvents when the incident info hasnt been loaded yet', function(assert) {

  const state = {
    respond: {
      incident: {
        storyline: [
          {
            alert: { numEvents: 10 }
          },
          {
            alert: { numEvents: 20 }
          }
        ]
      }
    }
  };

  const result = storyEventCountExpected(state);
  assert.equal(result, 30, 'Expected the sum of each numEvents for every alert');
});

test('storyEventCountExpected returns the max result when two counts are available', function(assert) {

  const state = {
    respond: {
      incident: {
        info: {
          eventCount: 10
        },
        storyline: [
          {
            alert: { numEvents: 10 }
          },
          {
            alert: { numEvents: 20 }
          }
        ]
      }
    }
  };

  const result = storyEventCountExpected(state);
  assert.equal(result, 30, 'Expected the maximum result to be returned');
});

