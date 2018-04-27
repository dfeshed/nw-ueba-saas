import remediationTaskCreators from './remediation-task-creators';
import dictionaryCreators from './dictionary-creators';
import incidentsCreators from './incidents-creators';
import alertsCreators from './alert-creators';
import journalCreators from './journal-creators';

export default {
  remediationTasks: remediationTaskCreators,
  dictionaries: dictionaryCreators,
  incidents: incidentsCreators,
  alerts: alertsCreators,
  journal: journalCreators
};