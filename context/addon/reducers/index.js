import { combineReducers } from 'redux';
import context from './context/reducer';
import tabs from './tabs/reducer';
import hover from './hover/reducer';

export default combineReducers({
  context,
  tabs,
  hover
});
