import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { riskState, getPropertyPanelTabs } from 'investigate-hosts/reducers/visuals/selectors';
import { setSelectedAlert, getUpdatedRiskScoreContext, expandEvent } from 'investigate-shared/actions/data-creators/risk-creators';
import { getPropertyData, getPoliciesPropertyData } from 'investigate-hosts/reducers/details/overview/selectors';
import hostDetailsConfig from 'investigate-hosts/components/property-panel/overview-property-config';
import policiesConfig from 'investigate-hosts/components/property-panel/policies-property-config';
import {
  setAlertTab,
  setPropertyPanelTabView
} from 'investigate-hosts/actions/data-creators/details';
import { toggleRightPanel } from 'investigate-hosts/actions/ui-state-creators';

const dispatchToActions = {
  setAlertTab,
  getUpdatedRiskScoreContext,
  setSelectedAlert,
  setPropertyPanelTabView,
  expandEvent,
  toggleRightPanel
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
  isRightPanelVisible: state.endpoint.detailsInput.isRightPanelVisible
});

const HostOverview = Component.extend({

  tagName: 'box',

  classNames: ['host-overview'],

  domIsReady: false,

  hostDetailsConfig,

  policiesConfig,

  isRightPanelClosed: false,

  @computed('activePropertyPanelTab')
  propertyPanelData(tab) {
    if (tab === 'POLICIES') {
      return {
        propertyData: this.get('policiesPropertyData'),
        localeNameSpace: 'adminUsm.policyWizard',
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
    closeRightPanel() {
      this.send('toggleRightPanel');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(HostOverview);
