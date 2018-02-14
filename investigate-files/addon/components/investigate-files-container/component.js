import Component from 'ember-component';
import { connect } from 'ember-redux';

import { isSchemaLoaded } from 'investigate-files/reducers/schema/selectors';
import { hasFiles } from 'investigate-files/reducers/file-list/selectors';
import {
  addSystemFilter,
  resetFilters,
  getPageOfFiles,
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
  getPageOfFiles,
  fetchSchemaInfo,
  resetDownloadId
};

/**
 * Container component that is responsible for orchestrating Files layout and top-level components.
 * @public
 */
const Files = Component.extend({
  tagName: 'vbox',

  classNames: 'rsa-investigate-files main-zone',

  willDestroyElement() {
    this.send('resetDownloadId');
  },

  init() {
    this._super(...arguments);
    if (!this.get('hasFiles')) {
      this.send('fetchSchemaInfo');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Files);
