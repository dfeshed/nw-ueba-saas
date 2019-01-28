import { createSelector } from 'reselect';
import { prepareContext } from 'investigate-shared/helpers/prepare-context';
import { isBrokerView } from 'investigate-shared/selectors/endpoint-server/selectors';
const SUPPORTED_SERVICES = [ 'broker', 'concentrator', 'decoder', 'log-decoder', 'archiver' ];

// Contains all the expression saved + newly added expression from the UI
const _files = (state) => state.files.fileList.fileData;
const _fileExportLinkId = (state) => state.files.fileList.downloadId;
const _serviceList = (state) => state.files.fileList.listOfServices;
const _context = (state) => state.files.fileList.lookupData;
const _selectedFileList = (state) => state.files.fileList.selectedFileList || [];
const _selectedFileStatusHistory = (state) => state.files.fileList.selectedFileStatusHistory || [];
const _hostList = (state) => state.files.fileList.hostNameList;
const _serverId = (state) => state.endpointQuery.serverId;
const _areFilesLoading = (state) => state.files.fileList.areFilesLoading;
const _activeDataSourceTab = (state) => state.files.visuals.activeDataSourceTab || 'FILE_DETAILS';
const _servers = (state) => state.endpointServer.serviceData || [];
const _fileTotal = (state) => state.files.fileList.totalItems || 0;
const _hasNext = (state) => state.files.fileList.hasNext;
const _expressionList = (state) => state.files.filter.expressionList || [];
const _downloadLink = (state) => state.files.fileList.downloadLink;
const _areSelectedFilesHavingThumbprint = createSelector(
  _selectedFileList,
  (fileContextSelections) => {
    if (fileContextSelections && fileContextSelections.length > 0) {
      const filteredList = fileContextSelections.some((item) => {
        return item.signature && item.signature.thumbprint;
      });
      return filteredList;
    }
    return true;
  }
);

export const files = createSelector(
  _files,
  (files = {}) => {
    return Object.values(files);
  }
);

export const areFilesLoading = createSelector(
  _areFilesLoading,
  (areFilesLoading) => {
    return areFilesLoading === 'wait';
  }
);

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

export const getContext = createSelector(
  [_context, _activeDataSourceTab],
  (context, riskPanelActiveTab) => {
    return prepareContext([context, riskPanelActiveTab]);
  }
);

export const isAllSelected = createSelector(
  [files, _selectedFileList],
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

export const hostListCount = createSelector(
  [hostList],
  (hostList = []) => {
    return hostList.length;
  }
);

export const isExportButtonDisabled = createSelector(
  [hasFiles, _servers, _serverId],
  (hasFiles, servers, serverId) => {
    const isEndpointBroker = servers.some((s) => s.id === serverId && s.name === 'endpoint-broker-server');
    return !hasFiles || isEndpointBroker;
  }
);

export const fileTotalLabel = createSelector(
  [_fileTotal, _expressionList, _hasNext, isBrokerView],
  (total, expressionList, hasNext, isBrokerView) => {
    if (total >= 1000) {
      if (isBrokerView || (expressionList && expressionList.length && hasNext)) {
        return '1000+';
      }
    }
    return `${total}`;
  }
);
export const nextLoadCount = createSelector(
  [files],
  (files) => {
    const ONE_PAGE_MAX_LENGTH = 100;
    const loadCount = files.length >= ONE_PAGE_MAX_LENGTH ? ONE_PAGE_MAX_LENGTH : files.length;
    return loadCount;
  }
);

export const isAnyFileFloatingOrMemoryDll = createSelector(
  _selectedFileList,
  (fileContextSelections) => {
    if (fileContextSelections && fileContextSelections.length) {
      const filteredList = fileContextSelections.some((item) => {
        return (item.format && item.format === 'floating') || (item.features && item.features.includes('file.memoryHash'));
      });
      return filteredList;
    }
    return false;
  }
);

const _isFloatingOrMemoryDll = createSelector(
  _selectedFileList,
  (selectedFileList) => {
    if (selectedFileList && selectedFileList.length) {
      return !!selectedFileList.find((file) => (file.format === 'floating') || (file.signature && file.signature.features.includes('file.memoryHash')));
    }
    return true;
  }
);

const _isSelectedFileDownloaded = createSelector(
  _selectedFileList,
  (selectedFileList) => !!selectedFileList.find((file) => file.downloadInfo && file.downloadInfo.status === 'Downloaded'));

export const fileDownloadButtonStatus = createSelector(
  [ _isFloatingOrMemoryDll, _selectedFileList, _isSelectedFileDownloaded],
  (isFloatingOrMemoryDll, selectedFileList, isSelectedFileDownloaded) => {
    const isDownloadToServerDisabled = isSelectedFileDownloaded || selectedFileList.length !== 1 || isFloatingOrMemoryDll;
    const isSaveLocalAndFileAnalysisDisabled = !isSelectedFileDownloaded || selectedFileList.length !== 1;
    return {
      isDownloadToServerDisabled,
      isSaveLocalAndFileAnalysisDisabled
    };
  }
);
export const downloadLink = createSelector(
  _downloadLink,
  (downloadLink) => {
    return downloadLink ? `${downloadLink}&${Number(new Date())}` : null;
  }
);
export const isCertificateViewDisabled = createSelector(
  _selectedFileList, _areSelectedFilesHavingThumbprint,
  (fileContextSelections, areSelectedFilesHavingThumbprint) => {
    const MAX_FILE_SELECTION_ALLOWED_FOR_CERTIFICATE_VIEW = 10;
    if (fileContextSelections.length <= MAX_FILE_SELECTION_ALLOWED_FOR_CERTIFICATE_VIEW && areSelectedFilesHavingThumbprint) {
      return false;
    }
    return true;
  }
);
