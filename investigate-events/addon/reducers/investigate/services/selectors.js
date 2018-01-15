import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _services = (state) => state.investigate.services.serviceData;
const _summaryData = (state) => state.investigate.services.summaryData;
const _isSummaryRetrieveError = (state) => state.investigate.services.isSummaryRetrieveError;

// This pattern filters out numbers after the first decimal place
// A serviceId like 11.1.0.0 will be changed to 11.1
const serviceIdRegex = new RegExp(/\d*\.\d/);

export const getDbEndTime = (state) => {
  const { summaryData } = state.investigate.services;
  return summaryData ? summaryData.endTime : null;
};
export const getDbStartTime = (state) => {
  const { summaryData } = state.investigate.services;
  return summaryData ? summaryData.startTime : null;
};
// SELECTOR FUNCTIONS
export const getServiceId = (state) => state.investigate.queryNode.serviceId;

export const selectedService = createSelector(
  [_services, getServiceId],
  (services, serviceId) => {
    let ret = null;
    if (services && Array.isArray(services)) {
      const selectedService = services.find((e) => e.id === serviceId);
      ret = selectedService ? selectedService : services[0];
    }
    return ret;
  }
);

/**
 * If there is a `selectedService` return the `displayName`, otherwise return
 * "Service Unavailable".
 * @public
 */
export const getServiceDisplayName = createSelector(
  [selectedService],
  (selectedSvc) => {
    return selectedSvc === null ? 'Services Unavailable' : selectedSvc.displayName;
  }
);

/**
 * Need to get core version from endpoint (service) rather than summary
 * as summary will always return an error if there is mixed mode
 * @public
 */
export const getCoreDeviceVersion = createSelector(
  [selectedService],
  (selectedSvc) => {
    return selectedSvc === null ? null : selectedSvc.version;
  }
);

export const hasServices = createSelector(
  [_services],
  (services) => !!(services && services.length)
);

/**
 * If summaryData object exists in state and the service has collected some
 * data, returns true.
 * @public
 */
export const hasSummaryData = createSelector(
  [_summaryData],
  (summaryData) => !!summaryData && summaryData.startTime !== 0
);

/**
 * Checks if the summaryData is invalid. If the aggregation has never started on
 * a service (zero startTime), it is considered invalid.
 * @public
 */
export const isSummaryDataInvalid = createSelector(
  [_summaryData, _isSummaryRetrieveError],
  (summaryData, isSummaryRetrieveError) => {
    let ret = false;
    if ((summaryData && summaryData.startTime === 0) || isSummaryRetrieveError) {
      ret = true;
    }
    return ret;
  }
);

export const isCoreServiceNotUpdated = (state, appVersionService) => {
  const coreDeviceVersion = getCoreDeviceVersion(state);
  if (coreDeviceVersion && appVersionService) {
    const coreVersion = Number(coreDeviceVersion.match(serviceIdRegex)[0]);
    const appVersion = Number(appVersionService.match(serviceIdRegex)[0]);
    return coreVersion < appVersion;
  }
  return false;
};
