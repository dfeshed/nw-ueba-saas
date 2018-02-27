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
    const array = new Array(count);
    _set(this.state, 'files.fileList.files', array);
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

  areFilesLoading(value) {
    _set(this.state, 'files.fileList.areFilesLoading', value);
    return this;
  }

  isSchemaLoaded(value) {
    _set(this.state, 'files.schema.schemaLoading', value);
    return this;
  }

  files(files) {
    _set(this.state, 'files.fileList.files', files);
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
}
