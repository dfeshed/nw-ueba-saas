import reselect from 'reselect';
import { isBlank } from '@ember/utils';
import _ from 'lodash';
import moment from 'moment';
import { RADIO_BUTTONS_CONFIG, SCAN_SCHEDULE_CONFIG } from 'admin-source-management/utils/settings';

const { createSelector } = reselect;

const policyState = (state) => state.usm.policy;

const weeks = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
// Key is type
const COUNTER = {
  'DAYS': [1, 2, 3, 4, 5, 6, 10, 15, 20],
  'WEEKS': [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24]
};

export const scheduleConfig = (state) => state.usm.policy.policy.scheduleConfig || {};
export const radioButtonConfig = () => RADIO_BUTTONS_CONFIG;
export const scanScheduleConfig = () => SCAN_SCHEDULE_CONFIG;


const availableSettings = (state) => state.usm.policy.availableSettings || {};

const selectedSettings = (state) => state.usm.policy.selectedSettings || {};

export const enabledAvailableSettings = createSelector(
  availableSettings,
  (availableSettings) => {
    return availableSettings.filter((el) => el.isEnabled);
  }
);

export const sortedSelectedSettings = createSelector(
  selectedSettings,
  (selectedSettings) => {
    return _.sortBy(selectedSettings, 'index');
  }
);

export const isPolicyLoading = createSelector(
  policyState,
  (policyState) => policyState.policyStatus === 'wait'
);

export const currentPolicy = createSelector(
  policyState,
  (policyState) => policyState.policy
);

export const hasMissingRequiredData = createSelector(
  currentPolicy,
  (policy) => {
    return isBlank(policy.name);
  }
);

export const isEnabled = createSelector(
  scheduleConfig,
  (scheduleConfig) => scheduleConfig.enabledScheduledScan
);

export const scheduleOptions = createSelector(
  scheduleConfig,
  (scheduleConfig) => _.cloneDeep(scheduleConfig.scheduleOptions) || {}
);

export const scanOptions = createSelector(
  scheduleConfig,
  (scheduleConfig) => scheduleConfig.scanOptions || { cpuMaximum: '80', cpuMaximumOnVirtualMachine: '90' }
);

export const startDate = createSelector(
  scheduleOptions,
  (scheduleOptions) => {
    return scheduleOptions.scanStartDate ? scheduleOptions.scanStartDate : moment().startOf('date').toDate().getTime();
  }
);

export const startTime = createSelector(
  scheduleOptions,
  (scheduleOptions) => {
    return scheduleOptions.scanStartTime ? scheduleOptions.scanStartTime : '10:00';
  }
);

const intervalType = createSelector(
  scheduleOptions,
  (schedule) => schedule.recurrenceIntervalUnit || 'DAYS'
);

export const isWeeklyInterval = createSelector(
  intervalType,
  (intervalUnit) => intervalUnit === 'WEEKS'
);

const runOnDaysOfWeek = createSelector(
  scheduleOptions,
  (schedule) => schedule.runOnDaysOfWeek || []
);

export const weekOptions = createSelector(
  intervalType, runOnDaysOfWeek,
  (intervalType, runOnDaysOfWeek) => {
    if (intervalType === 'WEEKS') {
      const config = weeks.map((week) => {
        const label = `adminUsm.policy.scheduleConfiguration.recurrenceInterval.week.${week}`;
        return {
          label,
          week,
          isActive: runOnDaysOfWeek.includes(week)
        };
      });
      return config;
    }
    return null;
  }
);

export const runIntervalConfig = createSelector(
  intervalType,
  (intervalType) => {
    const runLabel = `adminUsm.policy.scheduleConfiguration.recurrenceInterval.intervalText.${intervalType}`;
    const options = COUNTER[intervalType];
    return { runLabel, options };
  }
);

