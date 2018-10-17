import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'investigate-files/actions/types';
import Immutable from 'seamless-immutable';

const visualsInitialState = Immutable.from({
  activeFileDetailTab: 'OVERVIEW',
  activeRiskSeverityTab: 'critical'
});

const visuals = handleActions({
  [ACTION_TYPES.CHANGE_FILE_DETAIL_TAB]: (state, { payload: { tabName } }) => {
    return state.set('activeFileDetailTab', tabName);
  },
  [ACTION_TYPES.ACTIVE_RISK_SEVERITY_TAB]: (state, { payload: { tabName } }) => {
    return state.set('activeRiskSeverityTab', tabName);
  },
  [ACTION_TYPES.RESET_RISK_CONTEXT]: (state) => {
    return state.set('activeRiskSeverityTab', 'critical');
  }
}, visualsInitialState);

export default visuals;
