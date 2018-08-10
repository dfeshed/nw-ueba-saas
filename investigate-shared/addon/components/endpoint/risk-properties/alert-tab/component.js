import Component from '@ember/component';
import layout from './template';

export default Component.extend({

  layout,

  tagName: '',

  selectedTab: '',

  actions: {
    activate(tabName) {
      if (this.get('defaultAction')) {
        this.get('defaultAction')(tabName);
      }
    }
  }
});
