import { combineReducers } from 'redux';
import respond from './respond/index';

export default combineReducers({
  ...respond
});
