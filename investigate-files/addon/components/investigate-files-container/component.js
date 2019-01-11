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
import { hasFiles, selectedFileStatusHistory } from 'investigate-files/reducers/file-list/selectors';
import { getDataSourceTab, riskState } from 'investigate-files/reducers/visuals/selectors';
import { selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';
import { selectedServiceWithStatus } from 'investigate-shared/selectors/endpoint-server/selectors';
import {
  resetDownloadId,
  setDataSourceTab,
  toggleRiskPanel,
  getFirstPageOfFiles,
  setSelectedIndex
} from 'investigate-files/actions/data-creators';

import {
  getUpdatedRiskScoreContext
} from 'investigate-shared/actions/data-creators/risk-creators';

import { inject as service } from '@ember/service';

import { FILTER_TYPES } from './filter-type';

import CONFIG from '../file-details/base-property-config';

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
  fileProperty: state.files.fileList.selectedDetailFile
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
  setSelectedIndex
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

  willDestroyElement() {
    this.send('resetDownloadId');
  },

  actions: {
    onPanelClose() {
      this.send('setSelectedIndex', null);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Files);
