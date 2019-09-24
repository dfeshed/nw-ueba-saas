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
      listManager: this.state
    });
    if (this.setState) {
      this.setState(state);
    }
    return state.asMutable();
  }

  highlightedIndex(index = -1) {
    _set(this.state, 'highlightedIndex', index);
    return this;
  }

  highlightedId(id) {
    _set(this.state, 'highlightedId', id);
    return this;
  }

  stateLocation(location) {
    _set(this.state, 'stateLocation', location);
    return this;
  }

  listName(name) {
    _set(this.state, 'listName', name);
    return this;
  }

  modelName(name) {
    _set(this.state, 'modelName', name);
    return this;
  }

  list(list) {
    _set(this.state, 'list', list);
    return this;
  }

  filterText(text) {
    _set(this.state, 'filterText', text);
    return this;
  }

  isExpanded(value) {
    _set(this.state, 'isExpanded', value);
    return this;
  }

  selectedItemId(item) {
    _set(this.state, 'selectedItemId', item);
    return this;
  }

  viewName(viewname) {
    _set(this.state, 'viewName', viewname);
    return this;
  }

  helpId(object) {
    _set(this.state, 'helpId', object);
    return this;
  }

  editItemId(id) {
    _set(this.state, 'editItemId', id);
    return this;
  }
}
