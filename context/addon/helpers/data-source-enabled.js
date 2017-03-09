import Ember from 'ember';
const { Helper: { helper } } = Ember;
export function dataSourceEnabled([dataSources, dataSourceGroup]) {
  if ('overview' === dataSourceGroup || dataSources.contains(dataSourceGroup)) {
    return true;
  }
  return false;
}

export default helper(dataSourceEnabled);