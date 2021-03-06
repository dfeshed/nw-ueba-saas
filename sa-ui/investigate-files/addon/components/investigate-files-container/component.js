import classic from 'ember-classic-decorator';
import { action } from '@ember/object';
import { classNames, tagName } from '@ember-decorators/component';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  applyFilters,
  createCustomSearch,
  applySavedFilters,
  deleteFilter,
  resetFilters
} from 'investigate-shared/actions/data-creators/filter-creators';

import { isSchemaLoaded } from 'investigate-files/reducers/schema/selectors';
import { hasFiles, selectedFileStatusHistory, checksums } from 'investigate-files/reducers/file-list/selectors';
import { getDataSourceTab, riskState } from 'investigate-files/reducers/visuals/selectors';
import { selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';
import { selectedServiceWithStatus } from 'investigate-shared/selectors/endpoint-server/selectors';
import {
  resetDownloadId,
  setDataSourceTab,
  toggleRiskPanel,
  getFirstPageOfFiles,
  setSelectedIndex,
  downloadFilesToServer
} from 'investigate-files/actions/data-creators';

import {
  getUpdatedRiskScoreContext
} from 'investigate-shared/actions/data-creators/risk-creators';

import { FILTER_TYPES } from './filter-type';

import CONFIG from '../file-details/base-property-config';

import { success, failure } from 'investigate-shared/utils/flash-messages';
import { componentSelectionForFileType } from 'investigate-shared/utils/file-analysis-view-util';
import { saveLocalFileCopy } from 'investigate-shared/actions/data-creators/file-analysis-creators';

const callBackOptions = {
  onSuccess: () => success('investigateHosts.flash.fileDownloadRequestSent'),
  onFailure: (message) => failure(message, null, false)
};

const stateToComputed = (state) => ({
  isSchemaLoaded: isSchemaLoaded(state),
  hasFiles: hasFiles(state),
  dataSourceTabs: getDataSourceTab(state),
  context: selectedFileStatusHistory(state),
  activeDataSourceTab: state.files.visuals.activeDataSourceTab,
  selectedServiceData: selectedServiceWithStatus(state),
  filter: state.files.filter,
  risk: riskState(state),
  filesFilters: state.files.filter.savedFilterList,
  selectedFilterId: selectedFilterId(state.files),
  savedFilter: savedFilter(state.files),
  selectedFile: state.files.fileList.selectedFile,
  isCertificateView: state.certificate.list.isCertificateView,
  selectedIndex: state.files.fileList.selectedIndex,
  fileProperty: state.files.fileList.selectedDetailFile,
  checksums: checksums(state),
  serverId: state.endpointQuery.serverId,
  selections: state.files.fileList.selectedFileList
});

const dispatchToActions = {
  resetDownloadId,
  setDataSourceTab,
  toggleRiskPanel,
  applyFilters,
  createCustomSearch,
  getFirstPageOfFiles,
  getUpdatedRiskScoreContext,
  applySavedFilters,
  deleteFilter,
  resetFilters,
  setSelectedIndex,
  downloadFilesToServer,
  saveLocalFileCopy
};

/**
 * Container component that is responsible for orchestrating Files layout and top-level components.
 * @public
 */
@classic
@tagName('box')
@classNames('rsa-investigate-files main-zone')
class Files extends Component {
  @service
  features;

  filterTypes = FILTER_TYPES;
  propertyConfig = CONFIG;
  callBackOptions = callBackOptions;

  willDestroyElement() {
    this.send('resetDownloadId');
  }

  @action
  onPanelClose(side) {
    if (side === 'right') {
      this.send('setSelectedIndex', -1);
    }
  }

  @action
  onDownloadFilesToServer() {
    const [selectedDetailFile] = this.get('selections');
    const callBackOptions = this.get('callBackOptions');
    const [checksumSha256] = this.get('checksums');
    this.send('downloadFilesToServer', checksumSha256, selectedDetailFile.serviceId, callBackOptions);
  }

  @action
  onAnalyzeFile() {
    const [selectedDetailFile] = this.get('selections');
    const { serviceId, downloadInfo: { serviceId: sourceSid } } = selectedDetailFile;
    if (serviceId) {
      const { format, checksumSha256 } = selectedDetailFile;
      const fileFormat = componentSelectionForFileType(format).format || '';
      window.open(`${window.location.origin}/investigate/files/${checksumSha256}?checksum=${checksumSha256}&sid=${serviceId}&sourceSid=${sourceSid}&fileFormat=${fileFormat}&tabName=ANALYSIS`, '_self');
    }
  }

  @action
  onSaveLocalCopy() {
    const [selectedDetailFile] = this.get('selections');
    const callBackOptions = {
      onSuccess: () => success('investigateHosts.flash.fileDownloadRequestSent'),
      onFailure: (message) => failure(message, null, false)
    };
    this.send('saveLocalFileCopy', selectedDetailFile, callBackOptions);
  }
}

export default connect(stateToComputed, dispatchToActions)(Files);
