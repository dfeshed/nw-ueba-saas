import { combineReducers } from 'redux';
import visuals from './visuals/reducer';
import machines from './hosts/reducer';
import explore from './details/explore/reducer';
import process from './details/process/reducer';
import detailsInput from './details/reducer';
import hostFiles from './details/files/reducer';
import overview from './details/overview/reducer';
import autoruns from './details/autorun/reducer';
import drivers from './details/drivers/reducer';
import libraries from './details/libraries/reducer';
import schema from './schema/reducer';
import filter from './filters/reducer';
import preferences from './preferences/reducer';
import datatable from './details/datatable/reducer';

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
    drivers,
    libraries,
    schema,
    filter,
    datatable
  }),
  preferences
});
