import reselect from 'reselect';
import { lookup } from 'ember-dependency-lookup';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _services = (state) => state.investigate.services.serviceData;
const _summaryData = (state) => state.investigate.services.summaryData;
const _isSummaryRetrieveError = (state) => state.investigate.services.isSummaryRetrieveError;
const _summaryErrorMessage = (state) => state.investigate.services.summaryErrorMessage;

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

export const coreServiceNotUpdated = createSelector(
  [_summaryData],
  (summaryData) => {
    if (summaryData) {
      const coreVersion = Number(summaryData.version.match(serviceIdRegex)[0]);
      const appVersion = Number(lookup('service:appVersion').version.match(serviceIdRegex)[0]);
      return coreVersion < appVersion;
    }
  }
);

/**
 * For a selected service, we could have several messages. These are:
 * 1. Some error message returned from the server
 * 2. There is "No Data" for the service
 * 3. No message at all
 *
 * The error message from the server is trimmed like so:
 * Before - rsa.com.nextgen.classException: Failed to connect to broker:50003
 * After  - Failed to connect to broker:50003
 * Before - java.lang.NullPointerException
 * After  - java.lang.NullPointerException
 * @public
 */
export const selectedServiceMessage = createSelector(
  [coreServiceNotUpdated, isSummaryDataInvalid, hasSummaryData, _summaryErrorMessage],
  (coreServiceNotUpdated, isSummaryDataInvalid, hasSummaryData, summaryErrorMessage) => {
    const i18n = lookup('service:i18n');
    let title = null;
    if (isSummaryDataInvalid && summaryErrorMessage) {
      // Regex explained - `.*?` Makes the `.*` quantifier lazy, causing it to
      // match as few characters as possible.
      title = summaryErrorMessage.replace(/(.*?:)/, '');
    } else if (coreServiceNotUpdated) {
      title = i18n.t('investigate.services.coreServiceNotUpdated');
    } else if (!hasSummaryData) {
      title = i18n.t('investigate.services.noData');
    }
    return title;
  }
);
