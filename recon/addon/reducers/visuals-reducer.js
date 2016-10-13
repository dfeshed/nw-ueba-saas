import * as ACTION_TYPES from '../actions/types';
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
  [ACTION_TYPES.INITIALIZE]: (state) => ({
    ...state,
    isReconOpen: true
  }),

  [ACTION_TYPES.TOGGLE_HEADER]: (state, { payload = {} }) => ({
    ...state,
    isHeaderOpen: payload.setTo || !state.isHeaderOpen
  }),

  [ACTION_TYPES.TOGGLE_REQUEST]: (state, { payload = {} }) => ({
    ...state,
    isRequestShown: payload.setTo || !state.isRequestShown
  }),

  [ACTION_TYPES.TOGGLE_RESPONSE]: (state, { payload = {} }) => ({
    ...state,
    isResponseShown: payload.setTo || !state.isResponseShown
  }),

  [ACTION_TYPES.CLOSE_RECON]: (state) => ({
    ...state,
    isReconOpen: false
  }),

  // If meta is toggled on, need to expand recon
  [ACTION_TYPES.TOGGLE_META]: (state, { payload = {} }) => {
    const isMetaShown = payload.setTo || !state.isMetaShown;
    const isReconExpanded = (isMetaShown) ? true : state.isReconExpanded;

    return {
      ...state,
      isMetaShown,
      isReconExpanded
    };
  },

  // If recon is shrunk, need to shrink meta too
  [ACTION_TYPES.TOGGLE_EXPANDED]: (state, { payload = {} }) => {
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
