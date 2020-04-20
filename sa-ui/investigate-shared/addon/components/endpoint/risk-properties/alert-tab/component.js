import Component from '@ember/component';
import layout from './template';

export default Component.extend({

  layout,

  tagName: '',

  selectedTab: '',

  actions: {
    activate(id, tabName, alertCount) {
      if (alertCount !== 0 && this.get('defaultAction')) {
        const { riskType, agentId } = this.getProperties('riskType', 'agentId');
        this.get('defaultAction')(id, riskType, agentId, tabName);
      }
    }
  }
});
