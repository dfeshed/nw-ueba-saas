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
import { hasFiles, selectedFileStatusHistory, isRiskScoringServerNotConfigured } from 'investigate-files/reducers/file-list/selectors';
import { getDataSourceTab, riskState } from 'investigate-files/reducers/visuals/selectors';
import { selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';
import {
  resetDownloadId,
  setDataSourceTab,
  toggleRiskPanel,
  getFirstPageOfFiles
} from 'investigate-files/actions/data-creators';

import {
  getUpdatedRiskScoreContext
} from 'investigate-shared/actions/data-creators/risk-creators';

import { inject as service } from '@ember/service';

import { FILTER_TYPES } from './filter-type';

const stateToComputed = (state) => ({
  isSchemaLoaded: isSchemaLoaded(state),
  areFilesLoading: state.files.fileList.areFilesLoading,
  hasFiles: hasFiles(state),
  dataSourceTabs: getDataSourceTab(state),
  context: selectedFileStatusHistory(state),
  activeDataSourceTab: state.files.visuals.activeDataSourceTab,
  isEndpointServerOnline: !state.endpointServer.isSummaryRetrieveError,
  filter: state.files.filter,
  risk: riskState(state),
  filesFilters: state.files.filter.savedFilterList,
  selectedFilterId: selectedFilterId(state.files),
  savedFilter: savedFilter(state.files),
  selectedFile: state.files.fileList.selectedFile,
  isCertificateView: state.certificate.list.isCertificateView,
  isRiskScoringServerNotConfigured: isRiskScoringServerNotConfigured(state)
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
  resetFilters
};

/**
 * Container component that is responsible for orchestrating Files layout and top-level components.
 * @public
 */
const Files = Component.extend({
  tagName: 'vbox',

  classNames: 'rsa-investigate-files main-zone',

  features: service(),

  filterTypes: FILTER_TYPES,

  willDestroyElement() {
    this.send('resetDownloadId');
  },

  actions: {
    closeRiskPanel() {
      this.send('toggleRiskPanel', false);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Files);
