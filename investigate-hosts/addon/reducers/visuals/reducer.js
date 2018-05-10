import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import Immutable from 'seamless-immutable';

const visualsInitialState = Immutable.from({
  activeAutorunTab: 'AUTORUNS',
  activeHostDetailTab: 'OVERVIEW',
  activeHostPropertyTab: 'HOST',
  activeDataSourceTab: 'ALERTS',
  isTreeView: true,
  showDeleteHostsModal: false,
  hostDetailsLoading: false,
  activeSystemInformationTab: 'HOST_ENTRIES'
});

const visuals = handleActions({
  [ACTION_TYPES.RESET_INPUT_DATA]: (state) => state.merge(visualsInitialState),

  [ACTION_TYPES.TOGGLE_PROCESS_VIEW]: (state) => state.set('isTreeView', !state.isTreeView),

  [ACTION_TYPES.CHANGE_AUTORUNS_TAB]: (state, { payload: { tabName } }) => state.set('activeAutorunTab', tabName),

  [ACTION_TYPES.CHANGE_DETAIL_TAB]: (state, { payload: { tabName } }) => {
    return state.set('activeHostDetailTab', tabName);
  },
  [ACTION_TYPES.CHANGE_DATASOURCE_TAB]: (state, { payload: { tabName } }) => state.set('activeDataSourceTab', tabName),
  [ACTION_TYPES.CHANGE_PROPERTY_TAB]: (state, { payload: { tabName } }) => state.set('activeHostPropertyTab', tabName),

  [ACTION_TYPES.TOGGLE_DELETE_HOSTS_MODAL]: (state) => {
    return state.set('showDeleteHostsModal', !state.showDeleteHostsModal);
  },

  [ACTION_TYPES.SET_SYSTEM_INFORMATION_TAB]: (state, { payload: { tabName } }) => {
    return state.set('activeSystemInformationTab', tabName);
  }
}, visualsInitialState);

export default visuals;
