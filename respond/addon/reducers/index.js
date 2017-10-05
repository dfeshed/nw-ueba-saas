import { combineReducers } from 'redux';
import respond from './respond/index';
import context from 'context/reducers';

export default combineReducers({
  respond,
  context
});
