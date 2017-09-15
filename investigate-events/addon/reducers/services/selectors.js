import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
// These tell the selectors below _where_ to find the property in state.
// These functions are passed within the first Array argument to
// `createSelector()`
const _services = (state) => state.services.data;
const _endpointId = (state) => state.data.endpointId;

// SELECTOR FUNCTIONS
export const selectedService = createSelector(
  [_services, _endpointId],
  (services, endpointId) => {
    let ret = null;
    if (services && Array.isArray(services)) {
      ret = services.find((e) => e.id === endpointId);
    }
    return ret;
  }
);
