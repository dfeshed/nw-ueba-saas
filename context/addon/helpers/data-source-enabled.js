import { helper } from 'ember-helper';

export function dataSourceEnabled([dataSources, dataSourceGroup]) {
  if ('overview' === dataSourceGroup || dataSources.contains(dataSourceGroup)) {
    return true;
  }
  return false;
}

export default helper(dataSourceEnabled);