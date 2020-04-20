import { combineReducers } from 'redux';
import query from './query/reducer';
import server from './server/reducer';

export default combineReducers({
  query,
  server
});
