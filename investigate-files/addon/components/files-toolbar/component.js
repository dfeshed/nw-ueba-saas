import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
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
import { selectedServiceWithStatus } from 'investigate-shared/selectors/endpoint-server/selectors';

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
  isCertificateView: state.certificate.list.isCertificateView,
  selectedServiceWithStatus: selectedServiceWithStatus(state)
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
@classic
@tagName('hbox')
@classNames('files-toolbar')
class ToolBar extends Component {
  @service
  i18n;

  @service
  contextualHelp;

  @computed('fileStatusData')
  get data() {
    return { ...this.fileStatusData };
  }

  @computed('itemList', 'isCertificateViewDisabled')
  get selectedThumbPrint() {
    let selectedThumb = 'all';
    if (this.itemList.length > 0 && !this.isCertificateViewDisabled) {
      const [{ signature: { thumbprint } }] = this.itemList;
      selectedThumb = thumbprint;
    }
    return selectedThumb;
  }

  @computed('itemList', 'isCertificateViewDisabled', 'selectedServiceWithStatus')
  get isCertificateViewDisabledTitle() {
    const i18n = this.get('i18n');
    if (this.isCertificateViewDisabled) {
      if (!this.selectedServiceWithStatus.isServiceOnline) {
        return i18n.t('investigateFiles.endpointServerOffline').toString();
      }
      const MORE_THAN_TEN_FILES_SELECTED_TOOLTIP = i18n.t('investigateFiles.certificate.toolTipCertificateViewDisabled', { count: 1 }).toString();
      const FILES_ARE_NOT_SIGNED_TOOLTIP = i18n.t('investigateFiles.certificate.unsigned.toolTipCertificateViewDisabled').toString();
      return this.itemList.length > 1 ? MORE_THAN_TEN_FILES_SELECTED_TOOLTIP : FILES_ARE_NOT_SIGNED_TOOLTIP;
    }
    return '';
  }

  init() {
    super.init(...arguments);
    this.agentIds = this.agentIds || [];
  }

  @action
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
  }

  @action
  handleServiceSelection(service) {
    this.send('changeEndpointServerSelection', service);
    if (this.closeRiskPanel) {
      this.closeRiskPanel();
    }
  }
}

export default connect(stateToComputed, dispatchToActions)(ToolBar);
