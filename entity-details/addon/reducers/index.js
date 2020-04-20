import { combineReducers } from 'redux';
import entity from './entity/reducer';
import indicators from './indicators/reducer';
import alerts from './alerts/reducer';

export default combineReducers({
  entity,
  indicators,
  alerts
});