import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'springboard/actions/types';
import Immutable from 'seamless-immutable';

const dataInitialState = Immutable.from({
  springboards: [],
  fetchStatus: null,
  activeSpringboardId: null
});

const SpringboardReducer = handleActions({
  [ACTION_TYPES.FETCH_ALL_SPRINGBOARD]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('fetchStatus', 'wait'),
      success: (s) => s.merge({ springboards: action.payload.data.items, fetchStatus: 'completed' })
    });
  },

  [ACTION_TYPES.SET_ACTIVE_SPRINGBOARD_ID]: (state, action) => {
    return state.set('activeSpringboardId', action.payload);
  }

}, dataInitialState);

export default SpringboardReducer;