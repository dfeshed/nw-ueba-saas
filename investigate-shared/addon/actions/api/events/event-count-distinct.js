import {
  conditionsFilter,
  encodeMetaFilterConditions,
  serviceIdFilter,
  streamPromiseRequest,
  thresholdFilter,
  timeRangeFilter,
  selectFilter
} from './utils';

export default function fetchDistinctCount(serviceId, startTime, endTime, conditions, language, threshold, cancelPreviouslyExecuting = true) {
  const query = {
    filter: [
      serviceIdFilter(serviceId),
      thresholdFilter(threshold),
      selectFilter('process.vid.src'),
      timeRangeFilter(startTime, endTime),
      conditionsFilter(encodeMetaFilterConditions(conditions, language))
    ]
  };
  return streamPromiseRequest(
    'core-event-count-distinct',
    query,
    { cancelPreviouslyExecuting }
  );
}
