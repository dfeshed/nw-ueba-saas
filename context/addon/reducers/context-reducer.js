import * as ACTION_TYPES from '../actions/types';
import { handleActions } from 'redux-actions';

const initialState = {
  activeTabName: 'overview',
  dataSources: null,
  lookupData: null
};

const context = handleActions({
  [ACTION_TYPES.UPDATE_ACTIVE_TAB]: (state, { payload }) => ({
    ...state,
    activeTabName: payload
  }),
  [ACTION_TYPES.GET_ALL_DATA_SOURCES]: (state, { payload }) => ({
    ...state,
    dataSources: payload
  }),
  [ACTION_TYPES.GET_LOOKUP_DATA]: (state, { payload }) => ({
    ...state,
    lookupData: payload
  })
}, initialState);

export default context;