import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';

// import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({});

export default handleActions({
  _initialState
}, _initialState);
