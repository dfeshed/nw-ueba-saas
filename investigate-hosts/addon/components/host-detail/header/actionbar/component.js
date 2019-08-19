import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import {
  downloadLink,
  hostName,
  selectedSnapshot
} from 'investigate-hosts/reducers/details/overview/selectors';

import {
  exportFileContext,
  setTransition
} from 'investigate-hosts/actions/data-creators/details';
import { toggleDetailRightPanel } from 'investigate-hosts/actions/ui-state-creators';
import { isOnOverviewTab, isActiveTabDownloads } from 'investigate-hosts/reducers/visuals/selectors';
import { changeSnapshotTime } from 'investigate-hosts/actions/data-creators/host-details';
const stateToComputed = (state) => ({
  hostName: hostName(state),
  scanTime: state.endpoint.detailsInput.scanTime,
  selectedSnapshot: selectedSnapshot(state),
  host: state.endpoint.overview.hostDetails,
  agentId: state.endpoint.detailsInput.agentId,
  snapShots: state.endpoint.detailsInput.snapShots,
  downloadLink: downloadLink(state),
  isProcessDetailsView: state.endpoint.visuals.isProcessDetailsView,
  isDetailRightPanelVisible: state.endpoint.detailsInput.isDetailRightPanelVisible,
  showRightPanelButton: isOnOverviewTab(state),
  hideSnapshotAndExploreSearch: isActiveTabDownloads(state)
});

const dispatchToActions = {
  setTransition,
  changeSnapshotTime,
  exportFileContext,
  toggleDetailRightPanel
};

const ActionBar = Component.extend({

  tagName: 'hbox',

  classNames: ['actionbar', 'controls'],

  eventBus: service(),

  i18n: service(),

  flashMessage: service(),

  actions: {
    setSelect(option) {
      this.send('changeSnapshotTime', { agentId: this.get('agentId'), scanTime: option });
    },
    openInAction() {
      const host = this.get('host');
      const { machineIdentity: { agentVersion } } = host;
      const url = `ecatui:///machines/${host.id}`;
      const i18n = this.get('i18n');
      if (agentVersion && agentVersion.startsWith('4.4')) {
        window.open(url);
      } else {
        this.get('flashMessage').showErrorMessage(i18n.t('investigateHosts.hosts.moreActions.notAnEcatAgent'));
      }
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(ActionBar);
