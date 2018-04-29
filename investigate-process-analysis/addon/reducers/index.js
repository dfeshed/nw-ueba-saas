import { combineReducers } from 'redux';
import processTree from './process-tree/reducer';
import processProperties from './process-properties/reducer';

export default combineReducers({
  processAnalysis: combineReducers({
    processTree,
    processProperties
  })
});
