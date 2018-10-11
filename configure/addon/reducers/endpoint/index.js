import { combineReducers } from 'redux';
import certificates from './certificates/reducer';
import query from './query/reducer';
import server from './server/reducer';
import filter from 'investigate-shared/reducers/endpoint-filter/reducer';
import { createFilteredReducer } from 'component-lib/utils/reducer-wrapper';
const reducerPredicate = (action) => action.meta && action.meta.belongsTo === 'CERTIFICATE';

export default combineReducers({
  certificates: combineReducers({
    list: certificates,
    filter: createFilteredReducer(filter, reducerPredicate)
  }),
  query,
  server
});
