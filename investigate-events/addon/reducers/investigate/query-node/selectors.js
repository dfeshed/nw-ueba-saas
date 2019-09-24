import reselect from 'reselect';

import TIME_RANGES from 'investigate-shared/constants/time-ranges';
import { TEXT_FILTER } from 'investigate-events/constants/pill';
import { selectedService, hasSummaryData } from 'investigate-events/reducers/investigate/services/selectors';
import { createQueryHash } from 'investigate-events/util/query-hash';
import { relevantOperators } from 'investigate-events/util/possible-operators';
import { encodeMetaFilterConditions } from 'investigate-shared/actions/api/events/utils';
import { validMetaKeySuggestions } from 'investigate-events/reducers/investigate/dictionaries/selectors';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _endTime = (state) => state.investigate.queryNode.endTime;
const _metaFilter = (state) => state.investigate.queryNode.metaFilter;
const _previouslySelectedTimeRanges = (state) => state.investigate.queryNode.previouslySelectedTimeRanges;
const _previousQueryParams = (state) => state.investigate.queryNode.previousQueryParams;
const _queryTimeFormat = (state) => state.investigate.queryNode.queryTimeFormat;
const _serviceId = (state) => state.investigate.queryNode.serviceId;
const _startTime = (state) => state.investigate.queryNode.startTime;
const _isTimeRangeInvalid = (state) => state.investigate.queryNode.timeRangeInvalid;
const _queryView = (state) => state.investigate.queryNode.queryView;
const _currentQueryHash = (state) => state.investigate.queryNode.currentQueryHash;
const _updatedFreeFormTextPill = (state) => state.investigate.queryNode.updatedFreeFormTextPill;

export const pillsData = (state) => state.investigate.queryNode.pillsData;


// SELECTOR FUNCTIONS
export const freeFormText = createSelector(
  [pillsData],
  (_pillsData) => {
    return encodeMetaFilterConditions(_pillsData).trim();
  }
);

const _isFreeFormTextUpdated = createSelector(
  [_updatedFreeFormTextPill, freeFormText],
  (updatedFreeFormTextPill, freeFormTextInState) => {
    if (!updatedFreeFormTextPill) {
      return false;
    }
    const updatedFreeForm = encodeMetaFilterConditions([updatedFreeFormTextPill]).trim();
    return updatedFreeForm !== freeFormTextInState;
  }
);

export const isPillBeingEdited = createSelector(
  [pillsData],
  (pills) => pills.some((d) => d.isEditing)
);

