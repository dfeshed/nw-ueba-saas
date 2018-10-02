import reselect from 'reselect';
const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _queryStats = (state) => state.investigate.queryStats;
const _description = (state) => state.investigate.queryStats.description;
const _deviceStats = (state) => state.investigate.queryStats.devices;
const _percent = (state) => state.investigate.queryStats.percent;
const _errors = (state) => state.investigate.queryStats.errors;
const _warnings = (state) => state.investigate.queryStats.warnings;
const _devices = (state) => state.investigate.queryStats.devices;
const _services = (state) => state.investigate.services.serviceData;

// SELECTOR FUNCTIONS

export const slowestInQuery = createSelector(
  [_deviceStats],
  (deviceStats) => {
    let slowestIds = [];
    let slowestTime = 0;

    const findSlowest = (list = []) => {
      list.forEach((device) => {
        if (slowestIds.length === 0) {
          slowestTime = device.elapsedTime;
          slowestIds.push(device.serviceId);
        } else if (device.elapsedTime > slowestTime) {
          slowestTime = device.elapsedTime;
          slowestIds = [device.serviceId];
        } else if (device.elapsedTime === slowestTime) {
          slowestIds.push(device.serviceId);
        }

        if (device.devices && device.devices.length) {
          findSlowest(device.devices);
        }
      });
    };

    findSlowest(deviceStats);
    return slowestIds;
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
  [_queryStats],
  (queryStats) => {
    return queryStats.description === null;
  }
);

export const isProgressBarDisabled = createSelector(
  [_percent, _description],
  (percent, description) => {
    return (percent === 0) && description && description.toLowerCase() === 'queued';
  }
);

// references devices because we only receive devices data when the query isComplete
// references errors because errors are fatal and indicate completion
export const isComplete = createSelector(
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
            elapsedTime: d.elapsedTime / 1000,
            serviceName: services.findBy('id', d.serviceId).displayName
          };
        });
      };

      const updatedDevices = process(devices);
      return updatedDevices;
    }
  }
);
