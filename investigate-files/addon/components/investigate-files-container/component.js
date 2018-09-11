import Component from '@ember/component';
import { connect } from 'ember-redux';
import { next } from '@ember/runloop';

import { isSchemaLoaded } from 'investigate-files/reducers/schema/selectors';
import { hasFiles, getDataSourceTab, selectedFileStatusHistory } from 'investigate-files/reducers/file-list/selectors';
import { getAlertsCount, getIncidentsCount } from 'investigate-shared/selectors/context';
import {
  fetchSchemaInfo,
  resetDownloadId,
  setDataSourceTab,
  setAlertTab,
  toggleRiskPanel
} from 'investigate-files/actions/data-creators';
import { inject as service } from '@ember/service';

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
  isEndpointServerOnline: !state.endpointServer.isSummaryRetrieveError
});

const dispatchToActions = {
  fetchSchemaInfo,
  resetDownloadId,
  setDataSourceTab,
  setAlertTab,
  toggleRiskPanel
};

/**
 * Container component that is responsible for orchestrating Files layout and top-level components.
 * @public
 */
const Files = Component.extend({
  tagName: 'vbox',

  classNames: 'rsa-investigate-files main-zone',

  features: service(),

  willDestroyElement() {
    this.send('resetDownloadId');
  },

  init() {
    this._super(...arguments);
    next(() => {
      if (!this.get('hasFiles') && !this.get('isDestroyed') && !this.get('isDestroying')) {
        this.send('fetchSchemaInfo');
      }
    });
  },

  actions: {
    closeRiskPanel() {
      this.send('toggleRiskPanel', false);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Files);
