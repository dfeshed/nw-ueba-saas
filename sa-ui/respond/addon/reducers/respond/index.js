import { combineReducers } from 'redux';
import alerts from './alerts/alerts';
import alert from './alert';
import recon from './recon/index';
import dictionaries from './dictionaries';
import incident from './incident';
import incidents from './incidents';
import users from './users';
import remediationTasks from './remediation-tasks';
import storyline from './storyline';
import riac from './riac';

export default combineReducers({
  recon,
  alerts,
  alert,
  dictionaries,
  incident,
  incidents,
  remediationTasks,
  storyline,
  users,
  riac
});
