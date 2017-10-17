import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _anchor = (state) => state.investigate.eventResults.anchor;
const _goal = (state) => state.investigate.eventResults.goal;
const _resultsData = (state) => state.investigate.eventResults.data;
const _status = (state) => state.investigate.eventResults.status;

// SELECTOR FUNCTIONS
export const percentageOfEventsDataReturned = createSelector(
  [_anchor, _goal, _resultsData, _status],
  (anchor, goal, data, status) => {
    let ret = 0;
    if (status) {
      if (status === 'complete') {
        ret = 100;
      } else {
        const spread = goal - anchor;
        const len = data && data.length || 0;
        if (spread && spread > 0) {
          ret = parseInt(100 * (len - anchor) / spread, 10);
        }
      }
    }
    return ret;
  }
);
