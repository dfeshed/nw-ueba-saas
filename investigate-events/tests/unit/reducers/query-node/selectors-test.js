import { module, test } from 'qunit';
import {
  canQueryGuided,
  deselectedPills,
  enrichedPillsData,
  freeFormText,
  hasInvalidSelectedPill,
  hasRequiredValuesToQuery,
  hadTextPill,
  hasTextPill,
  isOnFreeForm,
  isOnGuided,
  isPillBeingEdited,
  isPillValidationInProgress,
  pillBeingEdited,
  queryNodeValuesForClassicUrl,
  selectedPills,
  selectedTimeRange,
  selectedTimeRangeId,
  selectedTimeRangeName,
  useDatabaseTime
} from 'investigate-events/reducers/investigate/query-node/selectors';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import TIME_RANGES from 'investigate-shared/constants/time-ranges';
import { TEXT_FILTER, QUERY_FILTER } from 'investigate-events/constants/pill';

module('Unit | Selectors | queryNode');

test('use default time range if not set', function(assert) {
  const state = {
    investigate: {
      queryNode: {
        serviceId: 'sd1',
        previouslySelectedTimeRanges: {}
      }
    }
  };
  const selectedTimeRange = selectedTimeRangeId(state);

  assert.equal(selectedTimeRange, TIME_RANGES.DEFAULT_TIME_RANGE_ID, `set to ${TIME_RANGES.DEFAULT_TIME_RANGE_ID}`);
});

test('retrieve correct time range with single range', function(assert) {
  const id = 'LAST_1_HOUR';
  const state = {
    investigate: {
      queryNode: {
        serviceId: 'sd1',
        previouslySelectedTimeRanges: { sd1: id }
      }
    }
  };
  const selectedTimeRange = selectedTimeRangeId(state);

  assert.equal(selectedTimeRange, id, `set to ${id}`);
});

test('retrieve correct time range with multiple ranges', function(assert) {
  const id1 = 'LAST_1_HOUR';
  const id2 = 'LAST_30_DAYS';
  const state = {
    investigate: {
      queryNode: {
        serviceId: 'sd2',
        previouslySelectedTimeRanges: { sd1: id1, sd2: id2 }
      }
    }
  };
  const selectedTimeRange = selectedTimeRangeId(state);

  assert.equal(selectedTimeRange, id2, `set to ${id2}`);
});

test('will not retrieve incorrect range with multiple ranges', function(assert) {
  const id1 = 'LAST_1_HOUR';
  const id2 = 'LAST_30_DAYS';
  const state = {
    investigate: {
      queryNode: {
        serviceId: 'sd1',
        previouslySelectedTimeRanges: { sd1: id1, sd2: id2 }
      }
    }
  };
  const selectedTimeRange = selectedTimeRangeId(state);

  assert.notEqual(selectedTimeRange, id2, `should not be ${id2}`);
});

test('retrieve correct default time range object', function(assert) {
  assert.expect(2);

  const state = {
    investigate: {
      queryNode: {
        serviceId: 'sd1',
        previouslySelectedTimeRanges: {}
      }
    }
  };
  const range = selectedTimeRange(state);

  assert.equal(typeof(range), 'object', 'range is of type "object"');
  assert.equal(range.id, TIME_RANGES.DEFAULT_TIME_RANGE_ID, `default object ${TIME_RANGES.DEFAULT_TIME_RANGE_ID} was returned`);
});

test('retrieve correct specified time range object', function(assert) {
  assert.expect(2);

  const id = 'LAST_30_DAYS';
  const state = {
    investigate: {
      queryNode: {
        serviceId: 'sd1',
        previouslySelectedTimeRanges: { sd1: id }
      }
    }
  };
  const range = selectedTimeRange(state);

  assert.equal(typeof(range), 'object', 'range is of type "object"');
  assert.equal(range.id, id, `object for ${id} was returned`);
});

