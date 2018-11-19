import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';

// import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({});

export default handleActions({
  // have to provide something semi-legit here or else
  // redux actions flakes out
  foo() {}
}, _initialState);
