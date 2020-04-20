import * as ACTION_TYPES from '../../actions/types';
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

const initialUserState = Immutable.from({
  userId: null,
  alertId: null,
  indicatorId: null
});

export default handleActions({
  [ACTION_TYPES.RESET_USER]: () => Immutable.from(initialUserState),
  [ACTION_TYPES.INITIATE_USER]: (state, { payload }) => state.merge(payload)
}, initialUserState);
