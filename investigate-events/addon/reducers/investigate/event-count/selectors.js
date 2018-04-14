import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _threshold = (state) => state.investigate.eventCount.threshold;
const _count = (state) => state.investigate.eventCount.data;

// SELECTOR FUNCTIONS
export const resultCountAtThreshold = createSelector(
  [_count, _threshold],
  (count, threshold) => count === threshold
);