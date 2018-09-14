import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'investigate-files/actions/types';
import Immutable from 'seamless-immutable';

const visualsInitialState = Immutable.from({
  activeFileDetailTab: 'OVERVIEW',
  activeAlertTab: 'CRITICAL'
});

const visuals = handleActions({
  [ACTION_TYPES.CHANGE_FILE_DETAIL_TAB]: (state, { payload: { tabName } }) => {
    return state.set('activeFileDetailTab', tabName);
  },
  [ACTION_TYPES.CHANGE_ALERT_TAB]: (state, { payload: { tabName } }) => {
    return state.set('activeAlertTab', tabName);
  }
}, visualsInitialState);

export default visuals;