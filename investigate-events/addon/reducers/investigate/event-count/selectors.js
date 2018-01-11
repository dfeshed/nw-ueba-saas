import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _threshold = (state) => state.investigate.eventCount.threshold;
const _count = (state) => state.investigate.eventCount.data;
const _eventCountStatus = (state) => state.investigate.eventCount.status;
const _eventErrorCode = (state) => state.investigate.eventCount.reason;

// SELECTOR FUNCTIONS
export const resultCountAtThreshold = createSelector(
  [_count, _threshold],
  (count, threshold) => count === threshold
);

export const isInvalidQuery = createSelector(
  [_eventCountStatus, _eventErrorCode],
  (status, errorCode) => {
    if ((status !== undefined && errorCode !== undefined) && status === 'rejected' && errorCode === 11) {
      return true;
    }
  }
);