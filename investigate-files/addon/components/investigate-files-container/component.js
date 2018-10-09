import Component from '@ember/component';
import { connect } from 'ember-redux';
import { applyFilters, createCustomSearch, applySavedFilters, deleteFilter, resetFilters } from 'investigate-files/actions/filter-creators';

import { isSchemaLoaded } from 'investigate-files/reducers/schema/selectors';
import { hasFiles, getDataSourceTab, selectedFileStatusHistory } from 'investigate-files/reducers/file-list/selectors';
import { getAlertsCount, getIncidentsCount } from 'investigate-shared/selectors/context';
import { selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';
import {
  resetDownloadId,
  setDataSourceTab,
  toggleRiskPanel,
  getFirstPageOfFiles,
  getUpdatedRiskScoreContext
} from 'investigate-files/actions/data-creators';
import { inject as service } from '@ember/service';

import { FILTER_TYPES } from './filter-type';

const stateToComputed = (state) => ({
  isSchemaLoaded: isSchemaLoaded(state),
  areFilesLoading: state.files.fileList.areFilesLoading,
  hasFiles: hasFiles(state),
  dataSourceTabs: getDataSourceTab(state),
  context: selectedFileStatusHistory(state),
  contextError: state.files.fileList.contextError,
  alertsCount: getAlertsCount(state),
  incidentsCount: getIncidentsCount(state),
  activeDataSourceTab: state.files.fileList.activeDataSourceTab,
  contextLoadingStatus: state.files.fileList.contextLoadingStatus,
  isEndpointServerOnline: !state.endpointServer.isSummaryRetrieveError,
  filter: state.files.filter,
  activeRiskSeverityTab: state.files.visuals.activeRiskSeverityTab,
  riskScoreContext: state.files.fileList.riskScoreContext,
  alertsData: state.files.fileList.alertsData,
  filesFilters: state.files.filter.savedFilterList,
  selectedFilterId: selectedFilterId(state.files),
  savedFilter: savedFilter(state.files),
  selectedFile: state.files.fileList.selectedFile
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
