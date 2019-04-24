import * as ACTION_TYPES from '../../actions/types';
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

const resetState = Immutable.from({
  isListView: true,
  errorMessage: null,
  list: [],
  entityType: null
});

const initialState = Immutable.from(resetState);

const list = handleActions({
  [ACTION_TYPES.INITIALIZE_ADD_TO_LIST_PARAM]: (state, { payload }) => {
    return state.merge({ isListView: true, entityType: payload, list: [], errorMessage: null });
  },

  [ACTION_TYPES.TOGGLE_LIST_VIEW]: (state, { payload }) => {
    return state.set('isListView', payload);
  },

  [ACTION_TYPES.SET_ALL_LIST]: (state, { payload }) => {
    return state.set('list', payload);
  },

  [ACTION_TYPES.CREATE_LIST]: (state, { payload }) => {
    const list = state.list.asMutable();
    return state.merge({ list: list.concat(payload), isListView: true });
  },

  [ACTION_TYPES.RESET_ERROR]: (state) => state.merge({
    errorMessage: null
  }),

  [ACTION_TYPES.LIST_ERROR]: (state, { payload }) => {
    return state.merge({
      errorMessage: payload });
  }

}, initialState);

export default list;
