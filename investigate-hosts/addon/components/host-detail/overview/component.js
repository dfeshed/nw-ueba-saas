import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { riskState, getPropertyPanelTabs } from 'investigate-hosts/reducers/visuals/selectors';
import { setSelectedAlert, getUpdatedRiskScoreContext, expandEvent } from 'investigate-shared/actions/data-creators/risk-creators';
import { getPropertyData,
  getPoliciesPropertyData,
  channelFiltersConfig,
  showWindowsLogPolicy,
  policiesUnavailableMessage } from 'investigate-hosts/reducers/details/overview/selectors';
import hostDetailsConfig from 'investigate-hosts/components/property-panel/overview-property-config';
import { getPoliciesPropertyConfig } from 'investigate-hosts/components/property-panel/policies-property-config';
import {
  setAlertTab,
  setPropertyPanelTabView
} from 'investigate-hosts/actions/data-creators/details';
import { toggleDetailRightPanel } from 'investigate-hosts/actions/ui-state-creators';
import { isInsightsAgent } from 'investigate-hosts/reducers/hosts/selectors';

const dispatchToActions = {
  setAlertTab,
  getUpdatedRiskScoreContext,
  setSelectedAlert,
  setPropertyPanelTabView,
  expandEvent,
  toggleDetailRightPanel
};

const stateToComputed = (state) => ({
  animation: state.endpoint.detailsInput.animation,
  hostDetailsPropertyData: getPropertyData(state),
  hostDetails: state.endpoint.overview.hostDetails || [],
  activeAlertTab: state.endpoint.overview.activeAlertTab,
  risk: riskState(state),
  activePropertyPanelTab: state.endpoint.visuals.activePropertyPanelTab,
  propertyPanelTabs: getPropertyPanelTabs(state),
  policiesPropertyData: getPoliciesPropertyData(state),
  isDetailRightPanelVisible: state.endpoint.detailsInput.isDetailRightPanelVisible,
  listOfServices: state.endpoint.machines.listOfServices,
  isInsightsAgent: isInsightsAgent(state),
  channelFiltersConfig: channelFiltersConfig(state),
  showWindowsLogPolicy: showWindowsLogPolicy(state),
  policiesUnavailableMessage: policiesUnavailableMessage(state)
});

const HostOverview = Component.extend({

  tagName: 'box',

  classNames: ['host-overview'],

  domIsReady: false,

  hostDetailsConfig,

  @computed('showWindowsLogPolicy', 'channelFiltersConfig')
  policiesConfig(showWindowsLogPolicy, channelFiltersConfig) {
    return getPoliciesPropertyConfig(showWindowsLogPolicy, channelFiltersConfig);
  },

  @computed('activePropertyPanelTab')
  propertyPanelData(tab) {
    if (tab === 'POLICIES') {
      return {
        propertyData: this.get('policiesPropertyData'),
        localeNameSpace: 'adminUsm',
        config: this.get('policiesConfig')
      };
    }
    return {
      propertyData: this.get('hostDetailsPropertyData'),
      localeNameSpace: 'investigateHosts.hosts.properties',
      config: this.get('hostDetailsConfig')
    };
  },

  didRender() {
    // Delay rendering the property panel
    setTimeout(() => {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        this.set('domIsReady', true);
      }
    }, 250);
  },
  actions: {
    expandEventAction(id) {
      if (this.get('isDetailRightPanelVisible')) {
        this.send('toggleDetailRightPanel');
      }
      this.send('expandEvent', id);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(HostOverview);
