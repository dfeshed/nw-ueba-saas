import { combineReducers } from 'redux';
import alertIncidentAssociation from './alerts/add-to-incident';
import alerts from './alerts/alerts';
import alert from './alert';
import dictionaries from './dictionaries';
import incident from './incident';
import incidents from './incidents';
import users from './users';
import remediationTasks from './remediation-tasks';

export default {
  respond: combineReducers({
    alertIncidentAssociation,
    alerts,
    alert,
    dictionaries,
    incident,
    incidents,
    remediationTasks,
    users
  })
};
