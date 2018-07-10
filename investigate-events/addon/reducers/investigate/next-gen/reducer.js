import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import _ from 'lodash';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  pillsData: [],
  serverSideValidationInProcess: false
});

const _initialPillState = {
  id: undefined,
  meta: undefined,
  operator: undefined,
  value: undefined,
  isEditing: false,
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

const _handlePillUpdate = (state, pillData) => {
  const newPillsData = _replacePill(state, pillData);
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
    return _handlePillUpdate(state, payload.pillData);
  },

  [ACTION_TYPES.VALIDATE_NEXT_GEN_PILL]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('serverSideValidationInProcess', !!action.meta.isServerSide),
      failure: (s) => {
        const { meta: { position } } = action;
        const { pillsData } = s;
        const currentPill = pillsData[position];
        const validatedPill = {
          ...currentPill,
          isInvalid: !!action.payload.meta,
          validationError: action.payload.meta
        };
        const newPillsData = _replacePill(s, validatedPill);
        return s.merge({ pillsData: newPillsData, serverSideValidationInProcess: false });
      },
      success: (s) => s.set('serverSideValidationInProcess', false)
    });
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
  },

  [ACTION_TYPES.OPEN_NEXT_GEN_PILL_FOR_EDIT]: (state, { payload }) => {
    const newPillData = {
      ...payload.pillData,
      isSelected: false,
      isEditing: true
    };
    return _handlePillUpdate(state, newPillData);
  }


}, _initialState);
