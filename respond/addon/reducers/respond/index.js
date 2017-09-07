import { combineReducers } from 'redux';
import incidents from './incidents';
import alerts from './alerts';
import alert from './alert';
import incident from './incident';
import users from './users';
import dictionaries from './dictionaries';
import remediationTasks from './remediation-tasks';

export default {
  respond: combineReducers({
    incidents,
    incident,
    users,
    dictionaries,
    remediationTasks,
    alerts,
    alert
  })
};
