import { combineReducers } from 'redux';
import context from './context/reducer';
import tabs from './tabs/reducer';
import hover from './hover/reducer';
import list from './list/reducer';

export default combineReducers({
  context,
  tabs,
  hover,
  list
});
