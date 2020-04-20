import classic from 'ember-classic-decorator';
import { action, computed } from '@ember/object';
import { classNames, tagName } from '@ember-decorators/component';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  downloadLink,
  hostName,
  selectedSnapshot,
  isSnapshotsAvailable
} from 'investigate-hosts/reducers/details/overview/selectors';

import {
  exportFileContext,
  setTransition
} from 'investigate-hosts/actions/data-creators/details';
import { toggleDetailRightPanel } from 'investigate-hosts/actions/ui-state-creators';
import { isOnOverviewTab, isActiveTabDownloads, isSnapShotDisable } from 'investigate-hosts/reducers/visuals/selectors';
import { changeSnapshotTime } from 'investigate-hosts/actions/data-creators/host-details';
const stateToComputed = (state) => ({
  hostName: hostName(state),
  scanTime: state.endpoint.detailsInput.scanTime,
  selectedSnapshot: selectedSnapshot(state),
  host: state.endpoint.overview.hostDetails,
  agentId: state.endpoint.detailsInput.agentId,
  snapShots: state.endpoint.detailsInput.snapShots,
  downloadLink: downloadLink(state),
  isSnapShotDisable: isSnapShotDisable(state),
  isDetailRightPanelVisible: state.endpoint.detailsInput.isDetailRightPanelVisible,
  showRightPanelButton: isOnOverviewTab(state),
  hideSnapshotAndExploreSearch: isActiveTabDownloads(state),
  listAllFiles: state.endpoint.visuals.listAllFiles,
  activeHostDetailTab: state.endpoint.visuals.activeHostDetailTab,
  isSnapshotsAvailable: isSnapshotsAvailable(state)
});

const dispatchToActions = {
  setTransition,
  changeSnapshotTime,
  exportFileContext,
  toggleDetailRightPanel
};

@classic
@tagName('hbox')
@classNames('actionbar', 'controls')
class ActionBar extends Component {
  @service
  eventBus;

  @service
  i18n;

  @service
  flashMessage;

  @action
  setSelect(option) {
    this.send('changeSnapshotTime', { agentId: this.get('agentId'), scanTime: option });
  }

  @action
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

  @computed('listAllFiles', 'activeHostDetailTab', 'isSnapshotsAvailable')
  get snapShotDisableTooltip() {
    const { activeHostDetailTab, i18n, isSnapshotsAvailable, listAllFiles } = this;
    if (activeHostDetailTab === 'FILES' && !isSnapshotsAvailable) {
      return i18n.t('investigateHosts.files.toolTip.noSnapShot');
    } else if (listAllFiles && activeHostDetailTab === 'FILES') {
      return i18n.t('investigateHosts.files.toolTip.disableSnapShot');
    }
    return '';
  }
}

export default connect(stateToComputed, dispatchToActions)(ActionBar);
