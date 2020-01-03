import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import { alias } from '@ember/object/computed';
import Component from '@ember/component';
import { toggleMftView } from 'investigate-hosts/actions/data-creators/downloads';
import { connect } from 'ember-redux';
import { success } from 'investigate-shared/utils/flash-messages';
import { downloadFilesToServer } from 'investigate-hosts/actions/data-creators/file-context';
import { isAgentMigrated } from 'investigate-hosts/reducers/details/overview/selectors';

const callBackOptions = (context) => ({
  onSuccess: () => success('investigateHosts.flash.genericFileDownloadRequestSent'),
  onFailure: (message) => context.get('flashMessage').showErrorMessage(message)
});

const stateToComputed = (state) => ({
  focusedHost: state.endpoint.detailsInput.agentId,
  serverId: state.endpointQuery.serverId,
  selections: state.endpoint.hostDownloads.mft.mftDirectory.selectedMftFileList,
  agentId: state.endpoint.detailsInput.agentId,
  selectedDirectory: state.endpoint.hostDownloads.mft.mftDirectory.selectedDirectoryForDetails,
  isAgentMigrated: isAgentMigrated(state)
});

const dispatchToActions = {
  toggleMftView,
  downloadFilesToServer
};

@classic
@tagName('box')
@classNames('mft-action-bar')
class mftActionBar extends Component {
  @service
  accessControl;

  callBackOptions = callBackOptions;

  @alias('focusedHost')
  machineId;

  @computed('selections', 'isAgentMigrated')
  get isDownloadToServerDisabled() {
    return this.isAgentMigrated || !this.selections.length;
  }

  @computed('selectedDirectory')
  get isShowActions() {
    const canManage = this.get('accessControl.endpointCanManageFiles');
    return this.selectedDirectory && canManage;
  }

  @action
  onDownloadFilesToServer() {
    const callBackOptions = this.get('callBackOptions')(this);
    const agentId = this.get('agentId');
    const fileSelections = this.get('selections');

    this.send('downloadFilesToServer', agentId, fileSelections, callBackOptions);
  }
}

export default connect(stateToComputed, dispatchToActions)(mftActionBar);