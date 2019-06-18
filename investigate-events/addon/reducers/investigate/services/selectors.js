import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _queriedServiceId = (state) => state.investigate.queryNode.previousQueryParams.serviceId;
const _services = (state) => state.investigate.services.serviceData;
const _summaryData = (state) => state.investigate.services.summaryData;
const _isSummaryRetrieveError = (state) => state.investigate.services.isSummaryRetrieveError;
const _summaryErrorCode = (state) => state.investigate.services.summaryErrorCode;

/**
 * Helper function to detect if all of the services are above a certain version.
 * @param {array} services List of services
 * @param {number} minVersion Minimum service version that supports feature
 * @private
 */
const _servicesMeetMinimumVersion = (services, minVersion) => {
  // Need to check services.length instead of just checking if it's an array
  // because an Array.every() will return `true` if passed an empty array.
  return services && services.length > 0 && services.every((service) => {
    const { version } = service;
    // There is a specific situation in a Docker stack that can return `null`
    // for the service's version. We will ignore this service for determining if
    // all services meet the minimum required version. See ASOC-79476.
    return version === null || parseFloat(version) >= minVersion;
  });
};

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

const selectService = (services, serviceId) => {
  let ret = null;
  if (services && Array.isArray(services)) {
    const selectedService = services.find((e) => e.id === serviceId);
    ret = selectedService ? selectedService : services[0];
  }
  return ret;
};

export const selectedService = createSelector([_services, getServiceId], selectService);
export const queriedService = createSelector([_services, _queriedServiceId], selectService);

/**
 * If summaryData object exists in state and the service has collected some
 * data, returns true.
 * @public
 */
export const hasSummaryData = createSelector(
  [_summaryData],
  (summaryData) => !!summaryData && summaryData.startTime !== 0
);

export const hasFatalSummaryError = createSelector(
  [_isSummaryRetrieveError, _summaryErrorCode],
  (isSummaryRetrieveError, errorCode) => isSummaryRetrieveError && errorCode === 3
);

/**
 * Check if all of the available services can support Text searching of meta
 * data.
 * @public
 */
export const hasMinimumCoreServicesVersionForTextSearch = createSelector(
  [_services],
  (services) => _servicesMeetMinimumVersion(services, 11.3)
);

/**
 * Check if all of the available services can support column sorting.
 * @public
 */
export const hasMinimumCoreServicesVersionForColumnSorting = createSelector(
  [_services],
  (services) => _servicesMeetMinimumVersion(services, 11.4)
);
