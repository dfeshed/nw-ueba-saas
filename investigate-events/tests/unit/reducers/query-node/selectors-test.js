import { module, test } from 'qunit';
import {
  hasMetaFilters,
  hasRequiredValuesToQuery,
  queryParams,
  selectedTimeRange,
  selectedTimeRangeId,
  selectedTimeRangeName,
  useDatabaseTime
} from 'investigate-events/reducers/investigate/query-node/selectors';
import TIME_RANGES from 'investigate-events/constants/time-ranges';

module('Unit | Selectors | queryNode');

test('determine presence of meta filters', function(assert) {
  const state = {
    investigate: {
      queryNode: {
        metaFilter: {
          conditions: ['a', 'b']
        }
      }
    }
  };
  const hasFilters = hasMetaFilters(state);

  assert.ok(hasFilters, 'meta filter conditions are present');
});

test('queryParams object has required properties with correct values', function(assert) {
  assert.expect(5);

  const mf = { uri: '', conditions: [] };
  const state = {
    investigate: {
      queryNode: {
        endTime: 1,
        eventMetas: ['a', 'b'],
        metaFilter: mf,
        serviceId: 'sd1',
        startTime: 2
      }
    }
  };
  const qp = queryParams(state);

  assert.equal(Object.keys(qp).length, 4, 'has 4 properties');
  assert.equal(qp.endTime, 1, 'endTime is present and has correct value');
  assert.deepEqual(qp.metaFilter, mf, 'metaFilter is present and has correct value');
  assert.equal(qp.serviceId, 'sd1', 'serviceId is present and has correct value');
  assert.equal(qp.startTime, 2, 'startTime is present and has correct value');
});

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
        metaFilter: { conditions: [] },
        previouslySelectedTimeRanges: {}
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
        metaFilter: { conditions: [] },
        previouslySelectedTimeRanges: {}
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
        metaFilter: { conditions: [] },
        previouslySelectedTimeRanges: {}
      },
      services: {
        serviceData: [{ id: '1', displayName: 'svs1', name: 'SVS1', version: '11.1.0.0' }],
        summaryData: { startTime: 0 }
      }
    }
  };
  assert.notOk(hasRequiredValuesToQuery(state), 'summaryData.startTime was not "0"');
});

test('has required inputs to query', function(assert) {
  const state = {
    investigate: {
      queryNode: {
        isDirty: true,
        metaFilter: { conditions: [] },
        previouslySelectedTimeRanges: {},
        // serviceId can be undefined because we select a default service
        serviceId: undefined
      },
      services: {
        serviceData: [{ id: '1', displayName: 'svs1', name: 'SVS1', version: '11.1.0.0' }],
        summaryData: { startTime: 1 }
      }
    }
  };
  assert.ok(hasRequiredValuesToQuery(state), 'Missing some required state to query');
});