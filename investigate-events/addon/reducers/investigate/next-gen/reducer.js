import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import _ from 'lodash';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  pillsData: []
});

export default handleActions({
  [ACTION_TYPES.ADD_NEXT_GEN_PILL]: (state, { payload }) => {
    const { pillData, position } = payload;
    const newPillData = {
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

    const position = state.pillsData.map((pD) => pD.id).indexOf(pillData.id);

    const newPillsData = Immutable.from([
      ...state.pillsData.slice(0, position),
      { ...pillData },
      ...state.pillsData.slice(position + 1)
    ]);

    return state.set('pillsData', newPillsData);
  },

  [ACTION_TYPES.DELETE_NEXT_GEN_PILL]: (state, { payload }) => {
    const { pillData: { id } } = payload;
    const newPills = state.pillsData.filter((pD) => pD.id !== id);
    return state.set('pillsData', newPills);
  }

}, _initialState);
