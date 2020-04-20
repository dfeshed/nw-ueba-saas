import reselect from 'reselect';
import { coreServiceNotUpdated } from 'component-lib/utils/core-services';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _services = (state) => state.services.serviceData;
const _summaryData = (state) => state.services.summaryData;
const _isSummaryRetrieveError = (state) => state.services.isSummaryRetrieveError;

// SELECTOR FUNCTIONS
export const getServiceId = (state) => state.serviceId;

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
  [_summaryData, _isSummaryRetrieveError],
  (summaryData, isSummaryRetrieveError) => {
    // this is required because endpoint does not have summaryData
    if (!summaryData) {
      return !isSummaryRetrieveError;
    }
    return !!summaryData && summaryData.startTime !== 0;
  }
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

export const isCoreServiceNotUpdated = (state, minServiceVersion) => {
  const coreDeviceVersion = getCoreDeviceVersion(state);
  return coreServiceNotUpdated(coreDeviceVersion, minServiceVersion);
};
