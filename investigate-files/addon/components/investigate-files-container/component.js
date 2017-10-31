import Component from 'ember-component';
import { connect } from 'ember-redux';
import service from 'ember-service/inject';
import $ from 'jquery';

import { isSchemaLoaded } from 'investigate-files/reducers/schema/selectors';
import { hasFiles } from 'investigate-files/reducers/file-list/selectors';
import {
  addSystemFilter,
  resetFilters,
  getPageOfFiles,
  getFilter,
  fetchSchemaInfo,
  resetDownloadId
} from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  isSchemaLoaded: isSchemaLoaded(state),
  areFilesLoading: state.files.fileList.areFilesLoading,
  hasFiles: hasFiles(state),
  filesFilters: state.files.filter.fileFilters
});

const dispatchToActions = {
  addSystemFilter,
  resetFilters,
  getFilter,
  getPageOfFiles,
  fetchSchemaInfo,
  resetDownloadId
};

/**
 * Container component that is responsible for orchestrating Files layout and top-level components.
 * @public
 */
const Files = Component.extend({
  tagName: 'hbox',

  classNames: 'rsa-investigate-files main-zone',

  eventBus: service(),

  actions: {
    applyCustomFilter(filter) {
      const { criteria: { expressionList } } = filter;
      this.send('addSystemFilter', expressionList);
    }
  },

  // Work around to set row styles when scroll width is increased
  didRender() {
    this._super(...arguments);
    const rsaDataTableBody = $('.rsa-data-table-body');
    if (rsaDataTableBody.length !== 0) {
      const dataTableTotalWidth = rsaDataTableBody[0].scrollWidth;
      $('.rsa-data-table-body-rows').innerWidth(dataTableTotalWidth);
    }
  },

  willDestroyElement() {
    this.send('resetDownloadId');
  },

  init() {
    this._super(...arguments);
    if (!this.get('hasFiles')) {
      this.send('fetchSchemaInfo');
      this.send('getFilter');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Files);
