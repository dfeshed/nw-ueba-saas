import { combineReducers } from 'redux';
import global from './global';
import context from 'context/reducers';
import packager from 'packager/reducers/packager';
import rar from 'endpoint-rar/reducers/rar';
import hw from 'health-wellness/reducers/reducers';

export default combineReducers({
  context,
  ...global,
  packager,
  rar,
  hw
});
