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
    const state = Immutable.from({
      files: this.state
    });
    this.setState(state);
    return state.asMutable();
  }

  schema(schema) {
    _set(this.state, 'schema.schema', schema);
    return this;
  }

  visibleColumns(columns) {
    _set(this.state, 'schema.visibleColumns', columns);
    return this;
  }

  totalItems(count) {
    _set(this.state, 'fileList.totalItems', count);
    return this;
  }

  isValidExpression(isValid) {
    if (isValid) {
      _set(this.state, 'filter.expressionList',
        [{ propertyName: 'format', propertyValues: [{ value: 'pe' }], restrictionType: 'IN' }]);
    } else {
      _set(this.state, 'filter.expressionList', null);
    }
    return this;
  }

  fileCount(count) {
    const array = new Array(count);
    _set(this.state, 'fileList.files', array);
    return this;
  }
}