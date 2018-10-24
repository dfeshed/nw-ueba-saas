import * as ACTION_TYPES from './types';

const setDataSourceTab = (tabName) => ({ type: ACTION_TYPES.CHANGE_DATASOURCE_TAB, payload: { tabName } });

const setNewFileTab = (tabName) => ({ type: ACTION_TYPES.CHANGE_FILE_DETAIL_TAB, payload: { tabName } });

const activeRiskSeverityTab = (tabName) => ({ type: ACTION_TYPES.ACTIVE_RISK_SEVERITY_TAB, payload: { tabName } });

export {
  setDataSourceTab,
  activeRiskSeverityTab,
  setNewFileTab
};
