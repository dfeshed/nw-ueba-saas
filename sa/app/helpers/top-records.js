import Ember from 'ember';
const { Helper: { helper } } = Ember;
export function topRecords([params, tabData]) {
  if (params) {
    if (tabData === 'overview') {
      return params.slice(0, 5);
    } else {
      return params;
    }
  }
}
export default helper(topRecords);
