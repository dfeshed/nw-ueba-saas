import reselect from 'reselect';
import _ from 'lodash';

import TIME_RANGES from 'investigate-shared/constants/time-ranges';
import { CLOSE_PAREN, OPEN_PAREN, TEXT_FILTER } from 'investigate-events/constants/pill';
import { selectedService, hasSummaryData } from 'investigate-events/reducers/investigate/services/selectors';
import { createQueryHash } from 'investigate-events/util/query-hash';
import { relevantOperators } from 'investigate-events/util/possible-operators';
import { markTextPillAttachedOperators } from 'investigate-events/util/logical-operator-helper';
import { encodeMetaFilterConditions } from 'investigate-shared/actions/api/events/utils';
import { validMetaKeySuggestions } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { isProfileExpanded } from 'investigate-events/reducers/investigate/profile/selectors';

const { createSelector, createSelectorCreator, defaultMemoize } = reselect;

// See https://github.com/reduxjs/reselect#q-why-is-my-selector-recomputing-when-the-input-state-stays-the-same
const createDeepEqualSelector = createSelectorCreator(
  defaultMemoize,
  _.isEqual
);

// ACCESSOR FUNCTIONS
const _endTime = (state) => state.investigate.queryNode.endTime;
const _metaFilter = (state) => state.investigate.queryNode.metaFilter;
const _previouslySelectedTimeRanges = (state) => state.investigate.queryNode.previouslySelectedTimeRanges;
const _previousQueryParams = (state) => state.investigate.queryNode.previousQueryParams;
const _queryTimeFormat = (state) => state.investigate.queryNode.queryTimeFormat;
const _serviceId = (state) => state.investigate.queryNode.serviceId;
const _startTime = (state) => state.investigate.queryNode.startTime;
const _isTimeRangeInvalid = (state) => state.investigate.queryNode.timeRangeInvalid;
const _currentQueryHash = (state) => state.investigate.queryNode.currentQueryHash;
const _updatedFreeFormText = (state) => state.investigate.queryNode.updatedFreeFormText;
const _pillDataHashes = (state) => state.investigate.queryNode.pillDataHashes;
const _originalPills = (state) => state.investigate.queryNode.originalPills || [];
const _isPillsDataStashed = (state) => state.investigate.queryNode.isPillsDataStashed;

export const pillsData = (state) => state.investigate.queryNode.pillsData;

const _onlyParenInfo = createSelector(
  [pillsData],
  (_pillsData) => {
    return _pillsData.map((pill) => ({
      isOpenParen: pill.type === OPEN_PAREN,
      isCloseParen: pill.type === CLOSE_PAREN
    }));
  }
);

// This transforms the meta/operator from state, which are just strings,
// into the full operator/meta objects used by the components
const _transformPills = (pills, metaKeys) => {
  return pills.map((pillData) => {
    const meta = metaKeys.find((mK) => mK.metaName === pillData.meta);
    const operator = relevantOperators(meta, pillData.operator).find((possOp) => possOp.displayName === pillData.operator);

    return {
      ...pillData,
      operator,
      meta
    };
  });
};

// SELECTOR FUNCTIONS
export const freeFormText = createSelector(
  [pillsData],
  (_pillsData) => {
    return encodeMetaFilterConditions(_pillsData).trim();
  }
);

export const queryNodeValuesForClassicUrl = createSelector(
  [_previouslySelectedTimeRanges, _pillDataHashes, _previousQueryParams],
  (previouslySelectedTimeRanges, pillDataHashes, previousQueryParams) => {
    // Must pick up params that have been executed in order to use them in the URL
    if (previousQueryParams) {
      const { endTime, startTime, serviceId } = previousQueryParams;
      const timeRangeType = previouslySelectedTimeRanges[serviceId];
      const textSearchTerm = previousQueryParams.metaFilter.find((pD) => pD.type === TEXT_FILTER);

      return {
        endTime,
        startTime,
        timeRangeType,
        serviceId,
        pillDataHashes,
        textSearchTerm
      };
    }
  }
);

