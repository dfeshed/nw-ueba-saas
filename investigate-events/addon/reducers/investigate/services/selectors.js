import reselect from 'reselect';
import { uriEncodeEventQuery } from 'investigate-events/actions/helpers/query-utils';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _services = (state) => state.investigate.services.data;
const _serviceId = (state) => state.investigate.queryNode.serviceId;
const _queryNode = (state) => state.investigate.queryNode;
const _summaryData = (state) => state.investigate.services.summaryData;

export const getDbEndTime = (state) => state.investigate.services.summaryData.endTime;
export const getDbStartTime = (state) => state.investigate.services.summaryData.startTime;
// SELECTOR FUNCTIONS
export const selectedService = createSelector(
  [_services, _serviceId],
  (services, serviceId) => {
    let ret = null;
    if (services && Array.isArray(services)) {
      ret = services.find((e) => e.id === serviceId);
    }
    return ret;
  }
);

export const hasServices = createSelector(
  [_services],
  (services) => !!(services && services.length)
);

// If summaryData object exists in state, returns true
export const hasSummaryData = createSelector(
  [_summaryData],
  (summaryData) => !!summaryData
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