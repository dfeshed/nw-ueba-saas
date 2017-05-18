import { handleActions } from 'redux-actions';

import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';
import * as ACTION_TYPES from 'recon/actions/types';
import { handleSetTo } from 'recon/reducers/util';

const visualsInitialState = {
  defaultReconView: RECON_VIEW_TYPES_BY_NAME.TEXT, // view defaults to Text Analysis
  currentReconView: null,
  isHeaderOpen: true,
  isMetaShown: true,
  isReconExpanded: true,
  isReconOpen: false,
  isRequestShown: true,
  isResponseShown: true
};

const visuals = handleActions({
  [ACTION_TYPES.REHYDRATE]: (state, { payload }) => {
    let reducerState = {};
    if (payload && payload.recon && payload.recon.visuals) {
      reducerState = {
        ...payload.recon.visuals
      };
      // Ignore currentReconView as that is handled elsewhere
      if (reducerState.currentReconView) {
        delete reducerState.currentReconView;
      }
    }
    return {
      ...state,
      ...reducerState
    };
  },

  [ACTION_TYPES.OPEN_RECON]: (state) => ({
    ...state,
    isReconOpen: true
  }),

  [ACTION_TYPES.CHANGE_RECON_VIEW]: (state, { payload: { newView } }) => ({
    ...state,
    currentReconView: newView
  }),

  [ACTION_TYPES.TOGGLE_HEADER]: (state, { payload = {} }) => ({
    ...state,
    isHeaderOpen: handleSetTo(payload, state.isHeaderOpen)
  }),

  [ACTION_TYPES.TOGGLE_REQUEST]: (state, { payload = {} }) => ({
    ...state,
    isRequestShown: handleSetTo(payload, state.isRequestShown)
  }),

  [ACTION_TYPES.TOGGLE_RESPONSE]: (state, { payload = {} }) => ({
    ...state,
    isResponseShown: handleSetTo(payload, state.isResponseShown)
  }),

  [ACTION_TYPES.CLOSE_RECON]: (state) => ({
    ...state,
    isReconOpen: false
  }),

  // If meta is toggled on, need to expand recon
  [ACTION_TYPES.TOGGLE_META]: (state, { payload = {} }) => {
    const isMetaShown = handleSetTo(payload, state.isMetaShown);
    const isReconExpanded = (isMetaShown) ? true : state.isReconExpanded;

    return {
      ...state,
      isMetaShown,
      isReconExpanded
    };
  },

  // If recon is shrunk, need to shrink meta too
  [ACTION_TYPES.TOGGLE_EXPANDED]: (state, { payload = {} }) => {
    const isReconExpanded = handleSetTo(payload, state.isReconExpanded);
    const isMetaShown = (!isReconExpanded) ? false : state.isMetaShown;

    return {
      ...state,
      isMetaShown,
      isReconExpanded
    };
  }
}, visualsInitialState);

export default visuals;
