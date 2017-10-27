import { combineReducers } from 'redux';

const preferences = function preferences() {
  return { theme: 'DARK' };
};

export default combineReducers({
  preferences
});
