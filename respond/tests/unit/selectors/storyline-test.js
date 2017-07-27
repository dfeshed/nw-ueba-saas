import { module, test } from 'qunit';
import { storyEventCountExpected, selectedStoryEventCountExpected } from 'respond/selectors/storyline';

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

test('selectedStoryEventCountExpected returns the selection size when events are selected', function(assert) {
  const state = {
    respond: {
      incident: {
        selection: {
          type: 'event',
          ids: [ 'event1', 'event2' ]
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

  const result = selectedStoryEventCountExpected(state);
  assert.equal(result, 2, 'Expected the selection size to be returned');
});

test('selectedStoryEventCountExpected returns the sum of alert.numEvents when storyPoints are selected', function(assert) {
  const state = {
    respond: {
      incident: {
        selection: {
          type: 'storyPoint',
          ids: [ 'alert2', 'alert3' ]
        },
        storyline: [
          {
            id: 'alert1',
            alert: { numEvents: 10 }
          },
          {
            id: 'alert2',
            alert: { numEvents: 20 }
          },
          {
            id: 'alert3',
            alert: { numEvents: 30 }
          }
        ]
      }
    }
  };

  const result = selectedStoryEventCountExpected(state);
  assert.equal(result, 50, 'Expected the sum of numEvents of selected alerts to be returned');
});

test('selectedStoryEventCountExpected returns the sum of all alert.numEvents when nothing is selected', function(assert) {
  const state = {
    respond: {
      incident: {
        selection: {
          type: 'storyPoint',
          ids: []
        },
        storyline: [
          {
            id: 'alert1',
            alert: { numEvents: 10 }
          },
          {
            id: 'alert2',
            alert: { numEvents: 20 }
          },
          {
            id: 'alert3',
            alert: { numEvents: 30 }
          }
        ]
      }
    }
  };

  const result = selectedStoryEventCountExpected(state);
  assert.equal(result, 60, 'Expected the sum of numEvents of all alerts to be returned');
});