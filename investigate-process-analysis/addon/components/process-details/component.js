import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { setDetailsTab } from 'investigate-process-analysis/actions/creators/process-visuals';
import { eventsCount } from 'investigate-process-analysis/reducers/process-tree/selectors';


const stateToComputed = (state) => ({
  eventsCount: eventsCount(state)
});

const dispatchToActions = {
  setDetailsTab
};

// Investigate TABS, order is important
const TABS = [
  {
    label: 'investigateProcessAnalysis.tabs.properties',
    name: 'Properties',
    component: 'process-details/process-property-panel'
  },
  {
    label: 'investigateProcessAnalysis.tabs.events',
    name: 'Events',
    component: 'process-details/events-table'
  }
];

const processDetails = Component.extend({
  layout,
  activeTab: 'Properties',
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
      this.send('setDetailsTab', tab.name);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(processDetails);