import { createSelector } from 'reselect';

export const serviceId = (state) => state.investigate.serviceId;

const _timeRange = (state) => state.investigate.timeRange;
export const startTime = (state) => state.investigate.startTime;
export const endTime = (state) => state.investigate.endTime;

export const timeRange = createSelector(
  _timeRange,
  (timeRange) => {
    return timeRange;
  }
);
