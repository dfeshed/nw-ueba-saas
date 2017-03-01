import Ember from 'ember';
const { Helper: { helper } } = Ember;
export function listTab([active, data]) {
  if (data) {
    if (active == 'unselected') {
      return data.filterBy('enabled', false);
    } else if (active == 'selected') {
      return data.filterBy('enabled', true);
    } else if (active == 'all') {
      return data;
    }
  }
}

export default helper(listTab);
