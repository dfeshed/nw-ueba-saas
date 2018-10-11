import { module, test } from 'qunit';
import {
  isEventResultsError,
  eventResultsErrorMessage,
  getNextPayloadSize,
  getDownloadOptions
} from 'investigate-events/reducers/investigate/event-results/selectors';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import CONFIG from 'investigate-events/reducers/investigate/config';

module('Unit | Selectors | event-results', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  const preferenceData = {
    eventAnalysisPreferences: CONFIG.defaultPreferences.eventAnalysisPreferences,
    eventsPreferencesConfig: CONFIG
  };

  const eventResultsData = [
    { sessionId: 1, medium: 1 },
    { sessionId: 2, medium: 1 },
    { sessionId: 3, medium: 32 }
  ];

  const withAllEventsSelected = {
    status: 'stopped',
    allEventsSelected: true,
    selectedEventIds: [],
    data: eventResultsData
  };

  const withNetworkEvents = {
    status: 'stopped',
    allEventsSelected: false,
    selectedEventIds: [1, 2],
    data: eventResultsData
  };

  const withMixedEvents = {
    status: 'stopped',
    allEventsSelected: false,
    selectedEventIds: [1, 3],
    data: eventResultsData
  };

  test('getDownloadOptions returns options', function(assert) {
    const state = {
      investigate: {
        data: preferenceData,
        eventResults: withAllEventsSelected
      }
    };
    const result = getDownloadOptions(state);
    // number of options in download as per the number of preferences x number of options per preference
    // TODO change when more options from remaining preferences are added
    assert.equal(result.length, 3, '3 options for download available');
    assert.equal(result[0].name.string, 'Logs as LOG', 'Logs download option');
    assert.equal(result[1].name.string, 'Visible Meta as TEXT', 'Meta download option');
    assert.equal(result[2].name.string, 'Network as PCAP', 'Network download option');
  });
  // TODO add assert to check disabled
  test('getDownloadOptions returns appropriate counts for options when network events are selected', function(assert) {
    const state = {
      investigate: {
        data: preferenceData,
        eventResults: withNetworkEvents
      }
    };
    const result = getDownloadOptions(state);
    // number of options in download as per the number of preferences x number of options per preference
    // TODO change/add when more options from remaining preferences are added
    assert.equal(result[0].count, '', 'No log event selected');
    assert.equal(result[1].count, '2/2', '2 Meta events selected');
    assert.equal(result[2].count, '2/2', '2 Network events selected');
  });

  test('getDownloadOptions returns appropriate counts for options when one each of log and network events are selected', function(assert) {
    const state = {
      investigate: {
        data: preferenceData,
        eventResults: withMixedEvents
      }
    };
    const result = getDownloadOptions(state);
    // number of options in download as per the number of preferences x number of options per preference
    // TODO change/add when more options from remaining preferences are added
    assert.equal(result[0].count, '1/2', '1 log event selected');
    assert.equal(result[1].count, '2/2', '2 Meta events selected');
    assert.equal(result[2].count, '1/2', '1 Network event selected');
  });

  test('isEventResultsError is false when status is not error', function(assert) {
    const state = {
      investigate: {
        eventResults: {
          status: 'foo'
        }
      }
    };

    const isError = isEventResultsError(state);
    assert.ok(isError === false, 'is not in error');
  });

  test('isEventResultsError is true when status is error', function(assert) {
    const state = {
      investigate: {
        eventResults: {
          status: 'error'
        }
      }
    };

    const isError = isEventResultsError(state);
    assert.ok(isError === true, 'error message');
  });

  test('eventResultsErrorMessage returns error message', function(assert) {
    const state = {
      investigate: {
        eventResults: {
          message: 'fooooooo'
        }
      }
    };

    const errorMessage = eventResultsErrorMessage(state);
    assert.ok(errorMessage === 'fooooooo', 'error message');
  });

  test('it pulls out correct formatted error message - type 1', function(assert) {
    const state = {
      investigate: {
        eventResults: {
          message: 'rule syntax error: expecting <unary operator> or <relational operator> here: "does 45454 && time="2018-04-09 15:48:00" - "2018-04-10 15:47:59""'
        }
      }
    };

    const formattedErrorMessage = eventResultsErrorMessage(state);
    assert.equal(formattedErrorMessage, 'syntax error: expecting <unary operator> or <relational operator> here: "does 45454 ', 'Correct error message');
  });

  test('it pulls out correct formatted error message - type 2', function(assert) {
    const state = {
      investigate: {
        eventResults: {
          message: 'rule syntax error: unrecognized key something'
        }
      }
    };

    const formattedErrorMessage = eventResultsErrorMessage(state);
    assert.equal(formattedErrorMessage, 'syntax error: unrecognized key something', 'Expected error message');
  });

  test('determines and returns the original message if not type 1 or 2', function(assert) {
    const state = {
      investigate: {
        eventResults: {
          message: 'The language key "someweirdtextishere" exceeds the maximum size of 16'
        }
      }
    };

    const formattedErrorMessage = eventResultsErrorMessage(state);
    assert.equal(formattedErrorMessage, 'The language key "someweirdtextishere" exceeds the maximum size of 16', 'Expected error message');
  });

  test('fetches next events payload size correctly', function(assert) {

    const state0 = {
      investigate: {
        eventResults: {
          goal: 200,
          streamGoal: 100,
          status: 'stopped'
        },
        eventCount: {
          data: 330
        }
      }
    };

    const state1 = {
      investigate: {
        eventResults: {
          goal: 300,
          streamGoal: 100,
          status: 'stopped'
        },
        eventCount: {
          data: 330
        }
      }
    };

    const state2 = {
      investigate: {
        eventResults: {
          goal: 400,
          streamGoal: 100,
          status: 'stopped'
        },
        eventCount: {
          data: 330
        }
      }
    };

    const intermediatePayloadSize = getNextPayloadSize(state0);
    assert.equal(intermediatePayloadSize, 100, 'Intermediate events payload size is equal to streamGoal size');

    const lastPayloadSize = getNextPayloadSize(state1);
    assert.equal(lastPayloadSize, 30, 'Last events payload size is 30');

    const nextToLastPayloadSize = getNextPayloadSize(state2);
    assert.equal(nextToLastPayloadSize, 0, 'Next to last events payload size is 0');
  });
});
