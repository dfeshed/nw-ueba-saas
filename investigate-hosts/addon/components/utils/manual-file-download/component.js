import classic from 'ember-classic-decorator';
import { computed, action } from '@ember/object';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { downloadWildcardsMatchedFilesRequest } from 'investigate-hosts/actions/data-creators/host';
import { downloadFilesToServer } from 'investigate-hosts/actions/data-creators/file-context';
import { filePathSeparatorFormat } from 'investigate-hosts/reducers/details/selectors';
import { filePathValidation, numberValidation } from 'investigate-hosts/util/util';

const callBackOptions = {
  onSuccess: () => success('investigateHosts.downloads.manualFileDownloads.manualFileDownloadsModal.success'),

  onFailure: (message) => failure(message, null, false)
};

const stateToComputed = (state) => ({
  filePathSeparatorFormat: filePathSeparatorFormat(state),
  machineOsType: state.endpoint.overview.hostOverview.machineIdentity.machineOsType
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
  isInvalidPath = false;
  isInvalidCount = false;
  isInvalidSize = false;

  @computed('filePath')
  get fileDownloadButtonStatus() {
    const { filePath } = this;
    const isWildcardPresent = filePath.includes('*');
    const isDownloadDisabled = !filePath.length;

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
    const lastIndexOfSeparator = filePath.lastIndexOf(filePathSeparatorFormat);
    const path = filePath.slice(0, lastIndexOfSeparator);
    const fileName = filePath.slice(lastIndexOfSeparator + 1);
    return {
      agentId,
      files: [{
        path,
        fileName
      }]
    };
  }

  @action
  validatePath() {
    const { filePath, machineOsType, fileDownloadButtonStatus: { isWildcardPresent }, filePathSeparatorFormat } = this;
    const filePathType = isWildcardPresent ? machineOsType : `${machineOsType}FullPath`;
    this.set('isInvalidPath', !filePathValidation(filePath, filePathType, filePathSeparatorFormat));
  }

  @action
  validateCount() {
    const { fileCount } = this;
    const { isInvalid, value } = numberValidation(fileCount, { lowerLimit: 1, upperLimit: 100 });

    this.set('isInvalidCount', isInvalid);
    this.set('fileCount', value);
  }

  @action
  validateSize() {
    const { fileSize } = this;
    const { isInvalid, value } = numberValidation(fileSize, { lowerLimit: 1 });

    this.set('isInvalidSize', isInvalid);
    this.set('fileSize', value);
  }

  @action
  requestManualFileDownloads() {
    const { fileDownloadButtonStatus: { isWildcardPresent },
      serverId,
      wildcardFileQuery,
      fullPathFileQuery: { agentId, files },
      isInvalidPath,
      isInvalidCount,
      isInvalidSize
    } = this;

    if (!isInvalidPath) {
      if (isWildcardPresent) {
        if (!isInvalidCount && !isInvalidSize) {
          this.send('downloadWildcardsMatchedFilesRequest', wildcardFileQuery, serverId, callBackOptions);
          this.closeConfirmModal();
        }
      } else {
        this.send('downloadFilesToServer', agentId, files, serverId, callBackOptions);
        this.closeConfirmModal();
      }
    }
  }

}

export default connect(stateToComputed, dispatchToActions)(MachineIsolationModal);