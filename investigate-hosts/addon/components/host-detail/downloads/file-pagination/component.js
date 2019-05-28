import Component from '@ember/component';
import { connect } from 'ember-redux';
import { fileCount, fileTotalLabel } from 'investigate-hosts/reducers/details/downloads/selectors';

const stateToComputed = (state) => ({
  fileTotal: fileTotalLabel(state),
  fileIndex: fileCount(state),
  selectedFileCount: state.endpoint.hostDownloads.downloads.selectedFileList.length
});

const FilePagination = Component.extend({
  tagName: 'section',
  classNames: ['file-pager']
});
export default connect(stateToComputed)(FilePagination);
