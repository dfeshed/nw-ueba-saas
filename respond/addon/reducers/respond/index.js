import redux from 'redux';
import incidents from './incidents';
import incident from './incident';

export default {
  respond: redux.combineReducers({
    incidents,
    incident
  })
};