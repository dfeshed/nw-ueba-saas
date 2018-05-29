import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import {
  isCoreServiceNotUpdated,
  getCoreDeviceVersion,
  getDbEndTime,
  getDbStartTime,
  hasSummaryData
} from 'investigate-events/reducers/investigate/services/selectors';

module('Unit | Selectors | services');

const endTime = new Date() / 1000 | 0;
const startTime = endTime - 3600;
const state = Immutable.from({
  investigate: {
    services: {
      serviceData: [
        { id: 'id1', displayName: 'Service Name', name: 'SN' },
        { id: 'id2', displayName: 'Service Name2', name: 'SN2' }
      ],
      summaryData: {
        startTime: '1234',
        endTime: '6789'
      }
    },
    queryNode: {
      serviceId: 'sd1',
      startTime,
      endTime,
      metaFilter: {
        conditions: [
          {
            queryString: 'foo=foo-value',
            isKeyValuePair: true,
            key: 'foo',
            value: 'foo-value'
          }
        ]
      }
    }
  }
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

test('determine correct core version is returned', function(assert) {
  const state = {
    investigate: {
      services: {
        serviceData: [
          { id: 'id1', displayName: 'Service Name', name: 'SN', version: '10.6.5.0-7206.5.21dd2e7' }
        ]
      },
      queryNode: {
        serviceId: 'sd1'
      }
    }
  };

  const coreVersion = getCoreDeviceVersion(state);
  assert.equal(coreVersion, '10.6.5.0-7206.5.21dd2e7', 'Correct core version');
});

test('when core version is not set', function(assert) {
  const state = {
    investigate: {
      services: {
        serviceData: [
          {}
        ]
      },
      queryNode: {
        serviceId: 'sd1'
      }
    }
  };
  const coreVersion = getCoreDeviceVersion(state);
  assert.equal(coreVersion, null, 'Core version is not set');
});

test('determine if core services are not updated with 10.x', function(assert) {
  const state = {
    investigate: {
      services: {
        serviceData: [
          { id: 'id1', displayName: 'Service Name', name: 'SN', version: '10.6.5.0-7206.5.21dd2e7' }
        ]
      },
      queryNode: {
        serviceId: 'sd1'
      }
    }
  };

  const appVersion = { version: '11.1.0.0', minServiceVersion: '11.1' };
  const flag = isCoreServiceNotUpdated(state, appVersion);

  assert.ok(flag, 'Core Service is not updated');
});

test('determine if core services are not updated with 11.x', function(assert) {
  const state = {
    investigate: {
      services: {
        serviceData: [
          { id: 'id1', displayName: 'Service Name', name: 'SN', version: '11.1.0.0-7206.5.21dd2e7' }
        ]
      },
      queryNode: {
        serviceId: 'sd1'
      }
    }
  };

  const appVersion = { version: '11.3.0.0', minServiceVersion: '11.2' };
  const flag = isCoreServiceNotUpdated(state, appVersion);

  assert.ok(flag, 'Core Service is not updated');
});

test('determine if core services are updated with 11.1', function(assert) {
  const state = {
    investigate: {
      services: {
        serviceData: [
          { id: 'id1', displayName: 'Service Name', name: 'SN', version: '11.1.0.0-7206.5.21dd2e7' }
        ]
      },
      queryNode: {
        serviceId: 'sd1'
      }
    }
  };
  const appVersion = { version: '11.1.0.0', minServiceVersion: '11.1' };
  const flag = isCoreServiceNotUpdated(state, appVersion);
  assert.notOk(flag, 'Core Service is up to date');
});

test('determine if core services are updated beyond 11.1', function(assert) {
  const state = {
    investigate: {
      services: {
        serviceData: [
          { id: 'id1', displayName: 'Service Name', name: 'SN', version: '11.3.0.0-7206.5.21dd2e7' }
        ]
      },
      queryNode: {
        serviceId: 'sd1'
      }
    }
  };
  const appVersion = { version: '11.2.0.0', minServiceVersion: '11.1' };
  const flag = isCoreServiceNotUpdated(state, appVersion);
  assert.notOk(flag, 'Core Service is up to date');
});
