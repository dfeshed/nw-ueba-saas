import Component from '@ember/component';
import { connect } from 'ember-redux';
import { applyFilters, createCustomSearch } from 'investigate-files/actions/filter-creators';

import { isSchemaLoaded } from 'investigate-files/reducers/schema/selectors';
import { hasFiles, getDataSourceTab, selectedFileStatusHistory } from 'investigate-files/reducers/file-list/selectors';
import { getAlertsCount, getIncidentsCount } from 'investigate-shared/selectors/context';
import {
  resetDownloadId,
  setDataSourceTab,
  setAlertTab,
  toggleRiskPanel,
  getFirstPageOfFiles
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
  activeAlertTab: state.files.fileList.activeAlertTab,
  contextLoadingStatus: state.files.fileList.contextLoadingStatus,
  isEndpointServerOnline: !state.endpointServer.isSummaryRetrieveError,
  filter: state.files.filter,
  alertsData: state.files.fileList.alertsData
});

const dispatchToActions = {
  resetDownloadId,
  setDataSourceTab,
  setAlertTab,
  toggleRiskPanel,
  applyFilters,
  createCustomSearch,
  getFirstPageOfFiles
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
