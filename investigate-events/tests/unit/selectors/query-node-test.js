import { module, test } from 'qunit';
import {
  hasMetaFilters,
  queryParams,
  selectedTimeRange,
  selectedTimeRangeId,
  useDatabaseTime
} from 'investigate-events/reducers/investigate/query-node/selectors';
import {
  DATABASE_TIME,
  DEFAULT_TIME_RANGE_ID
} from 'investigate-events/constants/time-ranges';

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
  assert.expect(6);

  const mf = { uri: '', conditions: [] };
  const state = {
    investigate: {
      queryNode: {
        endTime: 1,
        eventMetas: ['a', 'b'],
        metaFilter: mf,
        queryString: 'abc',
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
  assert.notOk(qp.queryString, 'queryString is not present');
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

  assert.equal(selectedTimeRange, DEFAULT_TIME_RANGE_ID, `set to ${DEFAULT_TIME_RANGE_ID}`);
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
  assert.equal(range.id, DEFAULT_TIME_RANGE_ID, `default object ${DEFAULT_TIME_RANGE_ID} was returned`);
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
        queryTimeFormat: DATABASE_TIME
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