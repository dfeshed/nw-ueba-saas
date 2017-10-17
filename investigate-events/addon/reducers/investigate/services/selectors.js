import reselect from 'reselect';
import { uriEncodeEventQuery } from 'investigate-events/actions/helpers/query-utils';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _services = (state) => state.investigate.services.data;
const _serviceId = (state) => state.investigate.queryNode.serviceId;
const _queryNode = (state) => state.investigate.queryNode;

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