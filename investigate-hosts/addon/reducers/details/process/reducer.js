import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  processList: null,
  // In list view, process view can be sorted based on processName, pid. By default, we fetch based on processName in ascending order.
  sortField: 'name',
  isDescOrder: false,

  processTree: null,
  processDetails: null

});

const processReducer = handleActions({

  [ACTION_TYPES.RESET_HOST_DETAILS]: (state) => state.merge(initialState),

  [ACTION_TYPES.RESET_PROCESS_LIST]: (state) => state.set('processList', null),

  [ACTION_TYPES.SET_SORT_BY]: (state, { payload: { sortField, isDescOrder } }) => {
    return state.merge({ sortField, isDescOrder });
  },

  [ACTION_TYPES.GET_PROCESS_LIST]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ processDetails: null }),
      success: (s) => s.set('processList', action.payload.data)
    });
  },

  [ACTION_TYPES.GET_PROCESS_TREE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ processDetails: null }),
      success: (s) => s.set('processTree', action.payload.data)
    });
  },

  [ACTION_TYPES.GET_PROCESS]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.set('processDetails', action.payload.data)
    });
  },

  [ACTION_TYPES.GET_PROCESS_FILE_CONTEXT]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('dllList', null),
      success: (s) => s.set('dllList', action.payload.data)
    });
  }

}, initialState);

export default processReducer;
