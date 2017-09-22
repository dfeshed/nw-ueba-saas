import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
// These tell the selectors below _where_ to find the property in state.
// These functions are passed within the first Array argument to
// `createSelector()`
const _services = (state) => state.services.data;
const _serviceId = (state) => state.data.serviceId;

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