const _isFreeFormTextUpdated = createSelector(
  [_updatedFreeFormText, freeFormText],
  (updatedFreeFormText, freeFormTextInState) => {
    if (!updatedFreeFormText) {
      return false;
    }
    return updatedFreeFormText !== freeFormTextInState;
  }
);

export const isPillBeingEdited = createSelector(
  [pillsData],
  (pills) => pills.some((d) => d.isEditing)
);

export const shouldUseStashedPills = createSelector(
  [_isPillsDataStashed, isProfileExpanded],
  (isPillsDataStashed, isProfileExpanded) => isPillsDataStashed && isProfileExpanded
);

export const isDirty = createSelector(
  [
    _currentQueryHash,
    _serviceId,
    _startTime,
    _endTime,
    pillsData,
    _isFreeFormTextUpdated,
    isPillBeingEdited,
    shouldUseStashedPills,
    _originalPills
  ],
  (
    currentQueryHash,
    serviceId,
    startTime,
    endTime,
    pills,
    isFreeFormTextUpdated,
    isPillBeingEdited,
    shouldUseStashedPills,
    originalPills
  ) => {
    // We check to see if a pill is being edited because the _pillData is
    // updated when entering edit mode, causing this selector to re-evaluate.
    if (isPillBeingEdited) {
      // Ignore dirty state while in the process of editing.
      return false;
    }
    const pillsToBeUsed = shouldUseStashedPills ? originalPills : pills;
    const queryHash = createQueryHash(serviceId, startTime, endTime, pillsToBeUsed);
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
  [selectedService, hasSummaryData, selectedTimeRange, _isTimeRangeInvalid, isDirty],
  (selectedService, hasSummaryData, selectedTimeRange, isTimeRangeInvalid, isDirty) => {
    return !!(selectedService && selectedService.id && hasSummaryData && selectedTimeRange && !isTimeRangeInvalid && isDirty);
  }
);

export const canFetchEvents = createSelector(
  [_serviceId, _startTime, _endTime],
  (serviceId, startTime, endTime) => !!(serviceId && startTime && endTime)
);

/**
 * Returns an object that has the values required to execute a query. If the
 * query is marked as "dirty", we should use the values from the previous
 * query. This assures that API requests to get stuff like reconstructions and
 * more events still work as expected.
 * @public
 */
export const getActiveQueryNode = createSelector(
  [_endTime, isDirty, _metaFilter, _previousQueryParams, _serviceId, _startTime],
  (endTime, isDirty, metaFilter, previousQueryParams, serviceId, startTime) => {
    if (isDirty && previousQueryParams) {
      return previousQueryParams;
    } else {
      return { endTime, metaFilter, serviceId, startTime };
    }
  }
);

export const hasInvalidPill = createSelector(
  [pillsData],
  (_pillsData) => _pillsData.isAny('isInvalid')
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

const _twinProcessedOGPills = createSelector(
  [_originalPills],
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
  [validMetaKeySuggestions, _twinProcessedPills, _twinProcessedOGPills],
  (metaKeys, _pillsData, _originalPills) => {
    return {
      pillsData: _transformPills(_pillsData, metaKeys) |> markTextPillAttachedOperators,
      originalPills: _transformPills(_originalPills, metaKeys) |> markTextPillAttachedOperators
    };
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

export const pillsInsideParens = createDeepEqualSelector(
  [_onlyParenInfo],
  (_onlyParenInfo) => {
    let parenDepth = 0;
    return [ ..._onlyParenInfo.map((pill) => {
      if (pill.isOpenParen) {
        return parenDepth++ > 0;
      } else if (pill.isCloseParen) {
        return parenDepth-- > 0;
      } else {
        return parenDepth > 0;
      }
    }), false];
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
