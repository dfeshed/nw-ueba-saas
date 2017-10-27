import Component from 'ember-component';
import { connect } from 'ember-redux';
import { fileCount } from 'investigate-files/reducers/file-list/selectors';

const stateToComputed = (state) => ({
  fileTotal: state.files.fileList.totalItems, // Total number of files in search result
  fileIndex: fileCount(state)
});

const Pager = Component.extend({
  tagName: 'section',
  classNames: ['file-pager']
});
export default connect(stateToComputed, undefined)(Pager);