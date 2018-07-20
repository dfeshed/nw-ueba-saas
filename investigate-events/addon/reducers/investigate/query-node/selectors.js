import reselect from 'reselect';

import TIME_RANGES from 'investigate-shared/constants/time-ranges';
import { selectedService, hasSummaryData } from 'investigate-events/reducers/investigate/services/selectors';
import { createQueryHash } from 'investigate-events/util/query-hash';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _endTime = (state) => state.investigate.queryNode.endTime;
const _eventMetas = (state) => state.investigate.queryNode.eventMetas;
const _metaFilter = (state) => state.investigate.queryNode.metaFilter;
const _previouslySelectedTimeRanges = (state) => state.investigate.queryNode.previouslySelectedTimeRanges;
const _previousQueryParams = (state) => state.investigate.queryNode.previousQueryParams;
const _queryTimeFormat = (state) => state.investigate.queryNode.queryTimeFormat;
const _serviceId = (state) => state.investigate.queryNode.serviceId;
const _startTime = (state) => state.investigate.queryNode.startTime;
const _queryView = (state) => state.investigate.queryNode.queryView;

const _currentQueryHash = (state) => state.investigate.nextGen.currentQueryHash;
const _pillsData = (state) => state.investigate.nextGen.pillsData;

// SELECTOR FUNCTIONS
const _isDirty = createSelector(
  [_currentQueryHash, _serviceId, _startTime, _endTime, _pillsData],
  (currentQueryHash, serviceId, startTime, endTime, pills) => {
    const queryHash = createQueryHash(serviceId, startTime, endTime, pills);
    return currentQueryHash !== queryHash;
  }
);

export const selectedTimeRangeId = createSelector(
  [_serviceId, _previouslySelectedTimeRanges],
  (serviceId, previouslySelectedTimeRanges) => {
    const last = previouslySelectedTimeRanges[serviceId];
    return last ? last : TIME_RANGES.DEFAULT_TIME_RANGE_ID;
  }
);

export const selectedTimeRangeName = createSelector(
  [_serviceId, _previouslySelectedTimeRanges],
  (serviceId, previouslySelectedTimeRanges) => {
    const last = previouslySelectedTimeRanges[serviceId];
    return last ? TIME_RANGES.getNameById(last) : '';
  }
);

export const selectedTimeRange = createSelector(
  [selectedTimeRangeId],
  (id) => TIME_RANGES.getById(id)
);

export const useDatabaseTime = createSelector(
  [_queryTimeFormat],
  (queryTimeFormat) => queryTimeFormat === TIME_RANGES.DATABASE_TIME
);

export const hasRequiredValuesToQuery = createSelector(
  [selectedService, hasSummaryData, selectedTimeRange, _isDirty],
  (selectedService, hasSummaryData, selectedTimeRange, isDirty) => {
    return !!(selectedService && selectedService.id && hasSummaryData && selectedTimeRange && isDirty);
  }
);

export const canFetchEvents = createSelector(
  [_serviceId, _startTime, _endTime],
  (serviceId, startTime, endTime) => !!(serviceId && startTime && endTime)
);

export const isOnFreeForm = createSelector(
  [_queryView],
  (queryView) => queryView === 'freeForm'
);

export const isOnNextGen = createSelector(
  [_queryView],
  (queryView) => queryView === 'nextGen'
);

/**
 * Returns an object that has the values required to execute a query. If the
 * query is marked as "dirty", we should use the values from the previous
 * query. This assures that API requests to get stuff like reconstructions and
 * more events still work as expected.
 * @public
 */
export const getActiveQueryNode = createSelector(
  [_endTime, _eventMetas, _isDirty, _metaFilter, _previousQueryParams, _serviceId, _startTime],
  (endTime, eventMetas, isDirty, metaFilter, previousQueryParams, serviceId, startTime) => {
    if (isDirty && previousQueryParams) {
      return previousQueryParams;
    } else {
      return { endTime, eventMetas, metaFilter, serviceId, startTime };
    }
  }
);
