import * as ACTION_TYPES from '../../actions/types';
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  selectedAlertId: null,
  alerts: []
});

const alerts = handleActions({
  [ACTION_TYPES.RESET_ALERT]: () => Immutable.from(initialState),
  [ACTION_TYPES.GET_ALERTS]: (state, { payload }) => state.merge({ alerts: payload }),
  [ACTION_TYPES.INITIATE_ALERT]: (state, { payload: { alertId } }) => state.merge({ selectedAlertId: alertId })
}, initialState);

export default alerts;