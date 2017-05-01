import redux from 'redux';
import incidents from './incidents';
import incident from './incident';
import users from './users';
import dictionaries from './dictionaries';
import remediationTasks from './remediation-tasks';

export default {
  respond: redux.combineReducers({
    incidents,
    incident,
    users,
    dictionaries,
    remediationTasks
  })
};