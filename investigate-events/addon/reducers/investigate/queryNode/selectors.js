import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _anchor = (state) => state.queryNode.results.events.anchor;
const _goal = (state) => state.queryNode.results.events.goal;
const _resultsData = (state) => state.queryNode.results.events.data;
const _status = (state) => state.queryNode.results.events.status;

const _endTime = (state) => state.investigate.queryNode.endTime;
const _serviceId = (state) => state.investigate.queryNode.serviceId;
const _startTime = (state) => state.investigate.queryNode.startTime;
const _metaFilter = (state) => state.investigate.queryNode.metaFilter;

export const queryString = (state) => state.investigate.queryNode.queryString;

// SELECTOR FUNCTIONS
export const percentageOfEventsDataReturned = createSelector(
  [_anchor, _resultsData, _goal, _status],
  (anchor, data, goal, status) => {
    let ret = 100;
    if (status !== 'complete') {
      const spread = goal - anchor;
      const len = data.length || 0;
      if (spread && spread > 0) {
        ret = parseInt(100 * (len - anchor) / spread, 10);
      } else {
        ret = 0;
      }
    }
    return ret;
  }
);

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