const _isDirty = createSelector(
  [_currentQueryHash, _serviceId, _startTime, _endTime, pillsData, _isFreeFormTextUpdated, isPillBeingEdited],
  (currentQueryHash, serviceId, startTime, endTime, pills, isFreeFormTextUpdated, isPillBeingEdited) => {
    // We check to see if a pill is being edited because the _pillData is
    // updated when entering edit mode, causing this selector to re-evaluate.
    if (isPillBeingEdited) {
      // Ignore dirty state while in the process of editing.
      return false;
    }
    const queryHash = createQueryHash(serviceId, startTime, endTime, pills);
    return (currentQueryHash !== queryHash) || isFreeFormTextUpdated;
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
  (id) => {
    return TIME_RANGES.getById(id);
  }
);

export const useDatabaseTime = createSelector(
  [_queryTimeFormat],
  (queryTimeFormat) => queryTimeFormat === TIME_RANGES.DATABASE_TIME
);

export const hasRequiredValuesToQuery = createSelector(
  [selectedService, hasSummaryData, selectedTimeRange, _isTimeRangeInvalid, _isDirty],
  (selectedService, hasSummaryData, selectedTimeRange, isTimeRangeInvalid, isDirty) => {
    return !!(selectedService && selectedService.id && hasSummaryData && selectedTimeRange && !isTimeRangeInvalid && isDirty);
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

export const isOnGuided = createSelector(
  [_queryView],
  (queryView) => queryView === 'guided'
);

/**
 * Returns an object that has the values required to execute a query. If the
 * query is marked as "dirty", we should use the values from the previous
 * query. This assures that API requests to get stuff like reconstructions and
 * more events still work as expected.
 * @public
 */
export const getActiveQueryNode = createSelector(
  [_endTime, _isDirty, _metaFilter, _previousQueryParams, _serviceId, _startTime],
  (endTime, isDirty, metaFilter, previousQueryParams, serviceId, startTime) => {
    if (isDirty && previousQueryParams) {
      return previousQueryParams;
    } else {
      return { endTime, metaFilter, serviceId, startTime };
    }
  }
);

export const hasInvalidPill = createSelector(
  [pillsData, isOnGuided],
  (_pillsData, isOnGuided) => isOnGuided && _pillsData.isAny('isInvalid')
);

const _twinProcessedPills = createSelector(
  [pillsData],
  (_pillsData) => {
    // See if a twin-able pill is focused
    const twin = _pillsData.find((pD) => !!pD.twinId && pD.isFocused);

    // no twin? done here, no work to do
    if (!twin) {
      return _pillsData;
    }

    // Finds twin of focused pill and flags for
    // twin focusing
    return _pillsData.map((pD) => {
      if (pD.twinId == twin.twinId && !pD.isFocused) {
        return {
          ...pD,
          isTwinFocused: true
        };
      }
      return pD;
    });
  }
);

export const enrichedPillsData = createSelector(
  [validMetaKeySuggestions, _twinProcessedPills],
  (metaKeys, _pillsData) => {
    // This transforms the meta/operator from state, which are just strings,
    // into the full operator/meta objects used by the components
    const newPillsData = _pillsData.map((pillData) => {
      const meta = metaKeys.find((mK) => mK.metaName === pillData.meta);
      const operator = relevantOperators(meta, pillData.operator).find((possOp) => possOp.displayName === pillData.operator);

      return {
        ...pillData,
        operator,
        meta
      };
    });

    return newPillsData;
  }
);

// If we have the required values to query and none of the pills are
// invalid, then we can query guided
export const canQueryGuided = createSelector(
  [hasInvalidPill, hasRequiredValuesToQuery],
  (hasInvalidPill, hasRequiredValuesToQuery) => hasRequiredValuesToQuery && !hasInvalidPill
);

export const selectedPills = createSelector(
  [pillsData],
  (_pillsData) => {
    return _pillsData.filter((pD) => pD.isSelected === true);
  }
);

export const focusedPill = createSelector(
  [pillsData],
  (_pillsData) => _pillsData.find((pD) => pD.isFocused)
);

export const deselectedPills = createSelector(
  [pillsData],
  (_pillsData) => {
    return _pillsData.filter((pD) => pD.isSelected === false);
  }
);

export const hasInvalidSelectedPill = createSelector(
  [selectedPills],
  (selectedPills) => selectedPills.isAny('isInvalid')
);

export const pillBeingEdited = createSelector(
  [pillsData],
  (_pillsData) => {
    const pillsBeingEdited = _pillsData.filter((pD) => pD.isEditing === true);

    // If there is one, return it, can only edit one at a time
    if (pillsBeingEdited.length > 0) {
      return pillsBeingEdited[0];
    }
  }
);

export const isPillValidationInProgress = createSelector(
  [pillsData],
  (_pillsData) => _pillsData.isAny('isValidationInProgress')
);

/**
 * Does the current query have a Text Filter?
 */
export const hasTextPill = createSelector(
  [pillsData],
  (_pillsData) => _pillsData.some((pD) => pD.type === TEXT_FILTER)
);

/**
 * Did the query that was executed have a Text Filter?
 */
export const hadTextPill = createSelector(
  [_previousQueryParams],
  (previousQueryParams) => {
    return !!(previousQueryParams &&
      previousQueryParams.metaFilter &&
      previousQueryParams.metaFilter.some((pD) => pD.type === TEXT_FILTER));
  }
);
