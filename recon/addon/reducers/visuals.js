import * as TYPES from '../actions/types';
import reduxActions from 'npm:redux-actions';

const visualsInitialState = {
  isHeaderOpen: true,
  isRequestShown: true,
  isResponseShown: true,
  isMetaShown: false,
  isReconExpanded: false,
  isReconOpen: false
};

const visuals = reduxActions.handleActions({
  [TYPES.INITIALIZE]: (state) => ({
    ...state,
    isReconOpen: true
  }),

  [TYPES.TOGGLE_HEADER]: (state, { payload = {} }) => ({
    ...state,
    isHeaderOpen: payload.setTo || !state.isHeaderOpen
  }),

  [TYPES.TOGGLE_REQUEST]: (state, { payload = {} }) => ({
    ...state,
    isRequestShown: payload.setTo || !state.isRequestShown
  }),

  [TYPES.TOGGLE_RESPONSE]: (state, { payload = {} }) => ({
    ...state,
    isResponseShown: payload.setTo || !state.isResponseShown
  }),

  [TYPES.CLOSE_RECON]: (state) => ({
    ...state,
    isReconOpen: false
  }),

  // If meta is toggled on, need to expand recon
  [TYPES.TOGGLE_META]: (state, { payload = {} }) => {
    const isMetaShown = payload.setTo || !state.isMetaShown;
    const isReconExpanded = (isMetaShown) ? true : state.isReconExpanded;

    return {
      ...state,
      isMetaShown,
      isReconExpanded
    };
  },

  // If recon is shrunk, need to shrink meta too
  [TYPES.TOGGLE_RECON_EXPANDED]: (state, { payload = {} }) => {
    const isReconExpanded = payload.setTo || !state.isReconExpanded;
    const isMetaShown = (!isReconExpanded) ? false : state.isMetaShown;

    return {
      ...state,
      isMetaShown,
      isReconExpanded
    };
  }
}, visualsInitialState);

export default visuals;
