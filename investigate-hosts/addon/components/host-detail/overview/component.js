import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { riskState, getPropertyPanelTabs } from 'investigate-hosts/reducers/visuals/selectors';
import { setSelectedAlert, getUpdatedRiskScoreContext, expandEvent } from 'investigate-shared/actions/data-creators/risk-creators';
import { getPropertyData,
  getPoliciesPropertyData,
  channelFiltersConfig,
  showWindowsLogPolicy,
  hostName,
  policiesUnavailableMessage } from 'investigate-hosts/reducers/details/overview/selectors';
import hostDetailsConfig from 'investigate-hosts/components/property-panel/overview-property-config';
import { getPoliciesPropertyConfig } from 'investigate-hosts/components/property-panel/policies-property-config';
import {
  setPropertyPanelTabView
} from 'investigate-hosts/actions/data-creators/details';
import { toggleDetailRightPanel } from 'investigate-hosts/actions/ui-state-creators';
import { isInsightsAgent } from 'investigate-hosts/reducers/hosts/selectors';

const dispatchToActions = {
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
  policiesUnavailableMessage: policiesUnavailableMessage(state),
  hostName: hostName(state)
});

@classic
@tagName('box')
@classNames('host-overview')
class HostOverview extends Component {
  domIsReady = false;
  hostDetailsConfig = hostDetailsConfig;

  @service
  accessControl;

  @service
  i18n;

  @computed('showWindowsLogPolicy', 'channelFiltersConfig')
  get policiesConfig() {
    return getPoliciesPropertyConfig(this.showWindowsLogPolicy, this.channelFiltersConfig);
  }

  @computed('activePropertyPanelTab')
  get propertyPanelData() {
    if (this.activePropertyPanelTab === 'POLICIES') {
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
  }

  @computed('policiesUnavailableMessage')
  get propertyPanelErrorMessage() {
    const i18n = this.get('i18n');
    return this.get('accessControl.hasPolicyReadPermission') ? this.policiesUnavailableMessage : i18n.t('investigateHosts.hosts.properties.message.policyReadPermission');
  }

  didRender() {
    // Delay rendering the property panel
    setTimeout(() => {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        this.set('domIsReady', true);
      }
    }, 250);
  }

  @action
  expandEventAction(id) {
    if (this.get('isDetailRightPanelVisible')) {
      this.send('toggleDetailRightPanel');
    }
    this.send('expandEvent', id);
  }
}

export default connect(stateToComputed, dispatchToActions)(HostOverview);
