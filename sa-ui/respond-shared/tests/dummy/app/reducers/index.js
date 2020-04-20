import { combineReducers } from 'redux';
import context from 'context/reducers';
import respondShared from 'respond-shared/reducers';
/**
 * Dummy app will be working as global container. Adding reducer will initialize context
 * redux store on global level.
 * Respond redux will be initialized as part of respond container load. So need not add here.
 * @public
 */
export default combineReducers({
  context,
  respondShared
});
