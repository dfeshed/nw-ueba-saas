import reselect from 'reselect';
const { createSelector } = reselect;
import { hasRequiredValuesToQuery } from 'investigate-events/reducers/investigate/query-node/selectors';
import {
  metaKeySuggestionsForQueryBuilder
} from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { relevantOperators } from 'investigate-events/util/possible-operators';

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

// checks if there is a service selected, has summary data, timerange and isDirty
// along with if pillsData has any invalid pill
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