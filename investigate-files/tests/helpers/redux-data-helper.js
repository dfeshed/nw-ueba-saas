import Immutable from 'seamless-immutable';

const _set = (obj, key, val) => {
  if (obj[key]) {
    obj[key] = val;
    return;
  }

  const keys = key.split('.');
  const firstKey = keys.shift();

  if (!obj[firstKey]) {
    obj[firstKey] = {};
  }

  if (keys.length === 0) {
    obj[firstKey] = val;
    return;
  } else {
    _set(obj[firstKey], keys.join('.'), val);
  }
};

export default class DataHelper {
  constructor(setState) {
    this.state = {};
    this.setState = setState;
  }

  // Trigger setState, also return the resulting state
  // in case it needs to be used/checked
  build() {
    const state = Immutable.from(this.state);
    this.setState(state);
    return state.asMutable();
  }

  schema(schema) {
    _set(this.state, 'files.schema.schema', schema);
    return this;
  }

  totalItems(count) {
    _set(this.state, 'files.fileList.totalItems', count);
    return this;
  }

  isValidExpression(isValid) {
    if (isValid) {
      _set(this.state, 'files.filter.expressionList',
        [{ propertyName: 'format', propertyValues: [{ value: 'pe' }], restrictionType: 'IN' }]);
    } else {
      _set(this.state, 'files.filter.expressionList', null);
    }
    return this;
  }

  fileCount(count) {

    const data = {};
    for (let i = 0; i < count; i++) {
      data[i] = {};
    }
    _set(this.state, 'files.fileList.fileData', data);
    return this;
  }

  isSystemFilter(value) {
    _set(this.state, 'files.filter.isSystemFilter', value);
    return this;
  }

  filesFilters(customFilesFilters) {
    _set(this.state, 'files.filter.fileFilters', customFilesFilters);
    return this;
  }

  filesFilterFilter(filter) {
    _set(this.state, 'files.filter.filter', filter);
    return this;
  }

  areFilesLoading(value) {
    _set(this.state, 'files.fileList.areFilesLoading', value);
    return this;
  }

  isSchemaLoaded(value) {
    _set(this.state, 'files.schema.schemaLoading', value);
    return this;
  }

  isSchemaLoading(value) {
    _set(this.state, 'files.schema.schemaLoading', value);
    return this;
  }

  files(files) {
    _set(this.state, 'files.fileList.fileData', files);
    return this;
  }

  loadMoreStatus(status) {
    _set(this.state, 'files.fileList.loadMoreStatus', status);
    return this;
  }

  fileList(fileList) {
    _set(this.state, 'files.fileList', fileList);
    return this;
  }

  downloadStatus(value) {
    _set(this.state, 'files.fileList.downloadStatus', value);
    return this;
  }

  downloadId(downloadId) {
    _set(this.state, 'files.fileList.downloadId', downloadId);
    return this;
  }

  preferences(preferences) {
    _set(this.state, 'preferences.preferences', preferences);
    return this;
  }

  expressionList(expressionListArray) {
    _set(this.state, 'files.filter.expressionList', expressionListArray);
    return this;
  }
  setSelectedFileList(list) {
    _set(this.state, 'files.fileList.selectedFileList', list);
    return this;
  }
  setSelectedFile(file) {
    _set(this.state, 'files.fileList.selectedFile', file);
    return this;
  }
  isEndpointServerOffline(status) {
    _set(this.state, 'endpointServer.isSummaryRetrieveError', status);
    return this;
  }
  endpointServer(status) {
    _set(this.state, 'endpointServer', status);
    return this;
  }
  endpointQuery(status) {
    _set(this.state, 'endpointQuery', status);
    return this;
  }
  hostNameList(hosts) {
    _set(this.state, 'files.fileList.hostNameList', hosts);
    return this;
  }
  activeDataSourceTab(tab) {
    _set(this.state, 'files.visuals.activeDataSourceTab', tab);
    return this;
  }
  fetchMetaValueLoading(status) {
    _set(this.state, 'files.fileList.fetchMetaValueLoading', status);
    return this;
  }
  certificatesItems(items) {
    _set(this.state, 'certificate.list.certificatesList', items);
    return this;
  }

  certificatesLoadingStatus(status) {
    _set(this.state, 'certificate.list.certificatesLoadingStatus', status);
    return this;
  }
  loadMoreCertificateStatus(status) {
    _set(this.state, 'certificate.list.loadMoreStatus', status);
    return this;
  }

  totalCertificates(items) {
    _set(this.state, 'certificate.list.totalItems', items);
    return this;
  }
  selectedCertificatesList(items) {
    _set(this.state, 'certificate.list.selectedCertificateList', items);
    return this;
  }
  certificateStatusData(status) {
    _set(this.state, 'certificate.list.certificateStatusData', status);
    return this;
  }
  isCertificateView(status) {
    _set(this.state, 'certificate.list.isCertificateView', status);
    return this;
  }
  certificateVisibleColumns(columns) {
    _set(this.state, 'certificate.list.certificateVisibleColumns', columns);
    return this;
  }
  serviceList(list) {
    _set(this.state, 'files.fileList.listOfServices', list);
    return this;
  }
  setSelectedIndex(index) {
    _set(this.state, 'files.fileList.setSelectedIndex', index);
    return this;
  }
  services(data) {
    _set(this.state, 'endpointServer', data);
    return this;
  }
  selectedDetailFile(data) {
    _set(this.state, 'files.fileList.selectedDetailFile', data);
    return this;
  }
  fileAnalysis(fileAnalysis) {
    _set(this.state, 'files.fileAnalysis', fileAnalysis);
    return this;
  }
  isFilePropertyPanelVisible(value) {
    _set(this.state, 'files.visuals.isFilePropertyPanelVisible', value);
    return this;
  }
}
