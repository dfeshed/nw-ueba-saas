import { module, test, skip } from 'qunit';
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
  dataCount,
  clientSortedData,
  requireServiceSorting,
  groupForSortAscending,
  groupForSortDescending,
  hideEventsForReQuery,
  nestChildEvents,
  updateStreamKeyTree,
  eventsHaveSplits,
  eventResultSetStart
} from 'investigate-events/reducers/investigate/event-results/selectors';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import CONFIG from 'investigate-events/reducers/investigate/config';
import EventColumnGroups from '../../../data/subscriptions/column-group';
import { mapColumnGroupsForEventTable } from 'investigate-events/util/mapping';

// TODO: unskip tests for 11.4.1 when intrasession events is turned back on
module('Unit | Selectors | event-results', function(hooks) {

  let mappedColumnGroups;

  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  hooks.before(function() {
    mappedColumnGroups = mapColumnGroupsForEventTable(EventColumnGroups);
  });

  const preferenceData = {
    eventAnalysisPreferences: CONFIG.defaultPreferences.eventAnalysisPreferences,
    eventsPreferencesConfig: CONFIG
  };

  const mixEventResultsData = [
    { sessionId: 101, medium: 1, foo: 3 },
    { sessionId: 102, medium: 1, foo: 1 },
    { sessionId: 103, medium: 32, foo: 2 }
  ];

  const withNetworkEvents = {
    status: 'stopped',
    selectedEventIds: { 0: 101, 1: 102 },
    data: mixEventResultsData
  };

  const withMixedEvents = {
    status: 'stopped',
    selectedEventIds: { 0: 102, 1: 103, 2: 101 },
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

  test('eventResultSetStart', async function(assert) {
    assert.notOk(eventResultSetStart({
      investigate: {
        eventCount: {
          data: 1,
          threshold: 2
        },
        data: {
          sortDirection: 'Ascending',
          sortField: 'time'
        }
      }
    }));

    assert.notOk(eventResultSetStart({
      investigate: {
        eventCount: {
          data: 1,
          threshold: 2
        },
        data: {
          sortDirection: 'Ascending',
          sortField: 'foo'
        }
      }
    }));

    assert.equal('oldest', eventResultSetStart({
      investigate: {
        eventCount: {
          data: 1,
          threshold: 1
        },
        data: {
          sortDirection: 'Ascending',
          sortField: 'time'
        }
      }
    }));

    assert.equal('newest', eventResultSetStart({
      investigate: {
        eventCount: {
          data: 1,
          threshold: 1
        },
        data: {
          sortDirection: 'Descending',
          sortField: 'time'
        }
      }
    }));

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

  test('hideEventsForReQuery', async function(assert) {
    assert.equal(hideEventsForReQuery({
      investigate: {
        eventResults: {
          status: 'streaming'
        },
        data: {
          isQueryExecutedBySort: false,
          isQueryExecutedByColumnGroup: false
        }
      }
    }), false);

    assert.equal(hideEventsForReQuery({
      investigate: {
        eventResults: {
          status: 'sorting'
        },
        data: {
          isQueryExecutedBySort: false,
          isQueryExecutedByColumnGroup: false
        }
      }
    }), false);

    assert.equal(hideEventsForReQuery({
      investigate: {
        eventResults: {
          status: 'foo'
        },
        data: {
          isQueryExecutedBySort: false,
          isQueryExecutedByColumnGroup: false
        }
      }
    }), false);

    assert.equal(hideEventsForReQuery({
      investigate: {
        eventResults: {
          status: 'loading'
        },
        data: {
          isQueryExecutedBySort: true,
          isQueryExecutedByColumnGroup: false
        }
      }
    }), true);

    assert.equal(hideEventsForReQuery({
      investigate: {
        eventResults: {
          status: 'loading'
        },
        data: {
          isQueryExecutedBySort: false,
          isQueryExecutedByColumnGroup: true
        }
      }
    }), true);
  });

  test('searchMatches returns empty array with no searchTerm', async function(assert) {
    const state = {
      investigate: {
        dictionaries: {
          language: [{ metaName: 'size', format: 'Int' }],
          languageCache: {},
          aliases: 'aliases',
          aliasesCache: {}
        },
        data: {
          selectedColumnGroup: 'EMAIL'
        },
        columnGroup: {
          columnGroups: mappedColumnGroups,
          globalPreferences: {
            dateFormat: 'dateFormat',
            timeFormat: 'timeFormat',
            timeZone: 'timeZone',
            locale: 'locale'
          }
        },
        services: {
          serviceData: [{ version: 11.4 }]
        },
        eventCount: {
          data: 3
        },
        eventResults: {
          searchTerm: '',
          data: [
            { sessionId: 1, medium: 32 },
            { sessionId: 2, medium: 32, 'nwe.callback_id': true },
            { sessionId: 3 }
          ]
        },
        queryNode: {
          previousQueryParams: {
            serviceId: 1
          }
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
          language: [{ metaName: 'size', format: 'Int' }],
          languageCache: {},
          aliases: 'aliases',
          aliasesCache: {}
        },
        services: {
          serviceData: [{ version: 11.4 }]
        },
        data: {
          globalPreferences: {
            dateFormat: 'dateFormat',
            timeFormat: 'timeFormat',
            timeZone: 'timeZone',
            locale: 'locale'
          }
        },
        eventCount: {
          data: 3
        },
        columnGroup: {
        },
        queryNode: {
          previousQueryParams: {
            serviceId: 1
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
          language: [{ metaName: 'size', format: 'Int' }],
          languageCache: {},
          aliases: 'aliases',
          aliasesCache: {}
        },
        services: {
          serviceData: [{ version: 11.4 }]
        },
        eventCount: {
          data: 3
        },
        data: {
          selectedColumnGroup: 'EMAIL',
          globalPreferences: {
            dateFormat: 'dateFormat',
            timeFormat: 'timeFormat',
            timeZone: 'timeZone',
            locale: 'locale'
          }
        },
        columnGroup: {
          columnGroups: mappedColumnGroups
        },
        queryNode: {
          previousQueryParams: {
            serviceId: 1
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
          language: [{ metaName: 'size', format: 'Int' }],
          languageCache: {},
          aliases: 'aliases',
          aliasesCache: {}
        },
        eventCount: {
          data: 3
        },
        services: {
          serviceData: [{ version: 11.4 }]
        },
        data: {
          selectedColumnGroup: 'EMAIL',
          globalPreferences: {
            dateFormat: 'dateFormat',
            timeFormat: 'timeFormat',
            timeZone: 'timeZone',
            locale: 'locale'
          }
        },
        columnGroup: {
          columnGroups: mappedColumnGroups
        },
        queryNode: {
          previousQueryParams: {
            serviceId: 1
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
          language: [{ metaName: 'size', format: 'Int' }],
          languageCache: {},
          aliases: 'aliases',
          aliasesCache: {}
        },
        services: {
          serviceData: [{ version: 11.4 }]
        },
        eventCount: {
          data: 3
        },
        data: {
          selectedColumnGroup: 'EMAIL',
          globalPreferences: {
            dateFormat: 'dateFormat',
            timeFormat: 'timeFormat',
            timeZone: 'timeZone',
            locale: 'locale'
          }
        },
        columnGroup: {
          columnGroups: mappedColumnGroups
        },
        queryNode: {
          previousQueryParams: {
            serviceId: 1
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
          language: [{ metaName: 'size', format: 'Int' }],
          languageCache: {},
          aliases: 'aliases',
          aliasesCache: {}
        },
        services: {
          serviceData: [{ version: 11.4 }]
        },
        eventCount: {
          data: 3
        },
        data: {
          selectedColumnGroup: 'EMAIL',
          globalPreferences: {
            dateFormat: 'dateFormat',
            timeFormat: 'timeFormat',
            timeZone: 'timeZone',
            locale: 'locale'
          }
        },
        columnGroup: {
          columnGroups: mappedColumnGroups
        },
        queryNode: {
          previousQueryParams: {
            serviceId: 1
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
          language: [{ metaName: 'size', format: 'Int' }],
          languageCache: {},
          aliases: 'aliases',
          aliasesCache: {}
        },
        services: {
          serviceData: [{ version: 11.4 }]
        },
        eventCount: {
          data: 3
        },
        data: {
          selectedColumnGroup: 'EMAIL',
          globalPreferences: {
            dateFormat: 'dateFormat',
            timeFormat: 'timeFormat',
            timeZone: 'timeZone',
            locale: 'locale'
          }
        },
        columnGroup: {
          columnGroups: mappedColumnGroups
        },
        queryNode: {
          previousQueryParams: {
            serviceId: 1
          }
        }
      }
    };

    const result = searchMatches(state);
    assert.equal(result.length, 1);
  });

  test('searchMatches returns matches when selectedColumnGroup is SUMMARY', async function(assert) {
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
        services: {
          serviceData: [{ version: 11.4 }]
        },
        dictionaries: {
          language: [{ metaName: 'size', format: 'Int' }],
          languageCache: {},
          aliases: 'aliases',
          aliasesCache: {}
        },
        eventCount: {
          data: 3
        },
        data: {
          selectedColumnGroup: 'SUMMARY',
          globalPreferences: {
            dateFormat: 'dateFormat',
            timeFormat: 'timeFormat',
            timeZone: 'timeZone',
            locale: 'locale'
          }
        },
        columnGroup: {
          columnGroups: mappedColumnGroups
        },
        queryNode: {
          previousQueryParams: {
            serviceId: 1
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
          language: [{ metaName: 'size', format: 'Int' }],
          languageCache: {},
          aliases: 'aliases',
          aliasesCache: {}
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
        eventCount: {},
        dictionaries: {},
        services: {},
        data: preferenceData,
        eventResults: withNetworkEvents,
        queryNode: {
          previousQueryParams: {}
        }
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
    await assertForCountsAndSessionIds(assert, defaultGroup, 1, '2/2', [101, 102], false);
    // preffered Meta option
    await assertForCountsAndSessionIds(assert, defaultGroup, 2, '2/2', [101, 102], false);
  });

  test('getDownloadOptions returns appropriate counts for options when one each of log and network events are selected', async function(assert) {
    const state = {
      investigate: {
        eventCount: {
          threshold: 4,
          data: 3
        },
        dictionaries: {
          language: [{
            metaName: 'foo',
            format: 'Int'
          }]
        },
        services: {},
        data: {
          ...preferenceData,
          sortField: 'foo',
          sortDirection: 'Ascending',
          globalPreferences: {
          }
        },
        eventResults: withMixedEvents,
        queryNode: {
          previousQueryParams: {}
        }
      }
    };
    const result = getDownloadOptions(state);

    assert.equal(result.length, 2, '2 groups of options for download available');

    const defaultGroup = result[0].options;
    // preferred LOG option
    await assertForCountsAndSessionIds(assert, defaultGroup, 0, '1/3', [103], false);
    // preferred Network option with session Ids in order of clientSorted data
    await assertForCountsAndSessionIds(assert, defaultGroup, 1, '2/3', [102, 101], false);
    // preffered Meta option
    await assertForCountsAndSessionIds(assert, defaultGroup, 2, '3/3', [102, 103, 101], false);
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
        data: {},
        dictionaries: {},
        eventCount: {},
        services: {},
        eventResults
      }
    };
    const endpointState = {
      investigate: {
        queryNode: { sessionId: 2 },
        data: {},
        dictionaries: {},
        eventCount: {},
        services: {},
        eventResults
      }
    };
    const networkState = {
      investigate: {
        queryNode: { sessionId: 3 },
        data: {},
        dictionaries: {},
        eventCount: {},
        services: {},
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

  test('requireServiceSorting', async function(assert) {
    assert.deepEqual(requireServiceSorting({
      investigate: {
        eventCount: {
          data: 5,
          threshold: 5
        },
        services: {
          serviceData: [{ version: 11.4 }]
        }
      }
    }), true);

    assert.deepEqual(requireServiceSorting({
      investigate: {
        eventCount: {
          data: 5,
          threshold: 4
        },
        services: {
          serviceData: [{ version: 11.4 }]
        }
      }
    }), false);

    assert.deepEqual(requireServiceSorting({
      investigate: {
        eventCount: {
          data: 5,
          threshold: 5
        },
        services: {
          serviceData: [{ version: 11.3 }]
        }
      }
    }), false);
  });

  test('groupForSortAscending', async function(assert) {
    assert.equal(groupForSortAscending({ toSort: 2 }), 1);
    assert.equal(groupForSortAscending({ toSort: 'a' }), -1);
  });

  test('groupForSortDescending', async function(assert) {
    assert.equal(groupForSortDescending({ toSort: 2 }), -1);
    assert.equal(groupForSortDescending({ toSort: 'a' }), 1);
  });

  test('clientSortedData when no data', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          data: null
        },
        data: {
          sortField: 'time',
          sortDirection: null,
          globalPreferences: {
          }
        },
        dictionaries: {
          language: [{
            metaName: 'foo',
            format: 'IPv4'
          }]
        },
        eventCount: {},
        services: {},
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = clientSortedData(state);
    assert.deepEqual(result, state.investigate.eventResults.data);
  });

  test('clientSortedData when no languages', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          data: [{ foo: 'foo' }]
        },
        data: {
          sortField: 'time',
          sortDirection: null,
          globalPreferences: {
          }
        },
        eventCount: {},
        dictionaries: {},
        services: {},
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = clientSortedData(state);
    assert.deepEqual(result, state.investigate.eventResults.data);
  });

  test('clientSortedData when data, but wrong version', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          data: [{ foo: 'foo' }]
        },
        data: {
          sortField: 'time',
          sortDirection: null,
          globalPreferences: {
          }
        },
        dictionaries: {
          language: [{
            metaName: 'foo',
            format: 'IPv4'
          }]
        },
        eventCount: {
          threshold: 1,
          data: 1
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = clientSortedData(state);
    assert.deepEqual(result, state.investigate.eventResults.data);
  });

  test('clientSortedData when IPv4', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          data: [
            { foo: '0.0.0.1' },
            { foo: '50.0.0.2' },
            { foo: '128.0.0.3' }
          ]
        },
        data: {
          sortField: 'foo',
          sortDirection: 'Descending',
          globalPreferences: {
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [{
            metaName: 'foo',
            format: 'IPv4'
          }]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = clientSortedData(state);
    assert.equal(result[0].foo, state.investigate.eventResults.data[2].foo);
    assert.equal(result[1].foo, state.investigate.eventResults.data[1].foo);
    assert.equal(result[2].foo, state.investigate.eventResults.data[0].foo);
  });


  test('clientSortedData when IPv6 when Descending', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          data: [
            { foo: '2000:0db8:85a3:0000:0000:8a2e:0370:7331' },
            { foo: null },
            { foo: '3001:0db8:85a3:0000:0000:8a2e:0370:7333' },
            { foo: '1001:0db8:85a3:0000:0000:8a2e:0370:7337' },
            { foo: '5001:0db8:85a3:0000:0000:8a2e:0370:0' }
          ]
        },
        data: {
          sortField: 'foo',
          sortDirection: 'Descending',
          globalPreferences: {
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [{
            metaName: 'foo',
            format: 'IPv6'
          }]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = clientSortedData(state);
    assert.equal(result[0].foo, state.investigate.eventResults.data[4].foo);
    assert.equal(result[1].foo, state.investigate.eventResults.data[2].foo);
    assert.equal(result[2].foo, state.investigate.eventResults.data[0].foo);
    assert.equal(result[3].foo, state.investigate.eventResults.data[3].foo);
    assert.equal(result[4].foo, state.investigate.eventResults.data[1].foo);
  });

  test('clientSortedData when IPv6 when Ascending', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          data: [
            { foo: '2000:0db8:85a3:0000:0000:8a2e:0370:7331' },
            { foo: null },
            { foo: '3001:0db8:85a3:0000:0000:8a2e:0370:7333' },
            { foo: '1001:0db8:85a3:0000:0000:8a2e:0370:7337' },
            { foo: '5001:0db8:85a3:0000:0000:8a2e:0370:0' }
          ]
        },
        data: {
          sortField: 'foo',
          sortDirection: 'Ascending',
          globalPreferences: {
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [{
            metaName: 'foo',
            format: 'IPv6'
          }]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = clientSortedData(state);
    assert.equal(result[0].foo, state.investigate.eventResults.data[1].foo);
    assert.equal(result[1].foo, state.investigate.eventResults.data[3].foo);
    assert.equal(result[2].foo, state.investigate.eventResults.data[0].foo);
    assert.equal(result[3].foo, state.investigate.eventResults.data[2].foo);
    assert.equal(result[4].foo, state.investigate.eventResults.data[4].foo);
  });

  test('clientSortedData when time', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          data: [
            { time: 1 },
            { time: 2 },
            { time: 3 }
          ]
        },
        data: {
          sortField: 'time',
          sortDirection: 'Descending',
          globalPreferences: {
            dateFormat: true,
            timeFormat: true,
            timeZone: true,
            locale: true
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [{
            metaName: 'time',
            format: 'TimeT'
          }]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = clientSortedData(state);
    assert.equal(result[0].time, state.investigate.eventResults.data[2].time);
    assert.equal(result[1].time, state.investigate.eventResults.data[1].time);
    assert.equal(result[2].time, state.investigate.eventResults.data[0].time);
  });

  test('clientSortedData when Float', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          data: [
            { size: .100 },
            { size: .20 },
            { size: .3 }
          ]
        },
        data: {
          sortField: 'size',
          sortDirection: 'Descending',
          globalPreferences: {
            dateFormat: true,
            timeFormat: true,
            timeZone: true,
            locale: true
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [{
            metaName: 'size',
            format: 'Float'
          }]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = clientSortedData(state);
    assert.equal(result[0].size, state.investigate.eventResults.data[2].size);
    assert.equal(result[1].size, state.investigate.eventResults.data[1].size);
    assert.equal(result[2].size, state.investigate.eventResults.data[0].size);
  });

  test('clientSortedData when Int', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          data: [
            { size: 3 },
            { size: 10 },
            { size: 200 }
          ]
        },
        data: {
          sortField: 'size',
          sortDirection: 'Descending',
          globalPreferences: {
            dateFormat: true,
            timeFormat: true,
            timeZone: true,
            locale: true
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [{
            metaName: 'size',
            format: 'Int'
          }]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = clientSortedData(state);
    assert.equal(result[0].size, state.investigate.eventResults.data[2].size);
    assert.equal(result[1].size, state.investigate.eventResults.data[1].size);
    assert.equal(result[2].size, state.investigate.eventResults.data[0].size);
  });

  test('clientSortedData when MAC', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          data: [
            { foo: '00:00:00:00:00:01' },
            { foo: '00:00:00:02:00:00' },
            { foo: '00:03:00:00:00:00' }
          ]
        },
        data: {
          sortField: 'size',
          sortDirection: 'Descending',
          globalPreferences: {
            dateFormat: true,
            timeFormat: true,
            timeZone: true,
            locale: true
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [{
            metaName: 'foo',
            format: 'MAC'
          }]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = clientSortedData(state);
    assert.equal(result[0].size, state.investigate.eventResults.data[2].size);
    assert.equal(result[1].size, state.investigate.eventResults.data[1].size);
    assert.equal(result[2].size, state.investigate.eventResults.data[0].size);
  });

  test('clientSortedData when nulls are mixed in with Ints and Descending', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          data: [
            { size: 1 },
            { size: null },
            { size: 3 }
          ]
        },
        data: {
          sortField: 'size',
          sortDirection: 'Descending',
          globalPreferences: {
            dateFormat: true,
            timeFormat: true,
            timeZone: true,
            locale: true
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [{
            metaName: 'size',
            format: 'UInt8'
          }]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = clientSortedData(state);
    assert.equal(result[0].size, state.investigate.eventResults.data[2].size);
    assert.equal(result[1].size, state.investigate.eventResults.data[0].size);
    assert.equal(result[2].size, state.investigate.eventResults.data[1].size);
  });

  test('clientSortedData when nulls are mixed in with Ints and Ascending', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          data: [
            { size: 1 },
            { size: null },
            { size: 3 }
          ]
        },
        data: {
          sortField: 'size',
          sortDirection: 'Ascending',
          globalPreferences: {
            dateFormat: true,
            timeFormat: true,
            timeZone: true,
            locale: true
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [{
            metaName: 'size',
            format: 'UInt8'
          }]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = clientSortedData(state);
    assert.equal(result[0].size, state.investigate.eventResults.data[1].size);
    assert.equal(result[1].size, state.investigate.eventResults.data[0].size);
    assert.equal(result[2].size, state.investigate.eventResults.data[2].size);
  });

  test('clientSortedData when nulls are mixed in with Text sorted Ascending', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          data: [
            { foo: '-B' },
            { foo: null },
            { foo: 'c' }
          ]
        },
        data: {
          sortField: 'foo',
          sortDirection: 'Ascending',
          globalPreferences: {
            dateFormat: true,
            timeFormat: true,
            timeZone: true,
            locale: true
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [{
            metaName: 'foo',
            format: 'Text'
          }]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = clientSortedData(state);
    assert.equal(result[0].foo, state.investigate.eventResults.data[1].foo);
    assert.equal(result[1].foo, state.investigate.eventResults.data[0].foo);
    assert.equal(result[2].foo, state.investigate.eventResults.data[2].foo);
  });

  test('clientSortedData when nulls are mixed in with Text sorted Descending', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          data: [
            { foo: '-b' },
            { foo: null },
            { foo: 'C' }
          ]
        },
        data: {
          sortField: 'foo',
          sortDirection: 'Descending',
          globalPreferences: {
            dateFormat: true,
            timeFormat: true,
            timeZone: true,
            locale: true
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [{
            metaName: 'foo',
            format: 'Text'
          }]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = clientSortedData(state);
    assert.equal(result[0].foo, state.investigate.eventResults.data[2].foo, '1');
    assert.equal(result[1].foo, state.investigate.eventResults.data[0].foo, '2');
    assert.equal(result[2].foo, state.investigate.eventResults.data[1].foo, '3');
  });

  skip('nestChildEvents should decorate events regardless of enablement', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          eventRelationshipsEnabled: false,
          data: [
            {
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              'session.split': 1,
              sessionId: 1
            },
            {
              'time': 'Tue Oct 11 2019 13:54:16',
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              sessionId: 2
            },
            {
              'time': 'Tue Oct 12 2019 13:54:16',
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              sessionId: 3
            }

          ]
        },
        data: {
          sortField: 'foo',
          sortDirection: 'Descending',
          globalPreferences: {
            dateFormat: true,
            timeFormat: true,
            timeZone: true,
            locale: true
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [
            { metaName: 'ip.dst' },
            { metaName: 'ip.src' },
            { metaName: 'ipv6.dst' },
            { metaName: 'ipv6.src' },
            { metaName: 'tcp.dstport' },
            { metaName: 'tcp.srcport' },
            { metaName: 'udp.dstport' },
            { metaName: 'udp.srcport' }
          ]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = nestChildEvents(state);
    assert.equal(result[0].tuple, 'ip.src=127.0.0.1 AND ip.dst=127.0.0.1 AND tcp.srcport=25 AND tcp.dstport=25');
    assert.equal(result[1].eventIndex, 1);
    assert.equal(result[2].eventIndex, 1.000001570902856);
  });

  skip('nestChildEvents for tuple: ip.dst|ip.src|tcp.srcport|tcp.dstport', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          eventRelationshipsEnabled: true,
          data: [
            {
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              'session.split': 1,
              sessionId: 1
            },
            {
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              sessionId: 2
            },
            {
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              'session.split': 0,
              sessionId: 3
            }
          ]
        },
        data: {
          sortField: 'foo',
          sortDirection: 'Descending',
          globalPreferences: {
            dateFormat: true,
            timeFormat: true,
            timeZone: true,
            locale: true
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [
            { metaName: 'ip.dst' },
            { metaName: 'ip.src' },
            { metaName: 'ipv6.dst' },
            { metaName: 'ipv6.src' },
            { metaName: 'tcp.dstport' },
            { metaName: 'tcp.srcport' },
            { metaName: 'udp.dstport' },
            { metaName: 'udp.srcport' }
          ]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = nestChildEvents(state);
    assert.equal(result[0].sessionId, 2);
    assert.equal(result[1].sessionId, 3);
    assert.equal(result[2].sessionId, 1);
  });

  skip('nestChildEvents for tuple: ipv6.dst|ipv6.src|tcp.srcport|tcp.dstport', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          eventRelationshipsEnabled: true,
          data: [
            {
              'ipv6.dst': '127.0.0.1',
              'ipv6.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              'session.split': 1,
              sessionId: 1
            },
            {
              'ipv6.dst': '127.0.0.1',
              'ipv6.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              sessionId: 2
            },
            {
              'ipv6.dst': '127.0.0.1',
              'ipv6.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              'session.split': 0,
              sessionId: 3
            }
          ]
        },
        data: {
          sortField: 'foo',
          sortDirection: 'Descending',
          globalPreferences: {
            dateFormat: true,
            timeFormat: true,
            timeZone: true,
            locale: true
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [
            { metaName: 'ip.dst' },
            { metaName: 'ip.src' },
            { metaName: 'ipv6.dst' },
            { metaName: 'ipv6.src' },
            { metaName: 'tcp.dstport' },
            { metaName: 'tcp.srcport' },
            { metaName: 'udp.dstport' },
            { metaName: 'udp.srcport' }
          ]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = nestChildEvents(state);
    assert.equal(result[0].sessionId, 2);
    assert.equal(result[1].sessionId, 3);
    assert.equal(result[2].sessionId, 1);
  });

  skip('nestChildEvents for tuple: ipv6.dst|ipv6.src|udp.srcport|udp.dstport', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          eventRelationshipsEnabled: true,
          data: [
            {
              'ipv6.dst': '127.0.0.1',
              'ipv6.src': '127.0.0.1',
              'udp.srcport': 25,
              'udp.dstport': 25,
              'session.split': 1,
              sessionId: 1
            },
            {
              'ipv6.dst': '127.0.0.1',
              'ipv6.src': '127.0.0.1',
              'udp.srcport': 25,
              'udp.dstport': 25,
              sessionId: 2
            },
            {
              'ipv6.dst': '127.0.0.1',
              'ipv6.src': '127.0.0.1',
              'udp.srcport': 25,
              'udp.dstport': 25,
              'session.split': 0,
              sessionId: 3
            }
          ]
        },
        data: {
          sortField: 'foo',
          sortDirection: 'Descending',
          globalPreferences: {
            dateFormat: true,
            timeFormat: true,
            timeZone: true,
            locale: true
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [
            { metaName: 'ip.dst' },
            { metaName: 'ip.src' },
            { metaName: 'ipv6.dst' },
            { metaName: 'ipv6.src' },
            { metaName: 'tcp.dstport' },
            { metaName: 'tcp.srcport' },
            { metaName: 'udp.dstport' },
            { metaName: 'udp.srcport' }
          ]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = nestChildEvents(state);
    assert.equal(result[0].sessionId, 2);
    assert.equal(result[1].sessionId, 3);
    assert.equal(result[2].sessionId, 1);
  });

  skip('nestChildEvents for tuple: ip.dst|ip.src|udp.srcport|udp.dstport', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          eventRelationshipsEnabled: true,
          data: [
            {
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'udp.srcport': 25,
              'udp.dstport': 25,
              'session.split': 1,
              sessionId: 1
            },
            {
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'udp.srcport': 25,
              'udp.dstport': 25,
              sessionId: 2
            },
            {
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'udp.srcport': 25,
              'udp.dstport': 25,
              'session.split': 0,
              sessionId: 3
            }
          ]
        },
        data: {
          sortField: 'foo',
          sortDirection: 'Descending',
          globalPreferences: {
            dateFormat: true,
            timeFormat: true,
            timeZone: true,
            locale: true
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [
            { metaName: 'ip.dst' },
            { metaName: 'ip.src' },
            { metaName: 'ipv6.dst' },
            { metaName: 'ipv6.src' },
            { metaName: 'tcp.dstport' },
            { metaName: 'tcp.srcport' },
            { metaName: 'udp.dstport' },
            { metaName: 'udp.srcport' }
          ]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = nestChildEvents(state);
    assert.equal(result[0].sessionId, 2);
    assert.equal(result[1].sessionId, 3);
    assert.equal(result[2].sessionId, 1);
  });

  skip('nestChildEvents when nesting is disabled', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          eventRelationshipsEnabled: false,
          data: [
            {
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              'session.split': 1,
              sessionId: 1
            },
            {
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              sessionId: 2
            },
            {
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              'session.split': 0,
              sessionId: 3
            }
          ]
        },
        data: {
          sortField: 'foo',
          sortDirection: 'Descending',
          globalPreferences: {
            dateFormat: true,
            timeFormat: true,
            timeZone: true,
            locale: true
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [
            { metaName: 'ip.dst' },
            { metaName: 'ip.src' },
            { metaName: 'ipv6.dst' },
            { metaName: 'ipv6.src' },
            { metaName: 'tcp.dstport' },
            { metaName: 'tcp.srcport' },
            { metaName: 'udp.dstport' },
            { metaName: 'udp.srcport' }
          ]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = nestChildEvents(state);
    assert.equal(result[0].sessionId, 1);
    assert.equal(result[1].sessionId, 2);
    assert.equal(result[2].sessionId, 3);
  });

  skip('nestChildEvents for tuple with multiple parents and different times', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          eventRelationshipsEnabled: true,
          data: [
            {
              'time': new Date(1571066026000 - 100000),
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              sessionId: 1
            },
            {
              'time': new Date(1571066026000 - 300000),
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              sessionId: 2
            },
            {
              'time': new Date(1571066026000 - 200000),
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              sessionId: 3
            }
          ]
        },
        data: {
          sortField: 'foo',
          sortDirection: 'Descending',
          globalPreferences: {
            dateFormat: true,
            timeFormat: true,
            timeZone: true,
            locale: true
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [
            { metaName: 'ip.dst' },
            { metaName: 'ip.src' },
            { metaName: 'ipv6.dst' },
            { metaName: 'ipv6.src' },
            { metaName: 'tcp.dstport' },
            { metaName: 'tcp.srcport' },
            { metaName: 'udp.dstport' },
            { metaName: 'udp.srcport' }
          ]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    const result = nestChildEvents(state);
    assert.equal(result[0].sessionId, 2);
    assert.equal(result[1].sessionId, 3);
    assert.equal(result[2].sessionId, 1);
  });

  skip('eventsHaveSplits when true', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          eventRelationshipsEnabled: true,
          data: [
            {
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              'session.split': 1,
              sessionId: 1
            },
            {
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              sessionId: 2
            },
            {
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              'session.split': 0,
              sessionId: 3
            }
          ]
        },
        data: {
          sortField: 'foo',
          sortDirection: 'Descending',
          globalPreferences: {
            dateFormat: true,
            timeFormat: true,
            timeZone: true,
            locale: true
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [
            { metaName: 'ip.dst' },
            { metaName: 'ip.src' },
            { metaName: 'ipv6.dst' },
            { metaName: 'ipv6.src' },
            { metaName: 'tcp.dstport' },
            { metaName: 'tcp.srcport' },
            { metaName: 'udp.dstport' },
            { metaName: 'udp.srcport' }
          ]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    assert.equal(eventsHaveSplits(state), true);
  });

  test('eventsHaveSplits when false', async function(assert) {
    const state = {
      investigate: {
        eventResults: {
          eventRelationshipsEnabled: true,
          data: [
            {
              'ip.dst': '127.0.0.1',
              'ip.src': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              sessionId: 1
            },
            {
              'ip.dst': '127.0.0.2',
              'ip.src': '127.0.0.2',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              sessionId: 2
            },
            {
              'ip.dst': '127.0.0.3',
              'ip.src': '127.0.0.3',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              sessionId: 3
            }
          ]
        },
        data: {
          sortField: 'foo',
          sortDirection: 'Descending',
          globalPreferences: {
            dateFormat: true,
            timeFormat: true,
            timeZone: true,
            locale: true
          }
        },
        eventCount: {
          threshold: 1000,
          data: 3
        },
        dictionaries: {
          language: [
            { metaName: 'ip.dst' },
            { metaName: 'ip.src' },
            { metaName: 'ipv6.dst' },
            { metaName: 'ipv6.src' },
            { metaName: 'tcp.dstport' },
            { metaName: 'tcp.srcport' },
            { metaName: 'udp.dstport' },
            { metaName: 'udp.srcport' }
          ]
        },
        services: {
          serviceData: [{
            version: '11.4'
          }]
        },
        queryNode: {
          previousQueryParams: {}
        }
      }
    };

    assert.equal(eventsHaveSplits(state), false);
  });

  test('updateStreamKeyTree on initial pass', async function(assert) {
    const result = updateStreamKeyTree(
      {},
      {
        'ip.src': '127.0.0.1',
        'ip.dst': '127.0.0.1',
        'tcp.srcport': 25,
        'tcp.dstport': 25
      },
      'ip.src',
      'ip.dst',
      'tcp.srcport',
      'tcp.dstport'
    );

    assert.deepEqual(result, {
      '127.0.0.1': {
        '127.0.0.1': {
          25: {
            25: {
              'ip.src': '127.0.0.1',
              'ip.dst': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25
            }
          }
        }
      }
    });
  });

  test('updateStreamKeyTree on overwriting previously saved parent event with definite parent', async function(assert) {
    const result = updateStreamKeyTree(
      {
        '127.0.0.1': {
          '127.0.0.1': {
            25: {
              25: {
                'ip.src': '127.0.0.2',
                'ip.dst': '127.0.0.2',
                'tcp.srcport': 25,
                'tcp.dstport': 25,
                'session.split': 0
              }
            }
          }
        }
      },
      {
        'ip.src': '127.0.0.1',
        'ip.dst': '127.0.0.1',
        'tcp.srcport': 25,
        'tcp.dstport': 25
      },
      'ip.src',
      'ip.dst',
      'tcp.srcport',
      'tcp.dstport'
    );

    assert.deepEqual(result, {
      '127.0.0.1': {
        '127.0.0.1': {
          25: {
            25: {
              'ip.src': '127.0.0.1',
              'ip.dst': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25
            }
          }
        }
      }
    });
  });

  test('updateStreamKeyTree on overwriting previously saved parent event with earlier sibling', async function(assert) {
    const result = updateStreamKeyTree(
      {
        '127.0.0.1': {
          '127.0.0.1': {
            25: {
              25: {
                'ip.src': '127.0.0.2',
                'ip.dst': '127.0.0.2',
                'tcp.srcport': 25,
                'tcp.dstport': 25,
                'session.split': 1
              }
            }
          }
        }
      },
      {
        'ip.src': '127.0.0.1',
        'ip.dst': '127.0.0.1',
        'tcp.srcport': 25,
        'tcp.dstport': 25,
        'session.split': 0
      },
      'ip.src',
      'ip.dst',
      'tcp.srcport',
      'tcp.dstport'
    );

    assert.deepEqual(result, {
      '127.0.0.1': {
        '127.0.0.1': {
          25: {
            25: {
              'ip.src': '127.0.0.1',
              'ip.dst': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25,
              'session.split': 0
            }
          }
        }
      }
    });
  });

  test('updateStreamKeyTree should not update previously saved true parent event with earlier child', async function(assert) {
    const result = updateStreamKeyTree(
      {
        '127.0.0.1': {
          '127.0.0.1': {
            25: {
              25: {
                'sessionId': 1,
                'time': 'Tue Oct 12 2019 13:54:16',
                'ip.src': '127.0.0.1',
                'ip.dst': '127.0.0.1',
                'tcp.srcport': 25,
                'tcp.dstport': 25
              }
            }
          }
        }
      },
      {
        'sessionId': 2,
        'time': 'Tue Oct 12 2019 13:34:16',
        'ip.src': '127.0.0.1',
        'ip.dst': '127.0.0.1',
        'tcp.srcport': 25,
        'tcp.dstport': 25,
        'session.split': 0
      },
      'ip.src',
      'ip.dst',
      'tcp.srcport',
      'tcp.dstport'
    );

    assert.deepEqual(result, {
      '127.0.0.1': {
        '127.0.0.1': {
          25: {
            25: {
              'sessionId': 1,
              'time': 'Tue Oct 12 2019 13:54:16',
              'ip.src': '127.0.0.1',
              'ip.dst': '127.0.0.1',
              'tcp.srcport': 25,
              'tcp.dstport': 25
            }
          }
        }
      }
    });
  });


});
