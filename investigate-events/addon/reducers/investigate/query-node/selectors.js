import reselect from 'reselect';
import TIME_RANGES from 'investigate-events/constants/time-ranges';
import { selectedService, hasSummaryData } from 'investigate-events/reducers/investigate/services/selectors';
const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _endTime = (state) => state.investigate.queryNode.endTime;
const _serviceId = (state) => state.investigate.queryNode.serviceId;
const _startTime = (state) => state.investigate.queryNode.startTime;
const _metaFilter = (state) => state.investigate.queryNode.metaFilter;
const _previouslySelectedTimeRanges = (state) => state.investigate.queryNode.previouslySelectedTimeRanges;
const _queryTimeFormat = (state) => state.investigate.queryNode.queryTimeFormat;

export const queryString = (state) => state.investigate.queryNode.queryString;

// SELECTOR FUNCTIONS
export const hasMetaFilters = createSelector(
  [_metaFilter],
  (metaFilters) => metaFilters.conditions.length > 0
);

export const queryParams = createSelector(
  [_serviceId, _startTime, _endTime, _metaFilter],
  (serviceId, startTime, endTime, metaFilter) => {
    return { serviceId, startTime, endTime, metaFilter };
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
  [selectedService, hasSummaryData, selectedTimeRange],
  (selectedService, hasSummaryData, selectedTimeRange) => {
    return selectedService && selectedService.id && hasSummaryData && selectedTimeRange;
  }
);