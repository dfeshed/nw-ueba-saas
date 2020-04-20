import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';
import {
  RECON_VIEW_TYPES,
  RECON_VIEW_TYPES_BY_NAME
} from 'recon/utils/reconstruction-types';
import { EVENT_TYPES } from 'component-lib/constants/event-types';
import * as ACTION_TYPES from 'recon/actions/types';
import { handleSetTo, handlePreference } from 'recon/reducers/util';

const visualsInitialState = Immutable.from({
  defaultReconView: RECON_VIEW_TYPES_BY_NAME.TEXT, // view defaults to Text,
  currentReconView: RECON_VIEW_TYPES_BY_NAME.TEXT,
  isHeaderOpen: true,
  isMetaShown: true,
  isReconExpanded: true,
  isReconOpen: false,
  isRequestShown: true,
  isResponseShown: true,
  defaultLogFormat: 'TEXT',
  defaultPacketFormat: 'PCAP'
});

const _getViewByName = (type) => RECON_VIEW_TYPES.findBy('name', type);

const visuals = handleActions({
  [ACTION_TYPES.REHYDRATE]: (state, { payload }) => {
    let reducerState = {};
    if (payload && payload.recon && payload.recon.visuals) {
      reducerState = payload.recon.visuals;
    }
    return state.merge(reducerState);
  },

  [ACTION_TYPES.INITIALIZE]: (state, { payload }) => {
    const { eventType } = payload;
    // if we know what type of event this is, let's set that up now
    if (eventType && typeof(eventType) === 'string') {
      // ENDPOINT and LOG events are always viewed as "text", otherwise, just
      // use the default view. The default view is updated whenever you choose
      // a new view.
      const view = (eventType === EVENT_TYPES.ENDPOINT || eventType === EVENT_TYPES.LOG) ?
        _getViewByName('TEXT') : state.defaultReconView;
      return state.set('currentReconView', view);
    }
    return state;
  },

  [ACTION_TYPES.OPEN_RECON]: (state) => state.set('isReconOpen', true),

  [ACTION_TYPES.CLOSE_RECON]: (state) => state.set('isReconOpen', false),

  [ACTION_TYPES.CHANGE_RECON_VIEW]: (state, { payload: { newView } }) => {
    return state.set('currentReconView', newView);
  },

  [ACTION_TYPES.STORE_RECON_VIEW]: (state, { payload: { newView } }) => {
    // do not store if isClassicReconView
    // isClassicReconView tabs open in a new window
    // when this is triggered via selecting a new event in the table, it results in a very jarring ux
    return !newView.isClassicReconView ? state.set('defaultReconView', newView) : state;
  },

  [ACTION_TYPES.SET_PREFERENCES]: (state, { payload: { eventAnalysisPreferences } }) => {
    const defaultLogFormat = handlePreference(eventAnalysisPreferences, 'defaultLogFormat', state);
    const defaultPacketFormat = handlePreference(eventAnalysisPreferences, 'defaultPacketFormat', state);
    let defaultReconView = handlePreference(eventAnalysisPreferences, 'currentReconView', state);
    if (typeof defaultReconView === 'string') {
      defaultReconView = RECON_VIEW_TYPES_BY_NAME[defaultReconView];
    }
    return state.merge({
      defaultReconView,
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
      defaultReconView: currentReconView,
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