test('using database time is specified correctly', function(assert) {
  const state = {
    investigate: {
      queryNode: {
        queryTimeFormat: TIME_RANGES.DATABASE_TIME
      }
    }
  };
  assert.ok(useDatabaseTime(state), 'time range not specified to use database');
});

test('not using database time is specified correctly', function(assert) {
  const state = {
    investigate: {
      queryNode: {
        queryTimeFormat: 'foo'
      }
    }
  };
  assert.notOk(useDatabaseTime(state), 'time range specified to use database');
});

test('get time range name when one was previously selected', function(assert) {
  const name = TIME_RANGES.getNameById(TIME_RANGES.DEFAULT_TIME_RANGE_ID);
  const state = {
    investigate: {
      queryNode: {
        serviceId: '1',
        previouslySelectedTimeRanges: {
          '1': TIME_RANGES.DEFAULT_TIME_RANGE_ID
        }
      }
    }
  };
  assert.equal(selectedTimeRangeName(state), name, `should be ${name}`);
});

test('return empty string when time range wasn\'t previously selected', function(assert) {
  const state = {
    investigate: {
      queryNode: {
        serviceId: '1',
        previouslySelectedTimeRanges: {
          '2': TIME_RANGES.DEFAULT_TIME_RANGE_ID
        }
      }
    }
  };
  assert.equal(selectedTimeRangeName(state), '', 'should be blank');
});

test('need services to query', function(assert) {
  const state = {
    investigate: {
      queryNode: {
        serviceId: '1',
        metaFilter: [],
        previouslySelectedTimeRanges: {},
        currentQueryHash: 'kajsdlkajldsk',
        pillsData: []
      },
      services: {
        serviceData: null,
        summaryData: { startTime: 1 }
      }
    }
  };
  assert.notOk(hasRequiredValuesToQuery(state), 'serviceData was set');
});

test('need summary data to query', function(assert) {
  const state = {
    investigate: {
      queryNode: {
        serviceId: '1',
        metaFilter: [],
        previouslySelectedTimeRanges: {},
        currentQueryHash: 'kajsdlkajldsk',
        pillsData: []
      },
      services: {
        serviceData: [{ id: '1', displayName: 'svs1', name: 'SVS1', version: '11.1.0.0' }],
        summaryData: null
      }
    }
  };
  assert.notOk(hasRequiredValuesToQuery(state), 'summaryData was set');
});

test('aggregation was not performed, so we cannot query', function(assert) {
  const state = {
    investigate: {
      queryNode: {
        serviceId: '1',
        metaFilter: [],
        previouslySelectedTimeRanges: {},
        currentQueryHash: 'kajsdlkajldsk',
        pillsData: []
      },
      services: {
        serviceData: [{ id: '1', displayName: 'svs1', name: 'SVS1', version: '11.1.0.0' }],
        summaryData: { startTime: 0 }
      }
    }
  };
  assert.notOk(hasRequiredValuesToQuery(state), 'summaryData.startTime was not "0"');
});

test('is not dirty so cannot query', function(assert) {
  const state = {
    investigate: {
      queryNode: {
        metaFilter: [],
        previouslySelectedTimeRanges: {},
        serviceId: '1',
        startTime: '1',
        endTime: '2',
        currentQueryHash: '1-1-2-abc',
        pillsData: [{ type: QUERY_FILTER, meta: 'a', operator: 'b', value: 'c' }]
      },
      services: {
        serviceData: [{ id: '1', displayName: 'svs1', name: 'SVS1', version: '11.1.0.0' }],
        summaryData: { startTime: 1 }
      }
    }
  };
  assert.notOk(hasRequiredValuesToQuery(state), 'query has not been updated so cannot query');
});

