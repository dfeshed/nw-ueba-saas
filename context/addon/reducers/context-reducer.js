import * as ACTION_TYPES from '../actions/types';
import { handleActions } from 'redux-actions';

const initialState = {
  activeTabName: 'overview'
};

const context = handleActions({
  [ACTION_TYPES.UPDATE_ACTIVE_TAB]: (state, { payload }) => ({
    ...state,
    activeTabName: payload
  })
}, initialState);

export default context;