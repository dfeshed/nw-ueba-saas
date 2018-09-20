import { combineReducers } from 'redux';
import certificates from './certificates/reducer';
import query from './query/reducer';
import server from './server/reducer';

export default combineReducers({
  certificates,
  query,
  server
});
