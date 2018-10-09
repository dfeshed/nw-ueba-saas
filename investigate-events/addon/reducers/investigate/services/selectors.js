import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _queriedServiceId = (state) => state.investigate.queryNode.previousQueryParams.serviceId;
const _services = (state) => state.investigate.services.serviceData;
const _summaryData = (state) => state.investigate.services.summaryData;
const _isSummaryRetrieveError = (state) => state.investigate.services.isSummaryRetrieveError;
const _summaryErrorCode = (state) => state.investigate.services.summaryErrorCode;

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
