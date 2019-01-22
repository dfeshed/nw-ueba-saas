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

import { inject as service } from '@ember/service';

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
const Files = Component.extend({
  tagName: 'box',

  classNames: 'rsa-investigate-files main-zone',

  features: service(),

  filterTypes: FILTER_TYPES,

  propertyConfig: CONFIG,

  callBackOptions,

  willDestroyElement() {
    this.send('resetDownloadId');
  },

  actions: {
    onPanelClose(side) {
      if (side === 'right') {
        this.send('setSelectedIndex', null);
      }
    },

    onDownloadFilesToServer() {
      const [selectedDetailFile] = this.get('selections');
      const callBackOptions = this.get('callBackOptions');
      const [checksumSha256] = this.get('checksums');
      this.send('downloadFilesToServer', checksumSha256, selectedDetailFile.serviceId, callBackOptions);
    },

    onAnalyzeFile() {
      const [selectedDetailFile] = this.get('selections');
      if (selectedDetailFile) {
        const { format, checksumSha256 } = selectedDetailFile;
        const serverId = this.get('serverId');
        const fileFormat = componentSelectionForFileType(format).format || '';
        window.open(`${window.location.origin}/investigate/files/${checksumSha256}?checksum=${checksumSha256}&sid=${serverId}&fileFormat=${fileFormat}&tabName=ANALYSIS`, '_self');
      }
    },

    onSaveLocalCopy() {
      const callBackOptions = {
        onSuccess: () => success('investigateHosts.flash.fileDownloadRequestSent'),
        onFailure: (message) => failure(message, null, false)
      };
      this.send('saveLocalFileCopy', this.get('selections')[0], callBackOptions);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Files);