test('is dirty due to change in query', function(assert) {
  const state = {
    investigate: {
      queryNode: {
        metaFilter: [],
        previouslySelectedTimeRanges: {},
        serviceId: '1',
        startTime: '1',
        endTime: '2',
        // note service is 2
        currentQueryHash: '2-1-2-abc',
        pillsData: [{ type: QUERY_FILTER, meta: 'a', operator: 'b', value: 'c' }]
      },
      services: {
        serviceData: [{ id: '1', displayName: 'svs1', name: 'SVS1', version: '11.1.0.0' }],
        summaryData: { startTime: 1 }
      }
    }
  };
  assert.ok(hasRequiredValuesToQuery(state), 'can query due to pills being updated');
});

test('is dirty due to updated free form text', function(assert) {
  const state = {
    investigate: {
      queryNode: {
        metaFilter: [],
        previouslySelectedTimeRanges: {},
        serviceId: '1',
        startTime: '1',
        endTime: '2',
        // note hash is the same as data
        currentQueryHash: '1-1-2-abc',
        pillsData: [{ type: QUERY_FILTER, meta: 'a', operator: 'b', value: 'c' }],
        updatedFreeFormTextPill: { complexFilterText: 'boom' }
      },
      services: {
        serviceData: [{ id: '1', displayName: 'svs1', name: 'SVS1', version: '11.1.0.0' }],
        summaryData: { startTime: 1 }
      }
    }
  };
  assert.ok(hasRequiredValuesToQuery(state), 'can query because free form updated');
});

test('is not dirty even with free form updated', function(assert) {
  const state = {
    investigate: {
      queryNode: {
        metaFilter: [],
        previouslySelectedTimeRanges: {},
        serviceId: '1',
        startTime: '1',
        endTime: '2',
        // note hash is the same as data
        currentQueryHash: '1-1-2-a=b',
        pillsData: [{ type: QUERY_FILTER, meta: 'a', operator: '=', value: 'b' }],
        updatedFreeFormTextPill: {
          meta: 'a', operator: '=', value: 'b'
        }
      },
      services: {
        serviceData: [{ id: '1', displayName: 'svs1', name: 'SVS1', version: '11.1.0.0' }],
        summaryData: { startTime: 1 }
      }
    }
  };
  assert.notOk(hasRequiredValuesToQuery(state), 'cannot query because free form updated equals pill data');
});

test('has required inputs to query', function(assert) {
  const state = {
    investigate: {
      queryNode: {
        metaFilter: [],
        previouslySelectedTimeRanges: {},
        // serviceId can be undefined because we select a default service
        serviceId: undefined,
        currentQueryHash: '`kajsdlkajldsk',
        pillsData: []
      },
      services: {
        serviceData: [{ id: '1', displayName: 'svs1', name: 'SVS1', version: '11.1.0.0' }],
        summaryData: { startTime: 1 }
      }
    }
  };
  assert.ok(hasRequiredValuesToQuery(state), 'Missing some required state to query');
});

test('check isOnFreeForm', function(assert) {
  const state = {
    investigate: {
      queryNode: {
        queryView: 'freeForm'
      }
    }
  };
  assert.equal(isOnFreeForm(state), true, 'Should have focus');
});

test('check isOnGuided', function(assert) {
  const state = {
    investigate: {
      queryNode: {
        queryView: 'guided'
      }
    }
  };
  assert.equal(isOnGuided(state), true, 'Should have focus');
});

test('enrichedPillsData is false when status is not error', function(assert) {
  const state = new ReduxDataHelper().language().pillsDataPopulated().build();
  const pD = enrichedPillsData(state);
  assert.equal(pD.length, 3, 'returns correct number of pill data');
  assert.equal(pD[0].meta.metaName, 'a', 'transforms meta correctly');
  assert.equal(pD[0].operator.displayName, '=', 'transforms operator correctly');
  assert.equal(pD[0].value, '\'x\'', 'transforms value correctly');
});

