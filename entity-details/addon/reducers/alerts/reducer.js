import * as ACTION_TYPES from '../../actions/types';
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  selectedAlertId: null,
  sortBy: 'severity',
  alerts: [],
  alertError: false
});

const alerts = handleActions({
  [ACTION_TYPES.RESET_ALERT]: () => Immutable.from(initialState),
  [ACTION_TYPES.GET_ALERTS]: (state, { payload }) => state.set('alerts', payload),
  [ACTION_TYPES.ALERT_ERROR]: (state) => state.set('alertError', true),
  [ACTION_TYPES.SELECT_ALERT]: (state, { payload }) => state.set('selectedAlertId', payload),
  [ACTION_TYPES.UPDATE_SORT]: (state, { payload }) => state.set('sortBy', payload),
  [ACTION_TYPES.INITIATE_ALERT]: (state, { payload }) => state.merge({ selectedAlertId: payload, alerts: [], sortBy: 'severity', alertError: false })
}, initialState);

export default alerts;