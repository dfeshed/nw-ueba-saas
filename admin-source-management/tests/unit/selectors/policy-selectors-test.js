import { module, /* test, */ skip } from 'qunit';
import Immutable from 'seamless-immutable';
import _ from 'lodash';
import moment from 'moment';

// ****************************************************************************
// skipping all tests as the create policy component is being replaced...
// we'll delete this once the new policy wizard is finished and is fully tested
// ****************************************************************************

import {
  isPolicyLoading,
  currentPolicy,
  hasMissingRequiredData,
  weekOptions,
  startDate,
  startTime,
  runIntervalConfig,
  enabledAvailableSettings,
  sortedSelectedSettings
} from 'admin-source-management/reducers/usm/policy-selectors';

module('Unit | Selectors | Policy Selectors');

const fullState = {
  usm: {
    policy: {
      policy: {
        name: '',
        description: '',
        scheduleConfig: {
          scanType: 'SCHEDULED',
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
      policyStatus: null, // wait, complete, error
      availableSettings: [
        { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: true, isGreyedOut: false, component: 'usm-policies/policy/schedule-config/scan-schedule' },
        { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: true, isGreyedOut: true, component: 'usm-policies/policy/schedule-config/effective-date' }
      ],
      selectedSettings: []
    }
  }
};

const policyData = {
  'id': 'policy_001',
  'name': 'Policy 001',
  'description': 'Policy Description'
};

skip('isPolicyLoading selector', function(assert) {
  const state = _.cloneDeep(fullState);
  state.usm.policy.policyStatus = 'wait';
  assert.equal(isPolicyLoading(Immutable.from(state)), true, 'isPolicyLoading should return true when status is wait');

  state.usm.policy.policyStatus = 'complete';
  assert.equal(isPolicyLoading(Immutable.from(state)), false, 'isPolicyLoading should return false when status is complete');
});

skip('hasMissingRequiredData selector', function(assert) {
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

skip('currentPolicy selector', function(assert) {
  const state = _.cloneDeep(fullState);
  state.usm.policy.policy = { ...policyData };
  assert.deepEqual(currentPolicy(Immutable.from(state)), policyData, 'The returned value from the policy selector is as expected');
});

skip('startDate', function(assert) {
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

skip('startTime', function(assert) {
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

skip('weekOptions', function(assert) {
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

skip('enabledAvailableSettings only renders settings with isEnabled set', function(assert) {
  assert.expect(1);
  const state = {
    usm: {
      policy: {
        availableSettings: [
          { index: 0, id: 'schedOrManScan', isEnabled: true },
          { index: 1, id: 'effectiveDate', isEnabled: false }
        ]
      }
    }
  };
  const result = enabledAvailableSettings(state);
  assert.deepEqual(result.length, 1, 'availableSettingToRender should not render when isEnabled is false');
});

skip('sortedSelectedSettings renders settings in the order of index', function(assert) {
  assert.expect(1);
  const state = {
    usm: {
      policy: {
        selectedSettings: [
          { index: 3, id: 'schedOrManScan' },
          { index: 1, id: 'effectiveDate' },
          { index: 2, id: 'cpuFrequency' }
        ]
      }
    }
  };
  const result = sortedSelectedSettings(state);
  assert.deepEqual(result[0].index, 1, 'selectedSettingToRender correctly sorted settings based on the index');
});

skip('runIntervalConfig', function(assert) {
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
