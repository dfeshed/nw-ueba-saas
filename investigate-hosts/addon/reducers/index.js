import { combineReducers } from 'redux';
import visuals from './visuals/reducer';
import machines from './hosts/reducer';
import explore from './details/explore/reducer';
import process from './details/process/reducer';
import detailsInput from './details/reducer';
import hostFiles from './details/files/reducer';
import overview from './details/overview/reducer';
import autoruns from './details/autorun/reducer';
import anomalies from './details/anomalies/reducer';
import drivers from './details/drivers/reducer';
import libraries from './details/libraries/reducer';
import schema from './schema/reducer';
import filter from 'investigate-shared/reducers/endpoint-filter/reducer';
import preferences from './preferences/reducer';
import datatable from './details/datatable/reducer';
import endpointServer from './endpoint-server/reducer';
import endpointQuery from './endpoint-query/reducer';
import { createFilteredReducer } from 'component-lib/utils/reducer-wrapper';

export default combineReducers({
  endpoint: combineReducers({
    detailsInput,
    machines,
    visuals,
    explore,
    process,
    hostFiles,
    overview,
    autoruns,
    anomalies,
    drivers,
    libraries,
    schema,
    filter: createFilteredReducer(filter, (action) => action.meta && action.meta.name === 'MACHINE'),
    datatable
  }),
  preferences,
  endpointServer,
  endpointQuery
});
