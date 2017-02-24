import redux from 'redux';
import incidents from './incidents';
import incident from './incident';
import users from './users';

export default {
  respond: redux.combineReducers({
    incidents,
    incident,
    users
  })
};