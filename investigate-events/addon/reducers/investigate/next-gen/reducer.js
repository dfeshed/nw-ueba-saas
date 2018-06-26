import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import _ from 'lodash';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  pillsData: []
});

const _initialPillState = {
  id: undefined,
  meta: undefined,
  operator: undefined,
  value: undefined,
  isSelected: false,
  isInvalid: false,
  validationError: undefined
};

// Takes in state and a new pill, finds the old pill in
// state and replaces it with a new version
const _replacePill = (state, pillData) => {
  const position = state.pillsData.map((pD) => pD.id).indexOf(pillData.id);

  const newPillData = {
    ...pillData,
    id: _.uniqueId('nextGenPill_')
  };

  return Immutable.from([
    ...state.pillsData.slice(0, position),
    { ...newPillData },
    ...state.pillsData.slice(position + 1)
  ]);
};

const handlePillSelection = (state, payload, isSelected) => {
  const { pillData } = payload;
  const selectIds = pillData.map((pD) => pD.id);
  const newPillsData = state.pillsData.map((pD) => {
    if (selectIds.includes(pD.id)) {
      return {
        ...pD,
        id: _.uniqueId('nextGenPill_'),
        isSelected
      };
    }

    return pD;
  });
  return state.set('pillsData', newPillsData);
};

export default handleActions({
  [ACTION_TYPES.ADD_NEXT_GEN_PILL]: (state, { payload }) => {
    const { pillData, position } = payload;
    const newPillData = {
      ..._initialPillState,
      ...pillData,
      id: _.uniqueId('nextGenPill_')
    };
    if (state.pillsData.length === 0) {
      return state.set('pillsData', Immutable.from([ newPillData ]));
    }

    return state.set('pillsData', Immutable.from([
      ...state.pillsData.slice(0, position),
      newPillData,
      ...state.pillsData.slice(position)
    ]));
  },

  [ACTION_TYPES.EDIT_NEXT_GEN_PILL]: (state, { payload }) => {
    const { pillData } = payload;
    const newPillsData = _replacePill(state, pillData);
    return state.set('pillsData', newPillsData);
  },

  [ACTION_TYPES.VALIDATE_NEXT_GEN_PILL]: (state, { payload }) => {
    const { validatedPillData } = payload;
    const newPillsData = _replacePill(state, validatedPillData);
    return state.set('pillsData', newPillsData);
  },

  [ACTION_TYPES.DELETE_NEXT_GEN_PILLS]: (state, { payload }) => {
    const { pillData } = payload;
    const deleteIds = pillData.map((pD) => pD.id);
    const newPills = state.pillsData.filter((pD) => !deleteIds.includes(pD.id));
    return state.set('pillsData', newPills);
  },

  [ACTION_TYPES.SELECT_NEXT_GEN_PILLS]: (state, { payload }) => {
    return handlePillSelection(state, payload, true);
  },

  [ACTION_TYPES.DESELECT_NEXT_GEN_PILLS]: (state, { payload }) => {
    return handlePillSelection(state, payload, false);
  }

}, _initialState);
