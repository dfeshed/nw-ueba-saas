import { isEmpty } from 'ember-utils';
import { helper } from 'ember-helper';

export function dataSourceEnabled([dataSources, dataSourceGroup]) {
  if (!isEmpty(dataSourceGroup)) {
    if (dataSourceGroup === 'overview' || (!isEmpty(dataSources) && dataSources.includes(dataSourceGroup))) {
      return true;
    }
  }

  return false;
}

export default helper(dataSourceEnabled);