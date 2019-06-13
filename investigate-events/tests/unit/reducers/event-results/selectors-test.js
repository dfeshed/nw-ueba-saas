import { module, test } from 'qunit';
import { lookup } from 'ember-dependency-lookup';
import {
  allExpectedDataLoaded,
  areEventsStreaming,
  eventResultsErrorMessage,
  eventType,
  getDownloadOptions,
  areAllEventsSelected,
  isCanceled,
  isEventResultsError,
  percentageOfEventsDataReturned,
  actualEventCount,
  noEvents,
  eventTableFormattingOpts,
  searchMatches,
  searchMatchesCount,
  eventTimeSortOrder,
  searchScrollDisplay,
  dataCount
} from 'investigate-events/reducers/investigate/event-results/selectors';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import CONFIG from 'investigate-events/reducers/investigate/config';
import EventColumnGroups from '../../../data/subscriptions/investigate-columns/data';

module('Unit | Selectors | event-results', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  const preferenceData = {
    eventAnalysisPreferences: CONFIG.defaultPreferences.eventAnalysisPreferences,
    eventsPreferencesConfig: CONFIG
  };

  const mixEventResultsData = [
    { sessionId: 1, medium: 1 },
    { sessionId: 2, medium: 1 },
    { sessionId: 3, medium: 32 }
  ];

  const withNetworkEvents = {
    status: 'stopped',
    selectedEventIds: { 1: 1, 2: 2 },
    data: mixEventResultsData
  };

  const withMixedEvents = {
    status: 'stopped',
    selectedEventIds: { 1: 1, 3: 3 },
    data: mixEventResultsData
  };

  const assertForDownloadOptions = async function(assert, result, optionNumber, eventDownloadType, fileType, nameString) {
    assert.equal(result[optionNumber].name.string, nameString, 'Option');
    assert.equal(result[optionNumber].eventDownloadType, eventDownloadType, 'Event Type');
    assert.equal(result[optionNumber].fileType, fileType, 'File Type');
  };

  test('dataCount', async function(assert) {
    assert.equal(dataCount({
      investigate: {
        eventResults: {
          data: [{}]
        }
      }
    }), 1);

    assert.equal(dataCount({
      investigate: {
        eventResults: {
          data: []
        }
      }
    }), 0);

    assert.equal(dataCount({
      investigate: {
        eventResults: {}
      }
    }), 0);
  });

  test('searchScrollDisplay', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          searchScrollIndex: 0
        }
      }
    };

    const result = searchScrollDisplay(state);
    assert.equal(result, 1);
  });

  test('searchMatches returns empty array with no searchTerm', async function(assert) {
    const state = {
      investigate: {
        dictionaries: {
          aliases: 'aliases'
        },
        data: {
          columnGroup: 'EMAIL',
          columnGroups: EventColumnGroups,
          globalPreferences: {
            dateFormat: 'dateFormat',
            timeFormat: 'timeFormat',
            timeZone: 'timeZone',
            locale: 'locale'
          }
        },
        eventResults: {
          searchTerm: '',
          data: [
            { sessionId: 1, medium: 32 },
            { sessionId: 2, medium: 32, 'nwe.callback_id': true },
            { sessionId: 3 }
          ]
        }
      }
    };

    const result = searchMatches(state);
    assert.equal(result.length, 0);
  });

  test('searchMatches returns empty array with a short searchTerm', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          searchTerm: 'a',
          data: [
            { sessionId: 1, medium: 32 },
            { sessionId: 2, medium: 32, 'nwe.callback_id': true },
            { sessionId: 3 }
          ]
        },
        dictionaries: {
          aliases: 'aliases'
        },
        data: {
          globalPreferences: {
            dateFormat: 'dateFormat',
            timeFormat: 'timeFormat',
            timeZone: 'timeZone',
            locale: 'locale'
          }
        }
      }
    };

    const result = searchMatches(state);
    assert.equal(result.length, 0);
  });

  test('searchMatches returns matches when not endpoint', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          searchTerm: 'log',
          visibleColumns: [{
            field: 'medium'
          }],
          data: [
            { sessionId: 1, medium: 32 }, // will resolve to "log"
            { sessionId: 2, medium: 32, 'nwe.callback_id': true }, // will resolve to "Endpoint"
            { sessionId: 3 }
          ]
        },
        dictionaries: {
          aliases: 'aliases'
        },
        data: {
          columnGroup: 'EMAIL',
          columnGroups: EventColumnGroups,
          globalPreferences: {
            dateFormat: 'dateFormat',
            timeFormat: 'timeFormat',
            timeZone: 'timeZone',
            locale: 'locale'
          }
        }
      }
    };

    const result = searchMatches(state);
    assert.equal(result.length, 1);
  });

  test('searchMatches returns no matches when not visibleColumns', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          searchTerm: 'log',
          visibleColumns: [{
            field: 'foo'
          }],
          data: [
            { sessionId: 1, medium: 32 }, // will resolve to "log"
            { sessionId: 2, medium: 32, 'nwe.callback_id': true }, // will resolve to "Endpoint"
            { sessionId: 3 }
          ]
        },
        dictionaries: {
          aliases: 'aliases'
        },
        data: {
          columnGroup: 'EMAIL',
          columnGroups: EventColumnGroups,
          globalPreferences: {
            dateFormat: 'dateFormat',
            timeFormat: 'timeFormat',
            timeZone: 'timeZone',
            locale: 'locale'
          }
        }
      }
    };

    const result = searchMatches(state);
    assert.equal(result.length, 0);
  });

  test('searchMatchesCount returns count of matches', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          searchTerm: 'log',
          visibleColumns: [{
            field: 'medium'
          }],
          data: [
            { sessionId: 1, medium: 32 }, // will resolve to "log"
            { sessionId: 3 }
          ]
        },
        dictionaries: {
          aliases: 'aliases'
        },
        data: {
          columnGroup: 'EMAIL',
          columnGroups: EventColumnGroups,
          globalPreferences: {
            dateFormat: 'dateFormat',
            timeFormat: 'timeFormat',
            timeZone: 'timeZone',
            locale: 'locale'
          }
        }
      }
    };

    const result = searchMatchesCount(state);
    assert.equal(result, 1);
  });

  test('searchMatches returns no matches when value not in column config', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          searchTerm: 'log',
          visibleColumns: [{
            field: 'medium'
          }],
          data: [
            { sessionId: 1, random: 32 }, // will resolve to "log"
            { sessionId: 2, medium: 32, 'nwe.callback_id': true }, // will resolve to "Endpoint"
            { sessionId: 3 }
          ]
        },
        dictionaries: {
          aliases: 'aliases'
        },
        data: {
          columnGroup: 'EMAIL',
          columnGroups: EventColumnGroups,
          globalPreferences: {
            dateFormat: 'dateFormat',
            timeFormat: 'timeFormat',
            timeZone: 'timeZone',
            locale: 'locale'
          }
        }
      }
    };

    const result = searchMatches(state);
    assert.equal(result.length, 0);
  });

  test('searchMatches returns matches when endpoint', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          searchTerm: 'end',
          visibleColumns: [{
            field: 'medium'
          }],
          data: [
            { sessionId: 1, medium: 32 }, // will resolve to "log"
            { sessionId: 2, medium: 32, 'nwe.callback_id': true }, // will resolve to "Endpoint"
            { sessionId: 3 }
          ]
        },
        dictionaries: {
          aliases: 'aliases'
        },
        data: {
          columnGroup: 'EMAIL',
          columnGroups: EventColumnGroups,
          globalPreferences: {
            dateFormat: 'dateFormat',
            timeFormat: 'timeFormat',
            timeZone: 'timeZone',
            locale: 'locale'
          }
        }
      }
    };

    const result = searchMatches(state);
    assert.equal(result.length, 1);
  });

  test('searchMatches returns matches when columnGroup is SUMMARY', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          searchTerm: 'foo',
          visibleColumns: [{
            field: 'medium'
          }],
          data: [
            { sessionId: 1, foo: 'foo' },
            { sessionId: 3 }
          ]
        },
        dictionaries: {
          aliases: 'aliases'
        },
        data: {
          columnGroup: 'SUMMARY',
          columnGroups: EventColumnGroups,
          globalPreferences: {
            dateFormat: 'dateFormat',
            timeFormat: 'timeFormat',
            timeZone: 'timeZone',
            locale: 'locale'
          }
        }
      }
    };

    const result = searchMatches(state);
    assert.equal(result.length, 1);
  });

  test('eventTableFormattingOpts returns expected format', async function(assert) {
    const state = {
      investigate: {
        dictionaries: {
          aliases: 'aliases'
        },
        data: {
          globalPreferences: {
            dateFormat: 'dateFormat',
            timeFormat: 'timeFormat',
            timeZone: 'timeZone',
            locale: 'locale'
          }
        }
      }
    };

    const i18n = lookup('service:i18n');
    const result = eventTableFormattingOpts(state);

    assert.deepEqual(result, {
      aliases: 'aliases',
      defaultWidth: 100,
      dateTimeFormat: 'dateFormat timeFormat',
      i18n: {
        size: {
          bytes: i18n.t('investigate.size.bytes'),
          KB: i18n.t('investigate.size.KB'),
          MB: i18n.t('investigate.size.MB'),
          GB: i18n.t('investigate.size.GB'),
          TB: i18n.t('investigate.size.TB')
        },
        medium: {
          '1': i18n.t('investigate.medium.network'),
          '32': i18n.t('investigate.medium.log'),
          '33': i18n.t('investigate.medium.correlation'),
          'endpoint': i18n.t('investigate.medium.endpoint'),
          'undefined': i18n.t('investigate.medium.undefined')
        }
      },
      locale: 'locale',
      timeZone: 'timeZone'
    });
  });

  const assertForCountsAndSessionIds = async function(assert, result, optionNumber, count, sessionIds, isDisabled) {
    assert.equal(result[optionNumber].count, count);
    assert.deepEqual(result[optionNumber].sessionIds, sessionIds);
    assert.equal(result[optionNumber].disabled, isDisabled);
  };

  test('areAllEventsSelected returns appropriate result', async function(assert) {
    const stateWithNoData = {
      investigate: {
        eventResults: {
          data: [],
          selectedEventIds: {}
        }
      }
    };
    const result = areAllEventsSelected(stateWithNoData);
    assert.notOk(result);
  });

  test('areAllEventsSelected returns appropriate result', async function(assert) {
    const stateWithSomeSelections = {
      investigate: {
        eventResults: {
          data: [{ sessionId: 1 }, { sessionId: 2 }],
          selectedEventIds: { 1: 1 }
        }
      }
    };
    const result = areAllEventsSelected(stateWithSomeSelections);
    assert.notOk(result);
  });

  test('areAllEventsSelected returns appropriate result', async function(assert) {
    const stateWithAllSelections = {
      investigate: {
        eventResults: {
          data: [{ sessionId: 1 }],
          selectedEventIds: { 1: 1 }
        }
      }
    };
    const result = areAllEventsSelected(stateWithAllSelections);
    assert.ok(result);
  });

  test('getDownloadOptions returns appropriate counts for options when network events are selected', async function(assert) {
    const state = {
      investigate: {
        data: preferenceData,
        eventResults: withNetworkEvents
      }
    };
    const result = getDownloadOptions(state);

    assert.equal(result.length, 2, '2 groups of options for download available');

    const defaultGroup = result[0].options;
    const otherGroup = result[1].options;
    assert.equal(defaultGroup.length, 3);
    assert.equal(otherGroup.length, 6);
    await assertForDownloadOptions(assert, defaultGroup, 0, 'LOG', 'TEXT', 'Logs as Text');
    await assertForDownloadOptions(assert, defaultGroup, 1, 'NETWORK', 'PCAP', 'Network as PCAP');
    await assertForDownloadOptions(assert, defaultGroup, 2, 'META', 'TEXT', 'Visible Meta as Text');
    await assertForDownloadOptions(assert, otherGroup, 0, 'LOG', 'CSV', 'Logs as CSV');
    await assertForDownloadOptions(assert, otherGroup, 3, 'META', 'CSV', 'Visible Meta as CSV');

    // preferred LOG option
    await assertForCountsAndSessionIds(assert, defaultGroup, 0, '0/2', [], true);
    // preferred Network option
    await assertForCountsAndSessionIds(assert, defaultGroup, 1, '2/2', [1, 2], false);
    // preffered Meta option
    await assertForCountsAndSessionIds(assert, defaultGroup, 2, '2/2', [1, 2], false);
  });

  test('getDownloadOptions returns appropriate counts for options when one each of log and network events are selected', async function(assert) {
    const state = {
      investigate: {
        data: preferenceData,
        eventResults: withMixedEvents
      }
    };
    const result = getDownloadOptions(state);

    assert.equal(result.length, 2, '2 groups of options for download available');

    const defaultGroup = result[0].options;
    // preferred LOG option
    await assertForCountsAndSessionIds(assert, defaultGroup, 0, '1/2', [3], false);
    // preferred Network option
    await assertForCountsAndSessionIds(assert, defaultGroup, 1, '1/2', [1], false);
    // preffered Meta option
    await assertForCountsAndSessionIds(assert, defaultGroup, 2, '2/2', [1, 3], false);
  });

  test('eventTimeSortOrder returns proper data', async function(assert) {
    let result = eventTimeSortOrder({
      investigate: {
        data: preferenceData
      }
    });
    assert.equal(result, 'Ascending', 'with data passes correct return');

    result = eventTimeSortOrder({
      investigate: {
        data: {
          eventAnalysisPreferences: null
        }
      }
    });
    assert.equal(result, 'Ascending', 'when no data passes correct return');
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

  test('noEvents is false when there are events', function(assert) {
    const state = {
      investigate: {
        eventResults: {
          data: [{}]
        }
      }
    };

    const result = noEvents(state);
    assert.ok(result === false);
  });

  test('noEvents is true when there are no events', function(assert) {
    const state = {
      investigate: {
        eventResults: {
          data: []
        }
      }
    };

    const result = noEvents(state);
    assert.ok(result === true);
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

  test('is correct event type determined', function(assert) {
    const eventResults = {
      data: [
        { sessionId: 1, medium: 32 },
        { sessionId: 2, medium: 32, 'nwe.callback_id': true },
        { sessionId: 3 }
      ]
    };
    const logState = {
      investigate: {
        queryNode: { sessionId: 1 },
        eventResults
      }
    };
    const endpointState = {
      investigate: {
        queryNode: { sessionId: 2 },
        eventResults
      }
    };
    const networkState = {
      investigate: {
        queryNode: { sessionId: 3 },
        eventResults
      }
    };

    assert.equal(eventType(logState), 'LOG', 'wrong type returned');
    assert.equal(eventType(endpointState), 'ENDPOINT', 'wrong type returned');
    assert.equal(eventType(networkState), 'NETWORK', 'wrong type returned');
  });

  test('actualEventCount returns the correct value', function(assert) {
    let state = {
      investigate: {
        eventResults: {
          status: 'stopped',
          data: [
            { sessionId: 1, medium: 32 },
            { sessionId: 2, 'nwe.callback_id': true },
            { sessionId: 3 }
          ]
        },
        eventCount: {
          data: 3
        }
      }
    };
    let actualCount = actualEventCount(state);
    assert.equal(actualCount, 3, 'This is the eventCount value');

    state = {
      investigate: {
        eventResults: {
          status: 'canceled',
          data: [
            { sessionId: 1, medium: 32 },
            { sessionId: 2, 'nwe.callback_id': true },
            { sessionId: 3 }
          ]
        },
        eventCount: {
          data: 5
        }
      }
    };
    actualCount = actualEventCount(state);
    assert.equal(actualCount, 3, 'This is the eventResults.data.length value when canceled');

    state = {
      investigate: {
        eventResults: {
          status: 'error',
          data: [
            { sessionId: 1, medium: 32 },
            { sessionId: 2, 'nwe.callback_id': true },
            { sessionId: 3 }
          ]
        },
        eventCount: {
          data: 5
        }
      }
    };
    actualCount = actualEventCount(state);
    assert.equal(actualCount, 3, 'This is the eventResults.data.length value when errored');
  });
});
