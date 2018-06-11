import {
  conditionsFilter,
  encodeMetaFilterConditions,
  serviceIdFilter,
  streamPromiseRequest,
  thresholdFilter,
  timeRangeFilter,
  selectFilter
} from './utils';

export default function fetchDistinctCount(column, serviceId, startTime, endTime, conditions, language, threshold, cancelPreviouslyExecuting = true) {
  const query = {
    filter: [
      serviceIdFilter(serviceId),
      thresholdFilter(threshold),
      selectFilter(column),
      timeRangeFilter(startTime, endTime),
      conditionsFilter(encodeMetaFilterConditions(conditions))
    ]
  };
  return streamPromiseRequest(
    'core-event-count-distinct',
    query,
    { cancelPreviouslyExecuting }
  );
}
