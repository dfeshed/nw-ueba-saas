import { combineReducers } from 'redux';
import preferences from './preferences';

export default {
  global: combineReducers({
    preferences
  })
};