test('enrichedPillsData contains proper twin focused details', function(assert) {
  const state = new ReduxDataHelper()
    .language()
    .pillsDataWithParens()
    .markFocused(['3'])
    .build();
  const pD = enrichedPillsData(state);
  assert.equal(pD[0].isTwinFocused, true, 'does not indicate twin is focused');
  assert.equal(pD[2].isTwinFocused, undefined, 'indicates twin is focused when IT is focused');
});

test('selectedPills returns only those pills that are selected', function(assert) {
  const state = new ReduxDataHelper()
    .language()
    .pillsDataPopulated()
    .markSelected(['1'])
    .build();
  const pD = selectedPills(state);
  assert.equal(pD.length, 1, 'returns correct number of pill data');
  assert.equal(pD[0].meta, 'a', 'transforms meta correctly');
  assert.equal(pD[0].operator, '=', 'transforms operator correctly');
  assert.equal(pD[0].value, '\'x\'', 'transforms value correctly');
  assert.equal(pD[0].id, 1, 'transforms id correctly');
});

test('deselectedPills returns only those pills that are not selected', function(assert) {
  const state = new ReduxDataHelper()
    .language()
    .pillsDataPopulated()// P & P
    .markSelected(['1'])
    .build();
  const pD = deselectedPills(state);
  assert.equal(pD.length, 2, 'returns correct number of pill data');
  assert.equal(pD[1].meta, 'b', 'transforms meta correctly');
  assert.equal(pD[1].operator, '=', 'transforms operator correctly');
  assert.equal(pD[1].value, '\'y\'', 'transforms value correctly');
  assert.equal(pD[1].id, 3, 'transforms id correctly');
});

test('canQueryGuided is true when a query is ready to execute and NO invalid pill is present', function(assert) {
  const state = new ReduxDataHelper()
    .language()
    .pillsDataPopulated()
    .hasRequiredValuesToQuery(true)
    .build();

  const canQuery = canQueryGuided(state);
  assert.ok(canQuery, 'Selector returns true if a query is ready to execute and NO invalid pill is present');
});

test('canQueryGuided is false when query is ready to execute, but an invalid pill is present', function(assert) {
  const state = new ReduxDataHelper()
    .language()
    .hasRequiredValuesToQuery(true)
    .pillsDataPopulated()
    .markInvalid(['1'])
    .build();

  const canQuery = canQueryGuided(state);
  assert.notOk(canQuery, 'Selector returns false if query is ready to execute, but an invalid pill is present');
});

test('hasInvalidSelectedPill is false when no invalid pill is selected', function(assert) {
  const state = new ReduxDataHelper()
    .language()
    .hasRequiredValuesToQuery(true)
    .pillsDataPopulated()
    .markSelected(['1'])
    .build();

  const hasInvalid = hasInvalidSelectedPill(state);
  assert.notOk(hasInvalid, 'Selector returns false if no invalid pill is selected');
});

test('hasInvalidSelectedPill is true when invalid pill is selected', function(assert) {
  const state = new ReduxDataHelper()
    .language()
    .hasRequiredValuesToQuery(true)
    .pillsDataPopulated()
    .markSelected(['1'])
    .markInvalid(['1'])
    .build();

  const hasInvalid = hasInvalidSelectedPill(state);
  assert.ok(hasInvalid, 'Selector returns true if  invalid pill is selected');
});

test('freeFormText is set properly', function(assert) {
  const state = new ReduxDataHelper()
    .language()
    .pillsDataPopulated()
    .build();

  const text = freeFormText(state);
  assert.equal(text, 'a = \'x\' AND b = \'y\'', 'freeFormText is set properly');
});

test('pillBeingEdited returns pill being edited ', function(assert) {
  const state = new ReduxDataHelper()
    .language()
    .hasRequiredValuesToQuery(true)
    .pillsDataPopulated()
    .markEditing(['1'])
    .build();

  const pBE = pillBeingEdited(state);
  assert.deepEqual(pBE, state.investigate.queryNode.pillsData[0], 'Selector returns pill that is being edited');
});

