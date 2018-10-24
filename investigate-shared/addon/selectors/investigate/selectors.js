import { createSelector } from 'reselect';

export const serviceId = (state) => state.investigate.serviceId;

const _timeRange = (state) => state.investigate.timeRange;

export const timeRange = createSelector(
  _timeRange,
  (timeRange) => {
    return timeRange;
  }
);
