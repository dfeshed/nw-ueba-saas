import Component from '@ember/component';
import { connect } from 'ember-redux';
import { fileCount, fileTotalLabel } from 'investigate-files/reducers/file-list/selectors';

const stateToComputed = (state) => ({
  fileTotal: fileTotalLabel(state), // Total number of files in search result
  fileIndex: fileCount(state),
  selectedFileCount: state.files.fileList.selectedFileList.length
});

const Pager = Component.extend({
  tagName: 'section',
  classNames: ['file-pager']
});
export default connect(stateToComputed)(Pager);
