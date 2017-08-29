import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'recon/actions/types';
import { augmentResult, handleSetTo } from 'recon/reducers/util';

const textInitialState = Immutable.from({
  decode: true,

  // the maximum number of packets represented in the textContent
  maxPacketsForText: 2500,
  // If the max packets reached for this event
  maxPacketsReached: false,
  // The string representing meta to search for in the text
  metaToHighlight: null,

  renderIds: null,
  textContent: null
});

const textReducer = handleActions({
  [ACTION_TYPES.REHYDRATE]: (state, { payload }) => {
    let reducerState = {};
    if (payload && payload.recon && payload.recon.text) {
      reducerState = payload.recon.text;
    }
    return state.merge(reducerState);
  },

  [ACTION_TYPES.INITIALIZE]: ({ decode }) => {
    // let whatever decode is remain, otherwise, start over
    return textInitialState.merge({ decode });
  },

  [ACTION_TYPES.TEXT_HIGHLIGHT_META]: (state, { payload }) => {
    return state.set('metaToHighlight', payload);
  },

  // If meta is toggled, need to clear metaToHighlight
  [ACTION_TYPES.TOGGLE_META]: (state) => {
    return state.set('metaToHighlight', null);
  },

  [ACTION_TYPES.TEXT_RECEIVE_PAGE]: (state, { payload }) => {
    const augmentedEntries = augmentResult(payload.data);
    const textContent = (state.textContent || Immutable.from([])).concat(augmentedEntries);

    let { maxPacketsReached } = state;
    if (payload.meta && payload.meta.MAX_PACKETS_THRESHOLD) {
      maxPacketsReached = true;
    }
    return state.merge({ textContent, maxPacketsReached });
  },

  [ACTION_TYPES.CHANGE_RECON_VIEW]: (state) => state.set('renderIds', []),

  [ACTION_TYPES.TEXT_RENDER_NEXT]: (state, { payload }) => {
    const newIds = payload.map((text) => text.firstPacketId);
    const renderIds = (state.renderIds || Immutable.from([])).concat(newIds);
    return state.merge({ renderIds });
  },

  [ACTION_TYPES.TOGGLE_TEXT_DECODE]: (state, { payload = {} }) => {
    return state.merge({
      decode: handleSetTo(payload, state.decode),
      textContent: [],
      renderIds: []
    });
  }

}, textInitialState);

export default textReducer;

