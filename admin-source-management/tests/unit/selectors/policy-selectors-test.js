import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import _ from 'lodash';
import moment from 'moment';

import {
  isPolicyLoading,
  currentPolicy,
  hasMissingRequiredData,
  weekOptions,
  startDate,
  startTime,
  runIntervalConfig
} from 'admin-source-management/reducers/usm/policy-selectors';

module('Unit | Selectors | Policy Selectors');

const fullState = {
  usm: {
    policy: {
      policy: {
        name: '',
        description: '',
        scheduleConfig: {
          enabledScheduledScan: false,
          scheduleOptions: {
            scanStartDate: null,
            scanStartTime: [10, 0],
            recurrenceInterval: 5,
            recurrenceIntervalUnit: 'DAYS',
            runOnDaysOfWeek: []
          },
          scanOptions: {
            cpuMaximum: 75,
            cpuMaximumOnVirtualMachine: 85
          }
        }
      },
      policyStatus: null // wait, complete, error
    }
  }
};

const policyData = {
  'id': 'policy_001',
  'name': 'Policy 001',
  'description': 'Policy Description'
};

test('isPolicyLoading selector', function(assert) {
  const state = _.cloneDeep(fullState);
  state.usm.policy.policyStatus = 'wait';
  assert.equal(isPolicyLoading(Immutable.from(state)), true, 'isPolicyLoading should return true when status is wait');

  state.usm.policy.policyStatus = 'complete';
  assert.equal(isPolicyLoading(Immutable.from(state)), false, 'isPolicyLoading should return false when status is complete');
});

test('hasMissingRequiredData selector', function(assert) {
  const state = _.cloneDeep(fullState);
  state.usm.policy.policy = { ...policyData, name: null };
  assert.equal(hasMissingRequiredData(Immutable.from(state)), true, 'hasMissingRequiredData should return true when name is null');

  state.usm.policy.policy = { ...policyData, name: '' };
  assert.equal(hasMissingRequiredData(Immutable.from(state)), true, 'hasMissingRequiredData should return true when name is an empty string');

  state.usm.policy.policy = { ...policyData, name: '   ' };
  assert.equal(hasMissingRequiredData(Immutable.from(state)), true, 'hasMissingRequiredData should return true when name is all whitespace');

  state.usm.policy.policy = { ...policyData };
  assert.equal(hasMissingRequiredData(Immutable.from(state)), false, 'hasMissingRequiredData should return false when populated');
});

test('currentPolicy selector', function(assert) {
  const state = _.cloneDeep(fullState);
  state.usm.policy.policy = { ...policyData };
  assert.deepEqual(currentPolicy(Immutable.from(state)), policyData, 'The returned value from the policy selector is as expected');
});

test('startDate', function(assert) {
  assert.expect(2);
  const result = startDate(fullState);
  const today = moment().startOf('date').toDate().getTime();
  assert.deepEqual(result, today, 'should return today if start date is empty');

  const state2 = {
    usm: {
      policy: {
        policy: {
          scheduleConfig: {
            scheduleOptions: {
              scanStartDate: moment('01/10/2018', 'MM-DD-YYYY').toDate().getTime()
            }
          }
        }
      }
    }
  };
  const result2 = startDate(state2);
  assert.deepEqual(result2, moment('01/10/2018', 'MM-DD-YYYY').toDate().getTime(), 'should return unix millisecond format date');
});

test('startTime', function(assert) {
  const state = {
    usm: {
      policy: {
        policy: {
          scheduleConfig: {
            scheduleOptions: {
              scanStartTime: '10:45'
            }
          }
        }
      }
    }
  };
  const result2 = startTime(state);
  assert.deepEqual(result2, '10:45', 'should return time');
});

test('weekOptions', function(assert) {
  assert.expect(1);
  const state = {
    usm: {
      policy: {
        policy: {
          scheduleConfig: {
            scheduleOptions: {
              recurrenceIntervalUnit: 'WEEKS',
              runOnDaysOfWeek: ['SUNDAY']
            }
          }
        }
      }
    }
  };
  const result = weekOptions(state);
  const expected = {
    'week': 'SUNDAY',
    'isActive': true,
    'label': 'adminUsm.policy.scheduleConfiguration.recurrenceInterval.week.SUNDAY'
  };
  assert.deepEqual(result[0], expected, 'should add label and isActive');
});

test('runIntervalConfig', function(assert) {
  assert.expect(1);
  const state = {
    usm: {
      policy: {
        policy: {
          scheduleConfig: {
            scheduleOptions: {
              recurrenceIntervalUnit: 'WEEKS'
            }
          }
        }
      }
    }
  };
  const result = runIntervalConfig(state);
  const expected = {
    'options': [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24],
    'runLabel': 'adminUsm.policy.scheduleConfiguration.recurrenceInterval.intervalText.WEEKS'
  };
  assert.deepEqual(result, expected, 'should return the processed run interval configuration');
});
