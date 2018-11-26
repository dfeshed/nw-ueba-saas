import * as ACTION_TYPES from '../../actions/types';
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  alertId: null
});

const alerts = handleActions({
  [ACTION_TYPES.RESET_ENTITY]: () => Immutable.from(initialState),
  [ACTION_TYPES.INITIATE_ENTITY]: (state, { payload: { alertId } }) => state.merge({ alertId })
}, initialState);

export default alerts;
