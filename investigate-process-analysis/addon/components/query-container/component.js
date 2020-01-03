import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setSelectedService, setQueryTimeRange } from 'investigate-process-analysis/actions/creators/services-creators';
import { setDetailsTab, toggleProcessDetailsVisibility } from 'investigate-process-analysis/actions/creators/process-visuals';
import { eventsCount } from 'investigate-process-analysis/reducers/process-tree/selectors';
import { selectedTab, allAlertCount } from 'investigate-process-analysis/reducers/process-visuals/selectors';
import { hostCount } from 'investigate-process-analysis/reducers/host-context/selectors';

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
    name: 'events',
    component: 'process-details/events-table',
    icon: 'list-bullets-1'
  },
  {
    label: 'investigateProcessAnalysis.tabs.hosts',
    name: 'hosts',
    component: 'process-details/host-list-container',
    icon: 'computer-notebook-2'
  },
  {
    label: 'investigateProcessAnalysis.tabs.alerts',
    name: 'alerts',
    component: 'process-details/alerts-container',
    icon: 'alarm'
  }
];
const stateToComputed = (state) => ({
  services: state.processAnalysis.services,
  serviceId: state.processAnalysis.query.serviceId,
  startTime: state.processAnalysis.query.startTime,
  endTime: state.processAnalysis.query.endTime,
  eventsCount: eventsCount(state),
  hostCount: hostCount(state),
  activeTab: selectedTab(state),
  allAlertCount: allAlertCount(state)
});

@classic
@tagName('hbox')
@classNames('query-container flexi-fit')
class QueryContainer extends Component {
  timeRangeInvalid = false;

  @service
  contextualHelp;

  @computed('activeTab')
  get tabs() {
    return TABS.map((t) => {
      return {
        ...t,
        isActive: t.name === this.activeTab.name
      };
    });
  }

  @computed('eventsCount')
  get isTabDisabled() {
    return !this.eventsCount > 0;
  }

  @action
  timeRangeError() {
    this.set('timeRangeInvalid', true);
  }

  @action
  timeRangeSelection(range) {
    this.set('timeRangeInvalid', false);
    this.send('setQueryTimeRange', range);
  }

  @action
  customTimeRange(start, end) {
    this.set('timeRangeInvalid', false);
    this.send('setQueryTimeRange', { startTime: start, endTime: end }, true);
  }

  @action
  goToHelp() {
    this.get('contextualHelp').goToHelp('investigation', 'invProcessAnalysis');
  }

  @action
  activate(tab) {
    if (!this.isTabDisabled) {
      this.send('toggleProcessDetailsVisibility', true);
      this.set('tabComponent', tab.component);
      this.send('setDetailsTab', tab);
    }
  }
}

export default connect(stateToComputed, dispatchToActions)(QueryContainer);
