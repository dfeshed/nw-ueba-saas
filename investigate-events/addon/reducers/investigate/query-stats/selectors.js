import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _queryStats = (state) => state.investigate.queryStats;
const _description = (state) => state.investigate.queryStats.description;
const _deviceStats = (state) => state.investigate.queryStats.devices;
const _percent = (state) => state.investigate.queryStats.percent;
const _errors = (state) => state.investigate.queryStats.errors;
const _warnings = (state) => state.investigate.queryStats.warnings;

// SELECTOR FUNCTIONS

export const slowestInQuery = createSelector(
  [_deviceStats],
  (deviceStats) => {
    const times = deviceStats.map((d) => d.elapsedTime);
    const min = Math.max(...times);
    return deviceStats.filter((d) => d.elapsedTime === min).map((d) => d.serviceId);
  }
);

export const offlineServices = createSelector(
  [_deviceStats],
  (deviceStats) => {
    if (!deviceStats) {
      return [];
    } else {
      return deviceStats.filter((d) => !d.on);
    }
  }
);

export const hasWarning = createSelector(
  [_warnings],
  (warnings) => {
    return warnings && warnings.length > 0;
  }
);

export const hasError = createSelector(
  [_errors, offlineServices],
  (errors, offline) => {
    return (errors && errors.length > 0) || (offline && offline.length > 0);
  }
);

export const serviceshasErrorOrWarning = createSelector(
  [_errors, _warnings],
  (errors, warnings) => {
    let errorIds = [];
    let warningIds = [];

    if (errors) {
      errorIds = errors.map((e) => e.serviceId);
    }

    if (warnings) {
      warningIds = warnings.map((e) => e.serviceId);
    }

    return [...errorIds, ...warningIds];
  }
);

export const isConsoleEmpty = createSelector(
  [_queryStats],
  (queryStats) => {
    return queryStats.description === null;
  }
);

export const isProgressBarDisabled = createSelector(
  [_percent, _description],
  (percent, description) => {
    return (percent === 0) && description === 'Queued';
  }
);
