import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _endTime = (state) => state.investigate.queryNode.endTime;
const _serviceId = (state) => state.investigate.queryNode.serviceId;
const _startTime = (state) => state.investigate.queryNode.startTime;
const _metaFilter = (state) => state.investigate.queryNode.metaFilter;

export const queryString = (state) => state.investigate.queryNode.queryString;

// SELECTOR FUNCTIONS
export const queryParams = createSelector(
  [_serviceId, _startTime, _endTime, _metaFilter],
  (serviceId, startTime, endTime, metaFilter) => {
    return { serviceId, startTime, endTime, metaFilter };
  }
);

export const hasMetaFilters = createSelector(
  [_metaFilter],
  (metaFilters) => metaFilters.conditions.length > 0
);
