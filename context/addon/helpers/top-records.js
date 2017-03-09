import Ember from 'ember';
const { Helper: { helper } } = Ember;
export function topRecords([params, tabData, columnName]) {

  if (params) {
    if (columnName) {
      params.sort((a, b) => (a[columnName] - b[columnName]));
    }
    if (tabData === 'overview') {
      return params.slice(0, 5);
    } else {
      return params;
    }
  }
}
export default helper(topRecords);
