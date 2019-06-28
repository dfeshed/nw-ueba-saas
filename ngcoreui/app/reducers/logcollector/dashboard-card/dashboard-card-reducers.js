import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
// import { handle } from 'redux-pack';

const initialState = {
  key1: 'value1',
  key2: 'value2',
  key3: 'value3'
};

export default reduxActions.handleActions({

  // [ACTION_TYPES.SOME_ACTION_TYPE]: (state /* , action */) => {
  'SOME_ACTION_TYPE': (state /* , action */) => {
    return state;
  },

  'ANOTHER_ACTION_TYPE': (state /* , action */) => {
    return state;
  }

}, Immutable.from(initialState));
