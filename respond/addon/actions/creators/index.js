import remediationTaskCreators from './remediation-task-creators';
import dictionaryCreators from './dictionary-creators';
import incidentsCreators from './incidents-creators';
import alertsCreators from './alert-creators';

export default {
  'remediation-tasks': remediationTaskCreators,
  dictionaries: dictionaryCreators,
  incidents: incidentsCreators,
  alerts: alertsCreators
};