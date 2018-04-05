import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _anchor = (state) => state.investigate.eventResults.anchor;
const _goal = (state) => state.investigate.eventResults.goal;
const _resultsData = (state) => state.investigate.eventResults.data;
const _status = (state) => state.investigate.eventResults.status;
const _sessionId = (state) => state.investigate.queryNode.sessionId;

export const eventResultsErrorMessage = (state) => state.investigate.eventResults.message;

export const isEventResultsError = createSelector(
  [_status],
  (status) => status === 'error'
);

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

export const selectedIndex = createSelector(
  [_sessionId, _resultsData], // sessionId not set on refresh
  (sessionId, data) => {
    let idx = -1;
    if (sessionId && data && data.length) {
      idx = _indexOfBy(data, 'sessionId', sessionId);
    }
    return idx;
  }
);

/**
 * Determines if we should show a message directing the user to scroll down to
 * see their selected event.
 * @private
 */
export const showScrollMessage = createSelector(
  [selectedIndex, _sessionId],
  (selectedIndex, sessionId) => sessionId && selectedIndex < 0
);

/**
 * Finds and returns the index of the first array member whose key matches a
 * given value. Will use `Array.findIndex()` if supported.
 * @param {array} arr The array to be searched
 * @param {string} key The name of the attribute whose value is to be matched
 * @param {*} value The attribute value to be matched
 * @private
 */
const _indexOfBy = (arr, key, value) => {
  let _index = -1;
  arr = Array.isArray(arr) ? arr : [];
  if (arr.findIndex) {
    _index = arr.findIndex((item) => item[key] === value);
  } else {
    let i = 0;
    const len = arr.length;
    for (i; i < len; i++) {
      if (arr[i][key] === value) {
        _index = i;
        break;
      }
    }
  }
  return _index;
};
