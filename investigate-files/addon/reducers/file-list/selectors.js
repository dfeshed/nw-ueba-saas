import { createSelector } from 'reselect';
import { isValidExpression } from 'investigate-files/reducers/filter/selectors';

const SUPPORTED_SERVICES = [ 'broker', 'concentrator', 'decoder', 'log-decoder', 'archiver' ];

const DATASOURCE_TABS = [
  {
    label: 'investigateHosts.tabs.alerts',
    name: 'ALERT'
  },
  {
    label: 'investigateHosts.tabs.incidents',
    name: 'INCIDENT'
  }
];

// Contains all the expression saved + newly added expression from the UI
const files = (state) => state.files.fileList.files;
const _fileExportLinkId = (state) => state.files.fileList.downloadId;
const _totalItems = (state) => state.files.fileList.totalItems;
const _serviceList = (state) => state.files.fileList.listOfServices;
const _context = (state) => state.files.fileList.lookupData;
const _activeDataSourceTab = (state) => state.files.fileList.activeDataSourceTab || 'ALERT';

export const fileCount = createSelector(
  files,
  (files) => {
    return files.length;
  }
);

export const hasFiles = createSelector(
  files,
  (files) => {
    return !!files.length;
  }
);

export const fileExportLink = createSelector(
  _fileExportLinkId,
  (fileExportLinkId) => {
    if (fileExportLinkId) {
      return `${location.origin}/rsa/endpoint/file/property/download?id=${fileExportLinkId}`;
    }
    return null;
  }
);

export const fileCountForDisplay = createSelector(
  [ _totalItems, isValidExpression],
  (totalItems, isValidExpression) => {
    // For performance reasons api returns 1000 as totalItems when filter is applied, even if result is more than 1000
    // Make sure we append '+' to indicate user more files are present
    if (isValidExpression && totalItems >= 1000) {
      return `${totalItems}+`;
    }
    return `${totalItems}`;
  }
);

export const serviceList = createSelector(
  _serviceList,
  (serviceList) => {
    if (serviceList) {
      return serviceList.filter((service) => SUPPORTED_SERVICES.includes(service.name));
    }
    return null;
  }
);
export const getDataSourceTab = createSelector(
  [_activeDataSourceTab],
  (activeDataSourceTab) => {
    return DATASOURCE_TABS.map((tab) => ({ ...tab, selected: tab.name === activeDataSourceTab }));
  }
);

export const getAlertsCount = createSelector(
  [_context],
  (context) => {
    let count = 0;
    if (context && context[0].Alerts) {
      count = context[0].Alerts.resultList.length;
    }
    return count;
  }
);
export const getIncidentsCount = createSelector(
  [_context],
  (context) => {
    let count = 0;
    if (context && context[0].Incidents) {
      count = context[0].Incidents.resultList.length;
    }
    return count;
  }
);

export const getContext = createSelector(
  [_context, _activeDataSourceTab],
  (context, riskPanelActiveTab) => {
    let { resultList, resultMeta } = '';
    const activeTab = riskPanelActiveTab === 'ALERT' ? 'Alerts' : 'Incidents';
    if (context && context[0][activeTab]) {
      resultList = context[0][activeTab].resultList;
      resultMeta = context[0][activeTab].resultMeta;
    }
    if (resultList) {
      if (activeTab === 'Alerts') {
        resultList = resultList.map((alert) => {
          const incident = alert.incidentId;
          return { ...alert.alert, incident };
        });
      }
    }
    return { resultList, resultMeta };
  }
);