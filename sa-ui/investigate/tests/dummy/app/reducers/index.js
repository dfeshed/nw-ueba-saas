import { combineReducers } from 'redux';
import context from 'context/reducers';
/**
 * Dummy app will be working as global container. Adding reducer will initialize context
 * redux store on global level.
 * Investigate redux will be initialized as part of Investigate container load. So need not add here.
 * @public
 */
export default combineReducers({
  context
});