import { combineReducers } from 'redux';
import preferences from 'preferences/reducers';
import context from 'context/reducers';

export default combineReducers({
  context,
  ...preferences
});