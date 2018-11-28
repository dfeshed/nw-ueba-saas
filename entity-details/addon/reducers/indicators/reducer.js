import * as ACTION_TYPES from '../../actions/types';
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

const initialState = Immutable.from({
  indicatorId: null
});

const indicators = handleActions({
  [ACTION_TYPES.RESET_INDICATOR]: () => Immutable.from(initialState),
  [ACTION_TYPES.INITIATE_INDICATOR]: (state, { payload: { indicatorId } }) => state.merge({ indicatorId })
}, initialState);

export default indicators;
