import { combineReducers } from 'redux';
import processTree from './process-tree/reducer';
import processProperties from './process-properties/reducer';
import processVisuals from './process-visuals/reducer';

export default combineReducers({
  processAnalysis: combineReducers({
    processTree,
    processProperties,
    processVisuals
  })
});
