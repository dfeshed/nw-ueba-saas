import { combineReducers } from 'redux';
import processTree from './process-tree/reducer';
import processProperties from './process-properties/reducer';
import processVisuals from './process-visuals/reducer';
import services from './services/reducer';
import query from './query/reducer';
import processFilter from './process-filter/reducer';
import filterPopup from './filter-popup/reducer';

export default combineReducers({
  processAnalysis: combineReducers({
    processTree,
    processProperties,
    processVisuals,
    services,
    query,
    processFilter,
    filterPopup
  })
});
