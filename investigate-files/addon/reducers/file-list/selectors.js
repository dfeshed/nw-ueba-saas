import { createSelector } from 'reselect';
import { prepareContext } from 'investigate-shared/helpers/prepare-context';

const SUPPORTED_SERVICES = [ 'broker', 'concentrator', 'decoder', 'log-decoder', 'archiver' ];

// Contains all the expression saved + newly added expression from the UI
const _files = (state) => state.files.fileList.fileData ? Object.values(state.files.fileList.fileData) : [];
const _fileExportLinkId = (state) => state.files.fileList.downloadId;
const _serviceList = (state) => state.files.fileList.listOfServices;
const _context = (state) => state.files.fileList.lookupData;
const _selectedFileList = (state) => state.files.fileList.selectedFileList || [];
const _selectedFileStatusHistory = (state) => state.files.fileList.selectedFileStatusHistory || [];
const _hostList = (state) => state.files.fileList.hostNameList;
const _serverId = (state) => state.endpointQuery.serverId;
const _areFilesLoading = (state) => state.files.fileList.areFilesLoading;
const _activeDataSourceTab = (state) => state.files.visuals.activeDataSourceTab || 'RISK_PROPERTIES';

export const files = createSelector(
  _files,
  (files) => {
    return files;
  }
);

export const areFilesLoading = createSelector(
  _areFilesLoading,
  (areFilesLoading) => {
    return areFilesLoading === 'wait';
  }
);

export const fileCount = createSelector(
  _files,
  (files) => {
    return files.length;
  }
);

export const hasFiles = createSelector(
  _files,
  (files) => {
    return !!files.length;
  }
);

export const fileExportLink = createSelector(
  [ _fileExportLinkId, _serverId ],
  (fileExportLinkId, serverId) => {
    if (fileExportLinkId) {
      if (serverId) {
        return `${location.origin}/rsa/endpoint/${serverId}/file/property/download?id=${fileExportLinkId}`;
      }
      return `${location.origin}/rsa/endpoint/file/property/download?id=${fileExportLinkId}`;
    }
    return null;
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

export const isRiskScoringServerNotConfigured = createSelector(
  _serviceList,
  (serviceList) => {
    if (serviceList) {
      const isConfigured = serviceList.some((service) => 'risk-scoring-server' === service.name);
      return !isConfigured;
    }
    return false;
  }
);

export const getContext = createSelector(
  [_context, _activeDataSourceTab],
  (context, riskPanelActiveTab) => {
    return prepareContext([context, riskPanelActiveTab]);
  }
);

export const isAllSelected = createSelector(
  [_files, _selectedFileList],
  (files, selectedFileList) => {
    if (selectedFileList && selectedFileList.length) {
      return files.length === selectedFileList.length;
    }
    return false;
  }
);


export const checksums = createSelector(
  _selectedFileList,
  (selectedFileList) => selectedFileList.map((file) => file.checksumSha256)
);

export const selectedFileStatusHistory = createSelector(
    _selectedFileStatusHistory,
    (selectedFileStatusHistory) => selectedFileStatusHistory
);

export const hostList = createSelector(
  _hostList,
  (hostList) => {
    const hosts = hostList.map((host) => {
      return host.value;
    });
    return hosts;
  }
);
