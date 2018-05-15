import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getHostPropertyTab, getOverviewContext, getAlertsCount, getIncidentsCount } from 'investigate-hosts/reducers/visuals/selectors';
import { setHostPropertyTabView } from 'investigate-hosts/actions/data-creators/details';

const stateToComputed = (state) => ({
  host: state.endpoint.overview.hostDetails,
  animation: state.endpoint.detailsInput.animation,
  activePropertyTab: state.endpoint.visuals.activeHostPropertyTab,
  hostPropertyTabs: getHostPropertyTab(state),
  overviewContext: getOverviewContext(state),
  alertsCount: getAlertsCount(state),
  incidentsCount: getIncidentsCount(state)
});

const dispatchToActions = {
  setHostPropertyTabView
};

const HostOverview = Component.extend({

  tagName: 'hbox',

  classNames: ['host-overview'],

  domIsReady: false,

  didRender() {
    // Delay rendering the property panel
    setTimeout(() => {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        this.set('domIsReady', true);
      }
    }, 250);
  }
});

export default connect(stateToComputed, dispatchToActions)(HostOverview);