test('pillBeingEdited is undefined when no pills being edited', function(assert) {
  const state = new ReduxDataHelper()
    .language()
    .hasRequiredValuesToQuery(true)
    .pillsDataPopulated()
    .build();

  const pBE = pillBeingEdited(state);
  assert.deepEqual(pBE, undefined, 'Selector returns pill that is being edited');
});

test('determine if a pill is being edited', function(assert) {
  // Test truthy case
  const truthyState = new ReduxDataHelper()
    .language()
    .pillsDataPopulated()
    .markEditing(['1'])
    .build();
  assert.ok(isPillBeingEdited(truthyState), 'pill is properly identified as being edited');

  // test falsy case
  const falsyState = new ReduxDataHelper()
    .language()
    .pillsDataPopulated()
    .markEditing([])
    .build();
  assert.notOk(isPillBeingEdited(falsyState), 'pill is properly identified as being edited');
});

test('determine if a pill is being validated', function(assert) {
  // Test truthy case
  const truthyState = new ReduxDataHelper()
    .language()
    .pillsDataPopulated()
    .markValidationInProgress(['1'])
    .build();
  assert.ok(isPillValidationInProgress(truthyState), 'validation is properly identified as being in progress');

  // test falsy case
  const falsyState = new ReduxDataHelper()
    .language()
    .pillsDataPopulated()
    .build();
  assert.notOk(isPillValidationInProgress(falsyState), 'validation is properly identified as NOT being in progress');
});

test('determine if pills contain a text pill', function(assert) {
  // Test truthy case
  const truthyState = new ReduxDataHelper()
    .pillsDataVaried()
    .build();
  assert.ok(hasTextPill(truthyState), 'validation is properly identified as having a text pill');

  // test falsy case
  const falsyState = new ReduxDataHelper()
    .pillsDataPopulated()
    .build();
  assert.notOk(hasTextPill(falsyState), 'validation is properly identified as NOT having a text pill');
});

test('hadTextPill detects if a previous query had a Text Filter', function(assert) {
  // Test truthy case
  const truthyState = { investigate: {
    queryNode: {
      previousQueryParams: {
        metaFilter: [
          { type: TEXT_FILTER }
        ]
      }
    }
  } };
  assert.ok(hadTextPill(truthyState), 'did not detect text pill');

  // Test falsy case
  const falsyState = { investigate: {
    queryNode: {
      previousQueryParams: {
        metaFilter: [
          { type: QUERY_FILTER }
        ]
      }
    }
  } };
  assert.notOk(hadTextPill(falsyState), 'detected a text pill when there was not one');

  // Test invalid case
  const invalidState = { investigate: {
    queryNode: {
      previousQueryParams: undefined
    }
  } };
  assert.notOk(hadTextPill(invalidState), 'detected a text pill when there was no previous query data');
});


test('queryNodeValuesForClassicUrl sends out values if serviceId is present', function(assert) {

  let state = { investigate: {
    queryNode: {
      endTime: '1508178179',
      startTime: '1508091780',
      serviceId: '555d9a6fe4b0d37c827d402e',
      previouslySelectedTimeRanges: {
        '555d9a6fe4b0d37c827d402e': TIME_RANGES.DEFAULT_TIME_RANGE_ID
      },
      pillDataHashes: [
        'wawa1'
      ],
      pillsData: [
        {
          type: TEXT_FILTER, searchTerm: 'foobar'
        }
      ]
    }
  } };
  assert.deepEqual(queryNodeValuesForClassicUrl(state), {
    endTime: '1508178179',
    startTime: '1508091780',
    timeRangeType: 'LAST_24_HOURS',
    serviceId: '555d9a6fe4b0d37c827d402e',
    pillDataHashes: ['wawa1'],
    textSearchTerm: { type: TEXT_FILTER, searchTerm: 'foobar' }
  });

  state = { investigate: {
    queryNode: { }
  } };
  assert.notOk(queryNodeValuesForClassicUrl(state), 'Did not find any values');

});