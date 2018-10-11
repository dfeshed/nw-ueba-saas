import Component from '@ember/component';
import layout from './template';

export default Component.extend({

  layout,

  tagName: '',

  selectedTab: '',

  actions: {
    activate(checksum, tabName, alertCount) {
      if (alertCount != 0 && this.get('defaultAction')) {
        this.get('defaultAction')(checksum, tabName);
      }
    }
  }
});
