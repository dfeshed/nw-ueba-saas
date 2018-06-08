import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { setDetailsTab, toggleEventPanelExpanded, toggleProcessDetailsVisibility } from 'investigate-process-analysis/actions/creators/process-visuals';
import { eventsCount } from 'investigate-process-analysis/reducers/process-tree/selectors';


const stateToComputed = (state) => ({
  eventsCount: eventsCount(state)
});

const dispatchToActions = {
  setDetailsTab,
  toggleEventPanelExpanded,
  toggleProcessDetailsVisibility
};

// Investigate TABS, order is important
const TABS = [
  {
    label: 'investigateProcessAnalysis.tabs.properties',
    name: 'Properties',
    component: 'process-details/property-container'
  },
  {
    label: 'investigateProcessAnalysis.tabs.events',
    name: 'Events',
    component: 'process-details/events-table'
  }
];

const processDetails = Component.extend({

  tagName: '',

  activeTab: 'Properties',

  tabComponent: 'process-details/property-container',

  isReconExpanded: true,

  @computed('isReconExpanded')
  toggleEventsClass: (isReconExpanded) => isReconExpanded ? 'shrink-diagonal-2' : 'expand-diagonal-4',

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
    },

    toggleDetailsExpanded() {
      this.toggleProperty('isReconExpanded');
      this.send('toggleEventPanelExpanded');
    },

    closeDetails() {
      this.send('toggleProcessDetailsVisibility');
    }

  }
});

export default connect(stateToComputed, dispatchToActions)(processDetails);
