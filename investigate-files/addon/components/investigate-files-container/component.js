import Component from '@ember/component';
import { connect } from 'ember-redux';

import { isSchemaLoaded } from 'investigate-files/reducers/schema/selectors';
import { hasFiles, getContext, getAlertsCount, getIncidentsCount, getDataSourceTab } from 'investigate-files/reducers/file-list/selectors';
import {
  fetchSchemaInfo,
  resetDownloadId,
  setDataSourceTab,
  toggleRiskPanel
} from 'investigate-files/actions/data-creators';
import { inject as service } from '@ember/service';

const stateToComputed = (state) => ({
  isSchemaLoaded: isSchemaLoaded(state),
  areFilesLoading: state.files.fileList.areFilesLoading,
  hasFiles: hasFiles(state),
  dataSourceTabs: getDataSourceTab(state),
  context: getContext(state),
  alertsCount: getAlertsCount(state),
  incidentsCount: getIncidentsCount(state),
  showRiskPanel: state.files.fileList.showRiskPanel
});

const dispatchToActions = {
  fetchSchemaInfo,
  resetDownloadId,
  setDataSourceTab,
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
    if (!this.get('hasFiles')) {
      this.send('fetchSchemaInfo');
    }
  },

  actions: {
    closeRiskPanel() {
      this.send('toggleRiskPanel', false);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Files);
