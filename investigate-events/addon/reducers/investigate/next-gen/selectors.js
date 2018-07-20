import reselect from 'reselect';

import { encodeMetaFilterConditions } from 'investigate-shared/actions/api/events/utils';
import { hasRequiredValuesToQuery } from 'investigate-events/reducers/investigate/query-node/selectors';
import { metaKeySuggestionsForQueryBuilder } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { relevantOperators } from 'investigate-events/util/possible-operators';

const { createSelector } = reselect;

const _pillsData = (state) => state.investigate.nextGen.pillsData;

// returns true if there is any invalid pill in state
const _hasInvalidPill = createSelector(
  [_pillsData],
  (pillsData) => {
    return pillsData.isAny('isInvalid');
  }
);

// This transforms the meta/operator from state, which are just strings,
// into the full operator/meta objects used by the components
export const enrichedPillsData = createSelector(
  [metaKeySuggestionsForQueryBuilder, _pillsData],
  (metaKeys, pillsData) => {
    return pillsData.map((pillData) => {
      const meta = metaKeys.find((mK) => mK.metaName === pillData.meta);
      const operator = relevantOperators(meta, pillData.operator).find((possOp) => possOp.displayName === pillData.operator);
      return {
        ...pillData,
        operator,
        meta
      };
    });
  }
);

// If we have the required values to query and none of the pills are
// invalid, then we can query next gen
export const canQueryNextGen = createSelector(
  [_hasInvalidPill, hasRequiredValuesToQuery],
  (hasInvalidPill, hasRequiredValuesToQuery) => hasRequiredValuesToQuery && !hasInvalidPill
);

export const selectedPills = createSelector(
  [_pillsData],
  (pillsData) => {
    return pillsData.filter((pD) => pD.isSelected === true);
  }
);

export const hasInvalidSelectedPill = createSelector(
  [selectedPills],
  (selectedPills) => selectedPills.isAny('isInvalid')
);

export const freeFormText = createSelector(
  [_pillsData],
  (pillsData) => {
    return encodeMetaFilterConditions(pillsData).trim();
  }
);