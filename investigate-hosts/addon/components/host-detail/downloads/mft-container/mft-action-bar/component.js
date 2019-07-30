import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { toggleMftView } from 'investigate-hosts/actions/data-creators/downloads';
import { connect } from 'ember-redux';
import computed, { alias } from 'ember-computed-decorators';
import { success } from 'investigate-shared/utils/flash-messages';

import { downloadFilesToServer } from 'investigate-hosts/actions/data-creators/file-context';

const callBackOptions = (context) => ({
  onSuccess: () => success('investigateHosts.flash.mftFileDownloadRequestSent'),
  onFailure: (message) => context.get('flashMessage').showErrorMessage(message)
});

const stateToComputed = (state) => ({
  focusedHost: state.endpoint.detailsInput.agentId,
  serverId: state.endpointQuery.serverId,
  selections: state.endpoint.hostDownloads.mft.mftDirectory.selectedMftFileList,
  agentId: state.endpoint.detailsInput.agentId,
  isShowActions: state.endpoint.hostDownloads.mft.mftDirectory.selectedDirectoryForDetails
});

const dispatchToActions = {
  toggleMftView,
  downloadFilesToServer
};

const mftActionBar = Component.extend({
  tagName: 'box',

  classNames: ['mft-action-bar'],

  accessControl: service(),

  callBackOptions,

  @alias('focusedHost')
  machineId: null,

  @computed('selections')
  isDownloadToServerDisabled(selections) {
    return !selections.length;
  },
  actions: {
    onDownloadFilesToServer() {
      const callBackOptions = this.get('callBackOptions')(this);
      const agentId = this.get('agentId');
      const fileSelections = this.get('selections');

      this.send('downloadFilesToServer', agentId, fileSelections, callBackOptions);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(mftActionBar);