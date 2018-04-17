import { combineReducers } from 'redux';
import processTree from './process-tree/reducer';

export default combineReducers({
  processAnalysis: combineReducers({
    processTree
  })
});
