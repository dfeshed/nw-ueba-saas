import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from 'recon/actions/types';
import { augmentResult, handleSetTo } from 'recon/reducers/util';

const textInitialState = {
  decode: true,

  // the maximum number of packets represented in the textContent
  maxPacketsForText: 2500,
  // If the max packets reached for this event
  maxPacketsReached: false,
  // The string representing meta to search for in the text
  metaToHighlight: null,

  renderIds: null,
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

  [ACTION_TYPES.TEXT_RECEIVE_PAGE]: (state, { payload }) => {
    const newContent = augmentResult(payload.data);
    if (payload.meta && payload.meta.MAX_PACKETS_THRESHOLD) {
      state.maxPacketsReached = true;
    }
    return {
      ...state,
      textContent: state.textContent ? [...state.textContent, ...newContent] : newContent
    };
  },

  [ACTION_TYPES.CHANGE_RECON_VIEW]: (state) => ({
    ...state,
    renderIds: []
  }),

  [ACTION_TYPES.TEXT_RENDER_NEXT]: (state, { payload }) => {
    const ids = payload.map((text) => text.firstPacketId);
    return {
      ...state,
      renderIds: state.renderIds ? [...state.renderIds, ...ids] : ids
    };
  },

  [ACTION_TYPES.TOGGLE_TEXT_DECODE]: (state, { payload = {} }) => ({
    ...state,
    decode: handleSetTo(payload, state.decode),
    textContent: [],
    renderIds: []
  })

}, textInitialState);

export default textReducer;
