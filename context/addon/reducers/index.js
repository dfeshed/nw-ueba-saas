import { combineReducers } from 'redux';
import context from './context/reducer';
import tabs from './tabs/reducer';

export default combineReducers({
  context,
  tabs
});
