import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'packager/actions/types';

const initialState = {
  defaultPackagerConfig: {},
  error: null,
  loading: false,
  downloadLink: null,
  updating: false
};

const packagerReducer = handleActions({
  [ACTION_TYPES.GET_INFO]: (state, action) => {
    return handle(state, action, {
      start: () => ({ ...state, loading: true, error: false }),
      finish: (s) => ({ ...s, loading: false }),
      failure: (s) => ({ ...s, error: true }),
      success: (s) => ({ ...s, defaultPackagerConfig: action.payload.data })
    });
  },

  [ACTION_TYPES.SAVE_INFO]: (state, action) => {
    return handle(state, action, {
      start: () => ({ ...state, updating: true, error: false, downloadLink: null }),
      finish: (s) => ({ ...s, updating: false }),
      failure: (s) => ({ ...s, error: true }),
      success: (s) => ({ ...s, defaultPackagerConfig: action.payload.request.data })
    });
  },

  [ACTION_TYPES.CREATE_LOG]: (state, action) => {
    return handle(state, action, {
      start: () => ({ ...state, updating: true, error: false, downloadLink: null }),
      finish: (s) => ({ ...s, updating: false }),
      failure: (s) => ({ ...s, error: true }),
      success: (s) => ({ ...s, defaultPackagerConfig: action.payload.request.data })
    });
  },

  [ACTION_TYPES.DOWNLOAD_PACKAGE]: (state, { payload }) => ({
    ...state,
    downloadLink: payload
  }),

  [ACTION_TYPES.RETRIEVE_FAILURE]: (state) => ({
    ...state,
    error: true,
    loading: false
  }),

  [ACTION_TYPES.RESET_FORM]: (state) => ({
    ...state,
    defaultPackagerConfig: { ...state.defaultPackagerConfig }
  })

}, initialState);

export default packagerReducer;
