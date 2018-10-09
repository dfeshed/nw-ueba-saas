import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import {
  getDbEndTime,
  getDbStartTime,
  hasSummaryData,
  hasFatalSummaryError,
  selectedService,
  queriedService
} from 'investigate-events/reducers/investigate/services/selectors';

module('Unit | Selectors | services');

const endTime = new Date() / 1000 | 0;
const startTime = endTime - 3600;
const state = Immutable.from({
  investigate: {
    services: {
      serviceData: [
        { id: 'id1', displayName: 'Service Name', name: 'SN' },
        { id: 'id2', displayName: 'Service Name2', name: 'SN2' },
        { id: 'sd1', displayName: 'Service Name3', name: 'SN3' }
      ],
      summaryData: {
        startTime: '1234',
        endTime: '6789'
      }
    },
    queryNode: {
      previousQueryParams: {
        serviceId: 'id1'
      },
      serviceId: 'sd1',
      startTime,
      endTime,
      metaFilter: [
        {
          queryString: 'foo=foo-value',
          isKeyValuePair: true,
          key: 'foo',
          value: 'foo-value'
        }
      ]
    }
  }
});

const errorState = Immutable.from({
  investigate: {
    services: {
      serviceData: [
        { id: 'id1', displayName: 'Service Name', name: 'SN' },
        { id: 'id2', displayName: 'Service Name2', name: 'SN2' }
      ],
      isSummaryRetrieveError: true,
      summaryErrorCode: 3
    }
  }
});

test('queriedService are computed correctly', function(assert) {
  assert.equal(queriedService(state).displayName, 'Service Name', 'Returns the queried service');
});

test('selectedService are computed correctly', function(assert) {
  assert.equal(selectedService(state).displayName, 'Service Name3', 'Returns the selected service');
});

test('hasSummaryData are computed correctly', function(assert) {
  assert.equal(hasSummaryData(state), true, 'The returned value from hasSummaryData selector is as expected');
});

test('handle different inputs when retrieving database end time', function(assert) {
  assert.expect(2);

  assert.equal(getDbEndTime(state), 6789, 'time was null');

  const missingSummaryState = state.setIn(['investigate', 'services', 'summaryData'], null);
  assert.equal(getDbEndTime(missingSummaryState), null, 'time should be null');
});

test('handle different inputs when retrieving database start time', function(assert) {
  assert.expect(2);

  assert.equal(getDbStartTime(state), 1234, 'time was null');

  const missingSummaryState = state.setIn(['investigate', 'services', 'summaryData'], null);
  assert.equal(getDbStartTime(missingSummaryState), null, 'time should be null');
});

test('check for summaryFatalError', function(assert) {
  assert.expect(1);

  assert.ok(hasFatalSummaryError(errorState), 'has fetch summary error - shut down events');
});
