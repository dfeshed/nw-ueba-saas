import reselect from 'reselect';
import { isEmpty } from '@ember/utils';
const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _queryStats = (state) => state.investigate.queryStats;
const _deviceStats = (state) => state.investigate.queryStats.devices;
const _errors = (state) => state.investigate.queryStats.errors;
const _warnings = (state) => state.investigate.queryStats.warnings;
const _devices = (state) => state.investigate.queryStats.devices;
const _services = (state) => state.investigate.services.serviceData;
const _streamingStartedTime = (state) => state.investigate.queryStats.streamingStartedTime;
const _streamingEndedTime = (state) => state.investigate.queryStats.streamingEndedTime;

// SELECTOR FUNCTIONS

export const slowestInQuery = createSelector(
  [_deviceStats],
  (deviceStats) => {
    let slowestIds = [];
    let slowestTime = 0;

    const findSlowest = (list = []) => {
      list.forEach((device) => {
        if (device.devices && device.devices.length) {
          findSlowest(device.devices);
        } else {
          if (device.elapsedTime && (slowestIds.length === 0)) {
            slowestTime = device.elapsedTime;
            slowestIds.push(device.serviceId);
          } else if (slowestTime && device.elapsedTime > slowestTime) {
            slowestTime = device.elapsedTime;
            slowestIds = [device.serviceId];
          } else if (device.elapsedTime && (device.elapsedTime === slowestTime)) {
            slowestIds.push(device.serviceId);
          }
        }
      });
    };

    findSlowest(deviceStats);

    if (slowestIds.length > 1 || slowestIds.length === 0) {
      return;
    } else {
      return slowestIds[0];
    }
  }
);

export const offlineServicesPath = createSelector(
  [_deviceStats],
  (deviceStats = []) => {
    const allOffline = [];

    const findOffline = (list = []) => {
      let foundOffline = false;
      list.forEach((device) => {
        if (!device.on || findOffline(device.devices)) {
          allOffline.push(device.serviceId);
          foundOffline = true;
        }
      });
      return foundOffline;
    };

    findOffline(deviceStats);
    return allOffline;
  }
);

export const offlineServices = createSelector(
  [_deviceStats],
  (deviceStats = []) => {
    const ids = [];

    const recursiveOffline = (list = []) => {
      list.forEach((device) => {
        if (!device.on) {
          ids.push(device.serviceId);
        } else if (device.devices && device.devices.length) {
          recursiveOffline(device.devices);
        }
      });
    };

    recursiveOffline(deviceStats);
    return ids;
  }
);

export const hasOfflineServices = createSelector(
  [offlineServices],
  (services) => {
    return services && services.length > 0;
  }
);

export const hasWarning = createSelector(
  [_warnings],
  (warnings) => {
    return warnings && warnings.length > 0;
  }
);

export const hasError = createSelector(
  [_errors, hasOfflineServices],
  (errors, offline) => {
    return (errors && errors.length > 0) || offline;
  }
);

export const isConsoleEmpty = createSelector(
  [_queryStats, _errors],
  (queryStats, errors) => {
    return queryStats.description === null && isEmpty(errors);
  }
);

// references devices because we only receive devices data when the query isComplete
// references errors because errors are fatal and indicate completion
export const isQueryComplete = createSelector(
  [_deviceStats, _errors],
  (devices = [], errors = []) => {
    return errors.length > 0 || devices.length > 0;
  }
);

const _formatErrorsAndWarnings = (list, services) => {
  if (!list || !services) {
    return;
  } else {
    return list.map((w) => {
      const service = services.findBy('id', w.serviceId);
      if (service) {
        return {
          ...w,
          serviceName: service.displayName
        };
      } else {
        return w;
      }
    });
  }
};

export const warningsWithServiceName = createSelector(
  [_warnings, _services],
  (warnings, services) => {
    return _formatErrorsAndWarnings(warnings, services);
  }
);

export const warningsPath = createSelector(
  [_deviceStats, warningsWithServiceName],
  (deviceStats, warnings = []) => {
    const allWarnings = [];
    const warningIds = warnings.map((w) => w.serviceId);

    const findWarning = (list = []) => {
      let foundWarning = false;
      list.forEach((device) => {
        if (warningIds.includes(device.serviceId) || findWarning(device.devices)) {
          allWarnings.push(device.serviceId);
          foundWarning = true;
        }
      });
      return foundWarning;
    };

    findWarning(deviceStats);
    return allWarnings;
  }
);

export const errorsWithServiceName = createSelector(
  [_errors, _services],
  (errors, services) => {
    return _formatErrorsAndWarnings(errors, services);
  }
);

export const decoratedDevices = createSelector(
  [_devices, _services],
  (devices, services) => {
    if (!devices || !services) {
      return [];
    } else {
      const process = (toProcess) => {
        if (!toProcess) {
          return [];
        }

        return toProcess.map((d) => {
          return {
            on: d.on,
            serviceId: d.serviceId,
            devices: process(d.devices),
            elapsedTime: d.elapsedTime,
            // handle case (mostly in dev env) where service under hood isn't
            // present by shimming in Unknown
            serviceName: (services.findBy('id', d.serviceId) || { displayName: 'Unknown' }).displayName
          };
        });
      };

      const updatedDevices = process(devices);
      return updatedDevices;
    }
  }
);

export const queryTimeElapsed = createSelector(
  [_devices],
  (devices = []) => {
    const [ device ] = devices;
    if (device) {
      if ((device.elapsedTime < 1) || (!device.elapsedTime)) {
        return '<1';
      } else {
        return `~${device.elapsedTime}`;
      }
    }
  }
);

export const streamingTimeElapsed = createSelector(
  [_streamingStartedTime, _streamingEndedTime],
  (streamingStartedTime, streamingEndedTime) => {
    const diff = streamingEndedTime - streamingStartedTime;
    if (diff > 0) {
      const toSeconds = diff / 1000;
      if (toSeconds < 1) {
        return '<1';
      } else {
        return `~${Math.round(toSeconds)}`;
      }
    }

  }
);
