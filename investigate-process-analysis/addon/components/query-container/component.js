import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setSelectedService, setQueryTimeRange } from 'investigate-process-analysis/actions/creators/services-creators';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';
import { setDetailsTab, toggleProcessDetailsVisibility } from 'investigate-process-analysis/actions/creators/process-visuals';
import { eventsCount } from 'investigate-process-analysis/reducers/process-tree/selectors';
import { selectedTab } from 'investigate-process-analysis/reducers/process-visuals/selectors';

const dispatchToActions = {
  setSelectedService,
  setQueryTimeRange,
  setDetailsTab,
  toggleProcessDetailsVisibility
};
// Investigate TABS, order is important
const TABS = [
  {
    label: 'investigateProcessAnalysis.tabs.events',
    name: 'Events',
    component: 'process-details/events-table',
    icon: 'list-bullets-1'
  },
  {
    label: 'investigateProcessAnalysis.tabs.hosts',
    name: 'Hosts',
    component: 'process-details/property-container',
    icon: 'computer-notebook-2'
  },
  {
    label: 'investigateProcessAnalysis.tabs.alerts',
    name: 'Alerts',
    component: 'process-details/property-container',
    icon: 'alarm'
  }
];
const stateToComputed = (state) => ({
  services: state.processAnalysis.services,
  serviceId: state.processAnalysis.query.serviceId,
  startTime: state.processAnalysis.query.startTime,
  endTime: state.processAnalysis.query.endTime,
  eventsCount: eventsCount(state),
  activeTab: selectedTab(state)
});

const QueryContainer = Component.extend({

  tagName: 'hbox',

  classNames: 'query-container',

  timeRangeInvalid: false,

  contextualHelp: service(),

  @computed('activeTab')
  tabs(activeTab) {
    return TABS.map((t) => {
      return {
        ...t,
        isActive: t.name === activeTab.name
      };
    });
  },
  @computed('eventsCount')
  isTabDisabled(eventsCount) {
    return !eventsCount > 0;
  },
  actions: {
    timeRangeError() {
      this.set('timeRangeInvalid', true);
    },

    timeRangeSelection(range) {
      this.set('timeRangeInvalid', false);
      this.send('setQueryTimeRange', range);
    },

    customTimeRange(start, end) {
      this.set('timeRangeInvalid', false);
      this.send('setQueryTimeRange', { startTime: start, endTime: end }, true);
    },

    goToHelp() {
      this.get('contextualHelp').goToHelp('investigation', 'invProcessAnalysis');
    },
    activate(tab) {
      if (!this.isTabDisabled) {
        this.send('toggleProcessDetailsVisibility', true);
        this.set('tabComponent', tab.component);
        this.send('setDetailsTab', tab);
      }
    }

  }
});

export default connect(stateToComputed, dispatchToActions)(QueryContainer);
