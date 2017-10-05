import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _anchor = (state) => state.queryNode.results.events.anchor;
const _goal = (state) => state.queryNode.results.events.goal;
const _resultsData = (state) => state.queryNode.results.events.data;
const _status = (state) => state.queryNode.results.events.status;

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
