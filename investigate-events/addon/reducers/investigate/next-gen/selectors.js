import reselect from 'reselect';
const { createSelector } = reselect;

import {
  metaKeySuggestionsForQueryBuilder
} from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { all as possibleOperators } from 'investigate-events/util/possible-operators';

const _pillsData = (state) => state.investigate.nextGen.pillsData;

// This transforms the meta/operator from state, which are just strings,
// into the full operator/meta objects used by the components
export const enrichedPillsData = createSelector(
  [metaKeySuggestionsForQueryBuilder, _pillsData],
  (metaKeys, pillsData) => {
    return pillsData.map((pillData) => {
      return {
        value: pillData.value,
        operator: possibleOperators.find((possOp) => possOp.displayName === pillData.operator),
        meta: metaKeys.find((mK) => mK.metaName === pillData.meta),
        id: pillData.id,
        isInvalid: pillData.isInvalid,
        validationError: pillData.validationError
      };
    });
  }
);