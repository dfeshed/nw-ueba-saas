import reselect from 'reselect';
import _ from 'lodash';
import moment from 'moment';
const { createSelector } = reselect;

const weeks = ['sunday', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday'];
// Key is type
const COUNTER = {
  'DAYS': [1, 2, 3, 4, 5, 6, 10, 15, 20],
  'WEEKS': [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24]
};

const fetchStatus = (state) => state.hostsScan.fetchScheduleStatus;

const scheduleConfig = (state) => state.hostsScan.config.scheduleConfig || {};

export const isFetchingSchedule = createSelector(
  fetchStatus,
  (fetchStatus) => fetchStatus === 'wait'
);

export const scheduleData = createSelector(
  scheduleConfig,
  (scheduleConfig) => _.cloneDeep(scheduleConfig.scheduleOptions) || {}
);

export const isEnabled = createSelector(
  scheduleConfig,
  (scheduleConfig) => scheduleConfig.enabled
);

export const cpuOptions = createSelector(
  scheduleConfig,
  (scheduleConfig) => scheduleConfig.scanOptions || { cpuMax: '80', cpuMaxVm: '90' }
);

export const startTime = createSelector(
  scheduleData,
  (scheduleData) => scheduleData.startTime
);

const intervalType = createSelector(
  scheduleData,
  (schedule) => schedule.recurrenceIntervalUnit || 'DAYS'
);

const runOnDays = createSelector(
  scheduleData,
  (schedule) => schedule.runOnDays || []
);

export const startDate = createSelector(
  scheduleData,
  (schedule) => {
    if (schedule.startDate) {
      return moment(schedule.startDate).toISOString();
    }
    return 'today';
  }
);

export const isWeeklyInterval = createSelector(
  intervalType,
  (intervalUnit) => intervalUnit === 'WEEKS'
);

export const weekOptions = createSelector(
  intervalType, runOnDays,
  (intervalType, runOnDays) => {
    if (intervalType === 'WEEKS') {
      const config = weeks.map((week, index) => {
        const label = `hostsScanConfigure.recurrenceInterval.week.${week}`;
        return {
          label,
          index,
          isActive: runOnDays.includes(index)
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
    const runLabel = `hostsScanConfigure.recurrenceInterval.intervalText.${intervalType}`;
    const options = COUNTER[intervalType];
    return { runLabel, options };
  }
);
