import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';
import * as ACTION_TYPES from 'recon/actions/types';
import { handleSetTo, handlePreference } from 'recon/reducers/util';

const visualsInitialState = Immutable.from({
  defaultReconView: RECON_VIEW_TYPES_BY_NAME.TEXT, // view defaults to Text Analysis,
  currentReconView: RECON_VIEW_TYPES_BY_NAME.TEXT,
  isHeaderOpen: true,
  isMetaShown: true,
  isReconExpanded: true,
  isReconOpen: false,
  isRequestShown: true,
  isResponseShown: true,
  defaultLogFormat: 'LOG',
  defaultPacketFormat: 'PCAP'
});

const visuals = handleActions({
  [ACTION_TYPES.REHYDRATE]: (state, { payload }) => {
    let reducerState = {};
    if (payload && payload.recon && payload.recon.visuals) {
      reducerState = payload.recon.visuals;
      // Ignore currentReconView as that is handled elsewhere
      if (reducerState.currentReconView) {
        delete reducerState.currentReconView;
      }
    }
    return state.merge(reducerState);
  },

  [ACTION_TYPES.OPEN_RECON]: (state) => state.set('isReconOpen', true),

  [ACTION_TYPES.CLOSE_RECON]: (state) => state.set('isReconOpen', false),

  [ACTION_TYPES.CHANGE_RECON_VIEW]: (state, { payload: { newView } }) => {
    return state.set('currentReconView', newView);
  },

  [ACTION_TYPES.SET_PREFERENCES]: (state, { payload: { eventAnalysisPreferences } }) => {
    const defaultLogFormat = handlePreference(eventAnalysisPreferences, 'defaultLogFormat', state);
    const defaultPacketFormat = handlePreference(eventAnalysisPreferences, 'defaultPacketFormat', state);
    let currentReconView = handlePreference(eventAnalysisPreferences, 'currentReconView', state);
    if (typeof currentReconView === 'string') {
      currentReconView = RECON_VIEW_TYPES_BY_NAME[currentReconView];
    }
    return state.merge({
      currentReconView,
      defaultLogFormat,
      defaultPacketFormat
    });
  },

  [ACTION_TYPES.RESET_PREFERENCES]: (state, { payload: { eventAnalysisPreferences } }) => {
    const defaultLogFormat = handlePreference(eventAnalysisPreferences, 'defaultLogFormat', visualsInitialState);
    const defaultPacketFormat = handlePreference(eventAnalysisPreferences, 'defaultPacketFormat', visualsInitialState);
    let currentReconView = handlePreference(eventAnalysisPreferences, 'currentReconView', visualsInitialState);
    if (typeof currentReconView === 'string') {
      currentReconView = RECON_VIEW_TYPES_BY_NAME[currentReconView];
    }
    return visualsInitialState.merge({
      defaultLogFormat,
      defaultPacketFormat,
      currentReconView,
      isReconOpen: state.isReconOpen
    });
  },

  [ACTION_TYPES.TOGGLE_HEADER]: (state, { payload = {} }) => {
    return state.set('isHeaderOpen', handleSetTo(payload, state.isHeaderOpen));
  },

  [ACTION_TYPES.TOGGLE_REQUEST]: (state, { payload = {} }) => {
    return state.set('isRequestShown', handleSetTo(payload, state.isRequestShown));
  },

  [ACTION_TYPES.TOGGLE_RESPONSE]: (state, { payload = {} }) => {
    return state.set('isResponseShown', handleSetTo(payload, state.isResponseShown));
  },

  // If meta is toggled on, need to expand recon
  [ACTION_TYPES.TOGGLE_META]: (state, { payload = {} }) => {
    const isMetaShown = handleSetTo(payload, state.isMetaShown);
    const isReconExpanded = (isMetaShown) ? true : state.isReconExpanded;
    return state.merge({ isMetaShown, isReconExpanded });
  },

  // If recon is shrunk, need to shrink meta too
  [ACTION_TYPES.TOGGLE_EXPANDED]: (state, { payload = {} }) => {
    const isReconExpanded = handleSetTo(payload, state.isReconExpanded);
    const isMetaShown = (!isReconExpanded) ? false : state.isMetaShown;
    return state.merge({ isMetaShown, isReconExpanded });
  }
}, visualsInitialState);

export default visuals;
