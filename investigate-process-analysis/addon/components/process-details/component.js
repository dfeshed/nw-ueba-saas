import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

// Investigate TABS, order is important
const TABS = [
  {
    label: 'Properties',
    name: 'investigateProcessAnalysis.propertiesPanel',
    component: 'process-details/process-property-panel'
  },
  {
    label: 'Events',
    name: 'investigateProcessAnalysis.events',
    component: 'process-details/events-table'
  }
];
export default Component.extend({
  layout,
  activeTab: 'investigateProcessAnalysis.propertiesPanel',
  tabComponent: 'process-details/process-property-panel',

  @computed('activeTab')
  tabs(activeTab) {
    return TABS.map((t) => {
      return {
        ...t,
        isActive: t.name === activeTab
      };
    });
  },
  actions: {
    activate(tab) {
      this.set('activeTab', tab.name);
      this.set('tabComponent', tab.component);
    }
  }
});
