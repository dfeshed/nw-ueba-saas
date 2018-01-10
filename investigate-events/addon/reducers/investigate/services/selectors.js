import reselect from 'reselect';
import { uriEncodeEventQuery } from 'investigate-events/actions/helpers/query-utils';
import { lookup } from 'ember-dependency-lookup';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _services = (state) => state.investigate.services.serviceData;
const _queryNode = (state) => state.investigate.queryNode;
const _summaryData = (state) => state.investigate.services.summaryData;
const _isSummaryRetrieveError = (state) => state.investigate.services.isSummaryRetrieveError;

// this pattern filters out numbers after the first decimal place
//  a serviceId like 11.1.0.0 will be changed to 11.1
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
      ret = services.find((e) => e.id === serviceId);
    }
    return ret;
  }
);

export const getServiceDisplayName = createSelector(
  [selectedService],
  (selectedSvc) => {
    return selectedSvc === null ? null : selectedSvc.displayName;
  }
);

export const hasServices = createSelector(
  [_services],
  (services) => !!(services && services.length)
);

// If summaryData object exists in state and the service has collected some data, returns true
export const hasSummaryData = createSelector(
  [_summaryData],
  (summaryData) => !!summaryData && summaryData.startTime !== 0
);

// Checks if the summaryData is invalid.
// If the aggregation has never started on a service (zero startTime), it is considered invalid.
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
      if (coreVersion < appVersion) {
        return true;
      }
    }
  }
);

export const servicesWithURI = createSelector(
  [_services, _queryNode],
  (services, queryNode) => {
    const { startTime, endTime, metaFilter } = queryNode;
    let ret = [];
    if (services && Array.isArray(services)) {
      ret = services.map((service) => {
        const query = {
          serviceId: service.id,
          startTime,
          endTime,
          metaFilter: {
            uri: metaFilter.uri,
            conditions: []
          }
        };
        return service.merge({
          ...query,
          queryURI: uriEncodeEventQuery(query)
        });
      });
    }
    return ret;
  }
);
