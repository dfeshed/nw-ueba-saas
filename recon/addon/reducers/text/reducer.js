import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from 'recon/actions/types';
import { augmentResult, handleSetTo } from 'recon/reducers/util';

const textInitialState = {
  decode: true,
  // The string to look for in the text
  metaToHighlight: null,
  textContent: null
};

const textReducer = handleActions({
  [ACTION_TYPES.REHYDRATE]: (state, { payload }) => {
    let reducerState = {};
    if (payload && payload.recon && payload.recon.text) {
      reducerState = payload.recon.text;
    }
    return {
      ...state,
      ...reducerState
    };
  },

  [ACTION_TYPES.INITIALIZE]: (state) => ({
    ...textInitialState,
    decode: state.decode // let whatever decode is remain
  }),

  [ACTION_TYPES.TEXT_HIGHLIGHT_META]: (state, { payload }) => ({
    ...state,
    metaToHighlight: payload
  }),

  // If meta is toggled, need to clear metaToHighlight
  [ACTION_TYPES.TOGGLE_META]: (state) => ({
    ...state,
    metaToHighlight: null
  }),

  [ACTION_TYPES.TEXT_DECODE_PAGE]: (state, { payload }) => {
    const newContent = augmentResult(payload);
    return {
      ...state,
      textContent: state.textContent ? [...state.textContent, ...newContent] : newContent
    };
  },

  [ACTION_TYPES.TOGGLE_TEXT_DECODE]: (state, { payload = {} }) => ({
    ...state,
    decode: handleSetTo(payload, state.decode),
    textContent: []
  })

}, textInitialState);

export default textReducer;
