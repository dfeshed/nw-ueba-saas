import { combineReducers } from 'redux';
import preferences from './preferences/index';

export default {
  global: combineReducers({
    preferences
  })
};
