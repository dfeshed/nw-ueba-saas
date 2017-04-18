import { handleActions } from 'redux-actions';

import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';
import * as ACTION_TYPES from 'recon/actions/types';

const visualsInitialState = {
  currentReconView: RECON_VIEW_TYPES_BY_NAME.PACKET,   // view defaults to packet
  isHeaderOpen: true,
  isMetaShown: true,
  isReconExpanded: true,
  isReconOpen: false,
  isRequestShown: true,
  isResponseShown: true
};

const visuals = handleActions({
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
    isHeaderOpen: payload.setTo !== undefined ? payload.setTo : !state.isHeaderOpen
  }),

  [ACTION_TYPES.TOGGLE_REQUEST]: (state, { payload = {} }) => ({
    ...state,
    isRequestShown: payload.setTo !== undefined ? payload.setTo : !state.isRequestShown
  }),

  [ACTION_TYPES.TOGGLE_RESPONSE]: (state, { payload = {} }) => ({
    ...state,
    isResponseShown: payload.setTo !== undefined ? payload.setTo : !state.isResponseShown
  }),

  [ACTION_TYPES.CLOSE_RECON]: (state) => ({
    ...state,
    isReconOpen: false
  }),

  // If meta is toggled on, need to expand recon
  [ACTION_TYPES.TOGGLE_META]: (state, { payload = {} }) => {
    const isMetaShown = payload.setTo !== undefined ? payload.setTo : !state.isMetaShown;
    const isReconExpanded = (isMetaShown) ? true : state.isReconExpanded;

    return {
      ...state,
      isMetaShown,
      isReconExpanded
    };
  },

  // If recon is shrunk, need to shrink meta too
  [ACTION_TYPES.TOGGLE_EXPANDED]: (state, { payload = {} }) => {
    const isReconExpanded = payload.setTo !== undefined ? payload.setTo : !state.isReconExpanded;
    const isMetaShown = (!isReconExpanded) ? false : state.isMetaShown;

    return {
      ...state,
      isMetaShown,
      isReconExpanded
    };
  }
}, visualsInitialState);

export default visuals;
