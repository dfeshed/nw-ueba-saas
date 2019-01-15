import { module, test } from 'qunit';
import {
  isCanceled,
  isEventResultsError,
  eventResultsErrorMessage,
  getDownloadOptions,
  areEventsStreaming,
  percentageOfEventsDataReturned,
  allExpectedDataLoaded,
  shouldStartAtOldest,
  mostRecentEvent
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

  const assertForDownloadOptions = async function(assert, result, optionNumber, eventType, fileType, nameString) {
    assert.equal(result[optionNumber].name.string, nameString, 'Option');
    assert.equal(result[optionNumber].eventType, eventType, 'Event Type');
    assert.equal(result[optionNumber].fileType, fileType, 'File Type');
  };

  const assertForCountsAndSessionIds = async function(assert, result, optionNumber, count, sessionIds, isDisabled) {
    assert.equal(result[optionNumber].count, count);
    assert.deepEqual(result[optionNumber].sessionIds, sessionIds);
    assert.equal(result[optionNumber].disabled, isDisabled);
  };

  test('getDownloadOptions returns options with no counts when selectAll is checked', async function(assert) {
    const state = {
      investigate: {
        data: preferenceData,
        eventResults: withAllEventsSelected
      }
    };
    const result = getDownloadOptions(state);
    // number of options in download as per the number of preferences x number of options per preference
    assert.equal(result.length, 12, '12 options for download available');
    await assertForDownloadOptions(assert, result, 0, 'LOG', 'LOG', 'Logs as Log');
    await assertForDownloadOptions(assert, result, 1, 'NETWORK', 'PCAP', 'Network as PCAP');
    await assertForDownloadOptions(assert, result, 2, 'META', 'TEXT', 'Visible Meta as Text');
    await assertForDownloadOptions(assert, result, 3, 'LOG', 'CSV', 'Logs as CSV');
    await assertForDownloadOptions(assert, result, 6, 'NETWORK', 'PAYLOAD', 'Network as All Payloads');
    await assertForDownloadOptions(assert, result, 9, 'META', 'CSV', 'Visible Meta as CSV');
    // preferred LOG option
    await assertForCountsAndSessionIds(assert, result, 0, '', [], false);
    // preferred Network option
    await assertForCountsAndSessionIds(assert, result, 1, '', [], false);
    // preffered Meta option
    await assertForCountsAndSessionIds(assert, result, 2, '', [], false);
  });

  // TODO add assert to check disabled
  test('getDownloadOptions returns appropriate counts for options when network events are selected', async function(assert) {
    const state = {
      investigate: {
        data: preferenceData,
        eventResults: withNetworkEvents
      }
    };
    const result = getDownloadOptions(state);
    // preferred LOG option
    await assertForCountsAndSessionIds(assert, result, 0, '0/2', [], true);
    // preferred Network option
    await assertForCountsAndSessionIds(assert, result, 1, '2/2', [1, 2], false);
    // preffered Meta option
    await assertForCountsAndSessionIds(assert, result, 2, '2/2', [1, 2], false);
  });

  test('getDownloadOptions returns appropriate counts for options when one each of log and network events are selected', async function(assert) {
    const state = {
      investigate: {
        data: preferenceData,
        eventResults: withMixedEvents
      }
    };
    const result = getDownloadOptions(state);
    // preferred LOG option
    await assertForCountsAndSessionIds(assert, result, 0, '1/2', [3], false);
    // preferred Network option
    await assertForCountsAndSessionIds(assert, result, 1, '1/2', [1], false);
    // preffered Meta option
    await assertForCountsAndSessionIds(assert, result, 2, '2/2', [1, 3], false);
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

  test('areEventsStreaming correctly determines if events are streaming', function(assert) {
    let state = {
      investigate: {
        eventResults: {
          status: 'streaming'
        }
      }
    };
    let areThey = areEventsStreaming(state);
    assert.equal(areThey, true, "streaming when 'streaming'");

    state = {
      investigate: {
        eventResults: {
          status: 'between-streams'
        }
      }
    };
    areThey = areEventsStreaming(state);
    assert.equal(areThey, true, "streaming when 'between-streams'");

    state = {
      investigate: {
        eventResults: {
          status: 'complete'
        }
      }
    };
    areThey = areEventsStreaming(state);
    assert.equal(areThey, false, "not streaming when 'complete'");
  });

  test('mostRecentEvent should get the correct event', function(assert) {
    let state = {
      investigate: {
        eventResults: {
          eventTimeSortOrder: 'Descending',
          data: eventResultsData
        }
      }
    };
    let event = mostRecentEvent(state);
    assert.equal(event.sessionId, 1, 'should get first event');

    state = {
      investigate: {
        eventResults: {
          eventTimeSortOrder: 'Ascending',
          data: eventResultsData
        }
      }
    };
    event = mostRecentEvent(state);
    assert.equal(event.sessionId, 3, 'should get first event');
  });

  test('shouldStartAtOldest correctly handles preference data', function(assert) {
    let state = {
      investigate: {
        eventResults: {
          eventResultSetStart: 'Oldest'
        }
      }
    };
    let shouldI = shouldStartAtOldest(state);
    assert.equal(shouldI, true, 'should start at oldest');

    state = {
      investigate: {
        eventResults: {
          eventResultSetStart: 'Newest'
        }
      }
    };
    shouldI = shouldStartAtOldest(state);
    assert.equal(shouldI, false, 'should not start at oldest');

    state = {
      investigate: {
        eventResults: {
          eventResultSetStart: 'poop'
        }
      }
    };
    shouldI = shouldStartAtOldest(state);
    assert.equal(shouldI, false, 'should not start at oldest');
  });

  test('percentageOfEventsDataReturned returns correct percentage', function(assert) {
    let state = {
      investigate: {
        eventResults: {
          status: undefined,
          data: [],
          streamLimit: 100
        },
        eventCount: {
          data: 100
        }
      }
    };
    let percentage = percentageOfEventsDataReturned(state);
    assert.equal(percentage, 0, 'no status gives 0');

    state = {
      investigate: {
        eventResults: {
          status: 'complete',
          data: [],
          streamLimit: 100
        },
        eventCount: {
          data: 100
        }
      }
    };
    percentage = percentageOfEventsDataReturned(state);
    assert.equal(percentage, 100, 'complete gives 100 percent');

    state = {
      investigate: {
        eventResults: {
          status: 'streaming',
          data: undefined,
          streamLimit: 100
        },
        eventCount: {
          data: 100
        }
      }
    };
    percentage = percentageOfEventsDataReturned(state);
    assert.equal(percentage, 0, 'empty data gives 0');

    state = {
      investigate: {
        eventResults: {
          status: 'streaming',
          data: [],
          streamLimit: 100
        },
        eventCount: {
          data: 100
        }
      }
    };
    percentage = percentageOfEventsDataReturned(state);
    assert.equal(percentage, 0, 'empty data gives 0');

    state = {
      investigate: {
        eventResults: {
          status: 'streaming',
          data: [1, 2, 3, 4, 5],
          streamLimit: 100
        },
        eventCount: {
          data: undefined
        }
      }
    };
    percentage = percentageOfEventsDataReturned(state);
    assert.equal(percentage, 5, 'correct percentage returned');


    state = {
      investigate: {
        eventResults: {
          status: 'streaming',
          data: [1, 2, 3, 4, 5],
          streamLimit: 100
        },
        eventCount: {
          data: 10
        }
      }
    };
    percentage = percentageOfEventsDataReturned(state);
    assert.equal(percentage, 50, 'correct percentage returned');
  });

  test('percentageOfEventsDataReturned returns correct percentage', function(assert) {
    let state = {
      investigate: {
        eventResults: {
          status: 'streaming',
          data: [],
          streamLimit: 100
        },
        eventCount: {
          data: 100
        }
      }
    };
    let hasItAllLoaded = allExpectedDataLoaded(state);
    assert.equal(hasItAllLoaded, false, 'if not completed, not all loaded');

    state = {
      investigate: {
        eventResults: {
          status: 'complete',
          data: [1, 2, 3],
          streamLimit: 100
        },
        eventCount: {
          data: 3
        }
      }
    };
    hasItAllLoaded = allExpectedDataLoaded(state);
    assert.equal(hasItAllLoaded, true, 'not all loaded, 5 vs 3');

    state = {
      investigate: {
        eventResults: {
          status: 'complete',
          data: [1, 2, 3],
          streamLimit: 3
        },
        eventCount: {
          data: 3
        }
      }
    };
    hasItAllLoaded = allExpectedDataLoaded(state);
    assert.equal(hasItAllLoaded, false, 'at the limit, not all loaded');

    state = {
      investigate: {
        eventResults: {
          status: 'complete',
          data: [1, 2, 3],
          streamLimit: 10
        },
        eventCount: {
          data: 3
        }
      }
    };
    hasItAllLoaded = allExpectedDataLoaded(state);
    assert.equal(hasItAllLoaded, true, 'got it all, not at the limit');
  });

  test('determines if a query has been canceled', function(assert) {
    const state = {
      investigate: {
        eventResults: {
          status: 'canceled'
        }
      }
    };
    assert.ok(isCanceled(state), 'should have returned "true"');

    const state2 = {
      investigate: {
        eventResults: {
          status: 'complete'
        }
      }
    };
    assert.notOk(isCanceled(state2), 'should have returned "false"');
  });
});

