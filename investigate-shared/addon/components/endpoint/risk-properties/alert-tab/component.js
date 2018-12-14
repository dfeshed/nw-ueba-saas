import Component from '@ember/component';
import layout from './template';

export default Component.extend({

  layout,

  tagName: '',

  selectedTab: '',

  actions: {
    activate(id, tabName, alertCount) {
      if (alertCount !== 0 && this.get('defaultAction')) {
        const { riskType, belongsTo } = this.getProperties('riskType', 'belongsTo');
        this.get('defaultAction')(id, riskType, belongsTo, tabName);
      }
    }
  }
});
