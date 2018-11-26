import { combineReducers } from 'redux';
import visuals from './visuals/reducer';
import machines from './hosts/reducer';
import explore from './details/explore/reducer';
import process from './details/process/reducer';
import detailsInput from './details/reducer';
import overview from './details/overview/reducer';
import schema from './schema/reducer';
import filter from 'investigate-shared/reducers/endpoint-filter/reducer';
import fileAnalysis from './details/file-analysis/reducer';
import preferences from './preferences/reducer';
import datatable from './details/datatable/reducer';
import endpointServer from './endpoint-server/reducer';
import endpointQuery from './endpoint-query/reducer';
import { createFilteredReducer } from 'component-lib/utils/reducer-wrapper';
import fileContext from './details/file-context/reducer';
import investigate from 'investigate-shared/reducers/investigate/reducer';
import fileStatus from 'investigate-shared/reducers/file-status/reducer';
import risk from 'investigate-shared/reducers/risk/reducer';

const reducerPredicate = (type) => {
  return (action) => {
    return action.meta && action.meta.belongsTo === type;
  };
};

export default combineReducers({
  endpoint: combineReducers({
    detailsInput,
    machines,
    visuals,
    explore,
    process,
    overview,
    datatable,
    schema,
    hostFiles: createFilteredReducer(fileContext, reducerPredicate('FILE')),
    autoruns: createFilteredReducer(fileContext, reducerPredicate('AUTORUN')),
    services: createFilteredReducer(fileContext, reducerPredicate('SERVICE')),
    tasks: createFilteredReducer(fileContext, reducerPredicate('TASK')),
    threads: createFilteredReducer(fileContext, reducerPredicate('THREAD')),
    imageHooks: createFilteredReducer(fileContext, reducerPredicate('IMAGEHOOK')),
    kernelHooks: createFilteredReducer(fileContext, reducerPredicate('KERNELHOOK')),
    drivers: createFilteredReducer(fileContext, reducerPredicate('DRIVER')),
    libraries: createFilteredReducer(fileContext, reducerPredicate('LIBRARY')),
    processes: createFilteredReducer(fileContext, reducerPredicate('PROCESS')),
    filter: createFilteredReducer(filter, reducerPredicate('MACHINE')),
    fileAnalysis,
    risk: createFilteredReducer(risk, reducerPredicate('HOST'))
  }),
  fileStatus: createFilteredReducer(fileStatus, reducerPredicate),
  investigate: createFilteredReducer(investigate, reducerPredicate('MACHINE')),
  preferences,
  endpointServer,
  endpointQuery
});
