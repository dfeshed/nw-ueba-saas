import * as ACTION_TYPES from '../actions/types';
import { handleActions } from 'redux-actions';

const visualsInitialState = {
  isHeaderOpen: true,
  isRequestShown: true,
  isResponseShown: true,
  isPayloadOnly: false,
  isMetaShown: true,
  isReconExpanded: true,
  isReconOpen: false,
  packetTooltipData: null
};

const visuals = handleActions({
  [ACTION_TYPES.OPEN_RECON]: (state) => ({
    ...state,
    isReconOpen: true
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

  [ACTION_TYPES.TOGGLE_PAYLOAD_ONLY]: (state, { payload = {} }) => ({
    ...state,
    isPayloadOnly: payload.setTo !== undefined ? payload.setTo : !state.isPayloadOnly
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
  },

  [ACTION_TYPES.SHOW_PACKET_TOOLTIP]: (state, { payload }) => ({
    ...state,
    packetTooltipData: payload
  }),

  [ACTION_TYPES.HIDE_PACKET_TOOLTIP]: (state) => ({
    ...state,
    packetTooltipData: null
  })

}, visualsInitialState);

export default visuals;
