import { combineReducers } from 'redux';
import schema from './schema/reducer';
import fileList from './file-list/reducer';
import filter from 'investigate-shared/reducers/endpoint-filter/reducer';
import risk from 'investigate-shared/reducers/risk/reducer';
import investigate from 'investigate-shared/reducers/investigate/reducer';
import fileStatus from 'investigate-shared/reducers/file-status/reducer';
import visuals from './visuals/reducer';
import preferences from './preferences/reducer';
import endpointServer from 'investigate-shared/reducers/endpoint-server/reducer';
import endpointQuery from 'investigate-shared/reducers/endpoint-query/reducer';
import certificate from './certificates/reducer';
import { createFilteredReducer } from 'component-lib/utils/reducer-wrapper';

const reducerPredicate = (action) => action.meta && action.meta.belongsTo === 'FILE';
const certificateReducerPredicate = (action) => action.meta && action.meta.belongsTo === 'CERTIFICATE';

export default combineReducers({
  files: combineReducers({
    fileList,
    schema,
    filter: createFilteredReducer(filter, reducerPredicate),
    risk: createFilteredReducer(risk, reducerPredicate),
    visuals
  }),
  investigate: createFilteredReducer(investigate, reducerPredicate),
  fileStatus: createFilteredReducer(fileStatus, reducerPredicate),
  preferences,
  endpointServer,
  endpointQuery,
  certificate: combineReducers({
    list: certificate,
    filter: createFilteredReducer(filter, certificateReducerPredicate)
  })
});
