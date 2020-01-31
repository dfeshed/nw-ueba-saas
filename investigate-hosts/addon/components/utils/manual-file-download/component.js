import classic from 'ember-classic-decorator';
import { computed, action } from '@ember/object';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { downloadWildcardsMatchedFilesRequest } from 'investigate-hosts/actions/data-creators/host';
import { downloadFilesToServer } from 'investigate-hosts/actions/data-creators/file-context';
import { filePathSeparatorFormat } from 'investigate-hosts/reducers/details/selectors';

const callBackOptions = {
  onSuccess: () => success('investigateHosts.downloads.manualFileDownloads.manualFileDownloadsModal.success'),

  onFailure: (message) => failure(message, null, false)
};

const stateToComputed = (state) => ({
  filePathSeparatorFormat: filePathSeparatorFormat(state)
});

const dispatchToActions = {
  downloadWildcardsMatchedFilesRequest,
  downloadFilesToServer
};


@classic
class MachineIsolationModal extends Component {

  filePath = '';
  fileCount = null;
  fileSize = null;
  fileCountPlaceHolder = 10;
  fileSizePlaceHolder = 100;

  @computed('filePath')
  get fileDownloadButtonStatus() {
    const { filePath } = this;
    const isWildcardPresent = filePath.includes('*');
    const isDownloadDisabled = !this.filePath.length;
    return { isWildcardPresent, isDownloadDisabled };
  }

  @computed('filePath', 'fileCount', 'fileSize')
  get wildcardFileQuery() {
    const { filePath, fileCount, fileSize, agentId, fileCountPlaceHolder, fileSizePlaceHolder } = this;
    return {
      agentIds: [agentId],
      path: filePath,
      countFiles: +fileCount || fileCountPlaceHolder,
      maxFileSize: +fileSize || fileSizePlaceHolder
    };
  }

  @computed('filePath')
  get fullPathFileQuery() {
    const { filePath, agentId, filePathSeparatorFormat } = this;
    const filePathSections = filePath.split(filePathSeparatorFormat);
    return {
      agentId,
      files: [{
        path: filePath,
        fileName: filePathSections.lastItem
      }]
    };
  }

  @action
  requestManualFileDownloads() {
    const { fileDownloadButtonStatus: { isWildcardPresent },
      serverId,
      wildcardFileQuery,
      fullPathFileQuery: { agentId, files } } = this;

    if (isWildcardPresent) {
      this.send('downloadWildcardsMatchedFilesRequest', wildcardFileQuery, serverId, callBackOptions);
    } else {
      this.send('downloadFilesToServer', agentId, files, callBackOptions);
    }
    this.closeConfirmModal();
  }

}

export default connect(stateToComputed, dispatchToActions)(MachineIsolationModal);