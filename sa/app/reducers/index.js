import { combineReducers } from 'redux';
import global from './global';
import context from 'context/reducers';
import packager from 'packager/reducers/packager';

export default combineReducers({
  context,
  ...global,
  packager
});
