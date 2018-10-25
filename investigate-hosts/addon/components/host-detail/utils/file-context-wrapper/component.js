import Component from '@ember/component';
import { connect } from 'ember-redux';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

import {
  fileContextFileProperty,
  fileContextSelections,
  fileStatus,
  selectedFileChecksums,
  isRemediationAllowed
} from 'investigate-hosts/reducers/details/file-context/selectors';

import {
  setFileContextFileStatus,
  getFileContextFileStatus,
  retrieveRemediationStatus,
  downloadFilesToServer
} from 'investigate-hosts/actions/data-creators/file-context';

import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';

const stateToComputed = (state, { storeName }) => ({
  fileProperty: fileContextFileProperty(state, storeName),
  fileContextSelections: fileContextSelections(state, storeName),
  serviceList: serviceList(state, storeName),
  fileStatus: fileStatus(state, storeName),
  selectedFileChecksums: selectedFileChecksums(state, storeName),
  isRemediationAllowed: isRemediationAllowed(state, storeName),
  agentId: state.endpoint.detailsInput.agentId,
  serviceId: serviceId(state),
  timeRange: timeRange(state)
});

const dispatchToActions = {
  setFileContextFileStatus,
  getFileContextFileStatus,
  retrieveRemediationStatus,
  downloadFilesToServer
};


const ContextWrapper = Component.extend({
  tagName: 'hbox',

  classNames: ['file-context-wrapper'],

  isPaginated: false,

  storeName: '',

  columnsConfig: null,

  propertyConfig: null,

  tabName: '',

  @computed('fileContextSelections')
  fileDownloadButtonStatus(fileContextSelections) {
    // if selectedFilesLength be 1 and file download status be true then isDownloadToServerDisabled should return true
    const selectedFilesLength = fileContextSelections.length;

    return {
      isDownloadToServerDisabled: (selectedFilesLength === 0), // and file's downloaded status is true
      isSaveLocalAndFileAnalysisDisabled: (selectedFilesLength !== 1) // and once file's downloaded status is true else false
    };
  },

  flashMessage: service(),

  actions: {
    onDownloadFilesToServer() {
      const callBackOptions = {
        onSuccess: () => this.get('flashMessage').showFlashMessage('investigateHosts.flash.fileDownloadRequestSent'),
        onFailure: (message) => this.get('flashMessage').showErrorMessage(message)
      };
      const { agentId, fileContextSelections } = this.getProperties('agentId', 'fileContextSelections');

      this.send('downloadFilesToServer', agentId, fileContextSelections, callBackOptions);
    },
    onSaveLocalCopy() {
      // Placeholder for the next PR.
    },
    onAnalyzeFile() {
      // Placeholder for the next PR.
    },

    resetRiskScoreAction() {
      // Placeholder for the next PR.
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(ContextWrapper);
