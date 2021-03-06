import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import {
  storyPointsWithEvents,
  storyEventCountExpected,
  selectedStoryEventCountExpected,
  getAlertsWithIndicatorId
} from 'respond/selectors/storyline';

module('Unit | Mixin | Storyline Selector', function(hooks) {
  setupTest(hooks);

  test('storyEventCountExpected returns 0 if neither the incident info nor storyline has been loaded yet', function(assert) {

    const state = {
      respond: {
        incident: {
          info: null
        },
        storyline: {
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
          }
        },
        storyline: {
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
        incident: {},
        storyline: {
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
          }
        },
        storyline: {
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
          }
        },
        storyline: {
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
          }
        },
        storyline: {
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
          }
        },
        storyline: {
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

  test('storyPointsWithEvents returns sum of storyline and events including isOpen boolean for child elements', function(assert) {
    const state = {
      respond: {
        incident: {
          selection: {
            type: 'storyPoint',
            ids: []
          }
        },
        storyline: {
          storyline: [
            {
              id: 'alert1',
              alert: { numEvents: 10 },
              items: [
                {
                  id: 'abc123'
                }
              ]
            },
            {
              id: 'alert2',
              alert: { numEvents: 20 }
            },
            {
              id: 'alert3',
              alert: { numEvents: 30 }
            }
          ],
          storylineEvents: [
            {
              indicatorId: 'alert1',
              events: [
                {
                  id: '586ecf95ecd25950034e1310',
                  description: 'IPIOC',
                  from: 'INENDEBS1L2C',
                  indicatorId: 'alert1'
                }
              ]
            },
            {
              indicatorId: 'alert2',
              events: []
            }
          ]
        }
      }
    };

    const result = storyPointsWithEvents(state);

    assert.deepEqual(result[0].events, [
      {
        description: 'IPIOC',
        from: 'INENDEBS1L2C',
        id: '586ecf95ecd25950034e1310',
        indicatorId: 'alert1'
      }
    ]);
    assert.deepEqual(result[0].indicator, {
      alert: {
        numEvents: 10
      },
      id: 'alert1',
      items: [
        {
          id: 'abc123'
        }
      ]
    });
    assert.equal(result[0].isOpen, false);
    assert.deepEqual(result[1].events, []);
    assert.deepEqual(result[1].indicator, {
      alert: {
        numEvents: 20
      },
      id: 'alert2'
    });
    assert.equal(result[1].isOpen, false);
    assert.deepEqual(result[2].indicator, {
      alert: {
        numEvents: 30
      },
      id: 'alert3'
    });
    assert.ok(result[2].isOpen);
  });

  test('getAlertsWithIndicatorId returns each alert with the associated indicator id', function(assert) {
    const state = {
      respond: {
        storyline: {
          storyline: [
            {
              id: 'alert1',
              alert: {
                name: 'one',
                severity: 10
              },
              storylineEvents: [
                {
                  indicatorId: 'alert1'
                }
              ]
            },
            {
              id: 'alert2',
              alert: {
                name: 'two',
                severity: 20
              },
              storylineEvents: [
                {
                  indicatorId: 'alert2'
                }
              ]
            },
            {
              id: 'alert3',
              alert: {
                name: 'three',
                severity: 30
              },
              storylineEvents: [
                {
                  indicatorId: 'alert3'
                }
              ]
            }
          ]
        }
      }
    };

    const result = getAlertsWithIndicatorId(state);

    assert.deepEqual(result, [
      {
        indicatorId: 'alert1',
        name: 'one',
        severity: 10
      },
      {
        indicatorId: 'alert2',
        name: 'two',
        severity: 20
      },
      {
        indicatorId: 'alert3',
        name: 'three',
        severity: 30
      }
    ]);
  });
});
