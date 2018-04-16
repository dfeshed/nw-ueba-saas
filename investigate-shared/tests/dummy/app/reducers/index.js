import { combineReducers } from 'redux';
import investigateShared from 'investigate-shared/reducers/index';

export default combineReducers({
  ...investigateShared
});
