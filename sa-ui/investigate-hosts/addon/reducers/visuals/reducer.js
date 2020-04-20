import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import Immutable from 'seamless-immutable';

const visualsInitialState = Immutable.from({
  activeAutorunTab: 'AUTORUNS',
  activeAnomaliesTab: 'IMAGEHOOKS',
  activeHostDetailTab: 'OVERVIEW',
  activePropertyPanelTab: 'HOST_DETAILS',
  isTreeView: false,
  showDeleteHostsModal: false,
  activeSystemInformationTab: 'HOST_ENTRIES',
  isProcessDetailsView: false,
  showHostDetailsFilter: true,
  listAllFiles: true
});

const getStateKey = (tabName, subTabName) => {
  if (tabName === 'AUTORUNS') {
    return { activeAutorunTab: subTabName };
  } else {
    return { activeAnomaliesTab: subTabName };
  }
};

const visuals = handleActions({
  [ACTION_TYPES.RESET_INPUT_DATA]: (state) => state.merge(visualsInitialState),

  [ACTION_TYPES.TOGGLE_PROCESS_VIEW]: (state) => state.set('isTreeView', !state.isTreeView),

  [ACTION_TYPES.CHANGE_AUTORUNS_TAB]: (state, { payload: { tabName } }) => {
    return state.set('activeAutorunTab', tabName);
  },

  [ACTION_TYPES.CHANGE_ANOMALIES_TAB]: (state, { payload: { tabName } }) => state.set('activeAnomaliesTab', tabName),

  [ACTION_TYPES.CHANGE_DETAIL_TAB]: (state, { payload: { tabName, subTabName } }) => {
    if (subTabName) {
      const key = getStateKey(tabName, subTabName);
      return state.merge({ activeHostDetailTab: tabName, ...key });
    }
    return state.set('activeHostDetailTab', tabName);
  },

  [ACTION_TYPES.TOGGLE_DELETE_HOSTS_MODAL]: (state) => {
    return state.set('showDeleteHostsModal', !state.showDeleteHostsModal);
  },

  [ACTION_TYPES.SET_SYSTEM_INFORMATION_TAB]: (state, { payload: { tabName } }) => {
    return state.set('activeSystemInformationTab', tabName);
  },

  [ACTION_TYPES.SET_PROPERTY_PANEL_TAB]: (state, { payload: { tabName } }) => {
    return state.set('activePropertyPanelTab', tabName);
  },

  [ACTION_TYPES.CHANGE_PROPERTY_PANEL_TAB]: (state, { payload: { tabName } }) => state.set('activePropertyPanelTab', tabName),

  [ACTION_TYPES.TOGGLE_HOST_DETAILS_FILTER]: (state, { payload: { flag } }) => state.set('showHostDetailsFilter', flag),

  [ACTION_TYPES.TOGGLE_ALL_FILE]: (state) => state.set('listAllFiles', !state.listAllFiles),

  [ACTION_TYPES.TOGGLE_PROCESS_DETAILS]: (state, action) => state.set('isProcessDetailsView', action.payload)

}, visualsInitialState);

export default visuals;
