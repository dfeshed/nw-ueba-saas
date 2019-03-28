import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import {
  serviceList,
  checksums,
  isAnyFileFloatingOrMemoryDll,
  isCertificateViewDisabled,
  fileDownloadButtonStatus
} from 'investigate-files/reducers/file-list/selectors';
import {
  exportFileAsCSV,
  getAllServices,
  saveFileStatus,
  getSavedFileStatus,
  retrieveRemediationStatus,
  changeEndpointServerSelection
} from 'investigate-files/actions/data-creators';
import { setEndpointServer } from 'investigate-shared/actions/data-creators/endpoint-server-creators';
import { success, failure, warning } from 'investigate-shared/utils/flash-messages';
import { resetRiskScore } from 'investigate-shared/actions/data-creators/risk-creators';
import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';

const stateToComputed = (state) => ({
  // Total number of files in search result
  totalItems: state.files.fileList.totalItems,
  downloadId: state.files.fileList.downloadId,
  checksums: checksums(state),
  selectedFileCount: state.files.fileList.selectedFileList.length,
  serviceList: serviceList(state),
  itemList: state.files.fileList.selectedFileList,
  servers: state.endpointServer,
  serverId: state.endpointQuery.serverId,
  fileStatusData: state.files.fileList.fileStatusData,
  remediationStatus: state.files.fileList.isRemediationAllowed,
  restrictedFileList: state.fileStatus.restrictedFileList,
  serviceId: serviceId(state),
  timeRange: timeRange(state),
  certificateLoadStatus: state.certificate.list.certificatesLoadingStatus,
  isFloatingOrMemoryDll: isAnyFileFloatingOrMemoryDll(state),
  fileDownloadButtonStatus: fileDownloadButtonStatus(state),
  isCertificateViewDisabled: isCertificateViewDisabled(state),
  isCertificateView: state.certificate.list.isCertificateView
});

const dispatchToActions = {
  exportFileAsCSV,
  getAllServices,
  saveFileStatus,
  setEndpointServer,
  getSavedFileStatus,
  retrieveRemediationStatus,
  resetRiskScore,
  changeEndpointServerSelection
};
/**
 * Toolbar that provides search filtering.
 * @public
 */
const ToolBar = Component.extend({
  tagName: 'hbox',

  classNames: 'files-toolbar',

  i18n: service(),

  contextualHelp: service(),

  agentIds: [],

  @computed('fileStatusData')
  data(fileStatusData) {
    return { ...fileStatusData };
  },
  @computed('itemList', 'isCertificateViewDisabled')
  selectedThumbPrint(selectedList, isCertificateViewDisabled) {
    let selectedThumb = 'all';
    if (selectedList.length > 0 && !isCertificateViewDisabled) {
      const [{ signature: { thumbprint } }] = selectedList;
      selectedThumb = thumbprint;
    }
    return selectedThumb;
  },
  @computed('itemList', 'isCertificateViewDisabled')
  isCertificateViewDisabledTitle(selectedList, isCertificateViewDisabled) {
    const i18n = this.get('i18n');
    if (isCertificateViewDisabled) {
      const MORE_THAN_TEN_FILES_SELECTED_TOOLTIP = i18n.t('investigateFiles.certificate.toolTipCertificateViewDisabled', { count: 1 }).toString();
      const FILES_ARE_NOT_SIGNED_TOOLTIP = i18n.t('investigateFiles.certificate.unsigned.toolTipCertificateViewDisabled').toString();
      return selectedList.length > 1 ? MORE_THAN_TEN_FILES_SELECTED_TOOLTIP : FILES_ARE_NOT_SIGNED_TOOLTIP;
    }
  },
  actions: {
    resetRiskScoreAction(itemsList) {
      const callBackOptions = {
        onSuccess: (response) => {
          const { data } = response;
          if (data === itemsList.length) {
            success('investigateFiles.riskScore.success');
          } else {
            warning('investigateFiles.riskScore.warning');
          }
        },
        onFailure: () => failure('investigateFiles.riskScore.error')
      };
      this.send('resetRiskScore', itemsList, 'FILE', callBackOptions);
    },

    handleServiceSelection(service) {
      this.send('changeEndpointServerSelection', service);
      if (this.closeRiskPanel) {
        this.closeRiskPanel();
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ToolBar);
