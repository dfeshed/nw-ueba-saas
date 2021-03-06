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

  build() {
    const state = Immutable.from({
      springboard: this.state
    });
    this.setState(state);
    return state.asMutable();
  }

  springboards(data) {
    _set(this.state, 'springboards', data);
    return this;
  }

  activeSpringboardId(id) {
    _set(this.state, 'activeSpringboardId', id);
    return this;
  }
  pagerPosition(position) {
    _set(this.state, 'pagerPosition', position);
    return this;
  }
}