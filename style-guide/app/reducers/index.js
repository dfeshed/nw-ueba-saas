import { combineReducers } from 'redux';

const global = function global() {
  return {
    preferences: {
      theme: 'DARK'
    }
  };
};

export default combineReducers({
  global
});
