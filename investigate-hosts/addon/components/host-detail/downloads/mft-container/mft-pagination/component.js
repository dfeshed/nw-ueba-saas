import Component from '@ember/component';
import { connect } from 'ember-redux';
import { fileCount, fileTotalLabel, mftSelectedFiles } from 'investigate-hosts/reducers/details/mft-directory/selectors';

const stateToComputed = (state) => ({
  fileTotal: fileTotalLabel(state),
  fileIndex: fileCount(state),
  selectedMftFiles: mftSelectedFiles(state)
});

const MftPagination = Component.extend({
  tagName: 'section',
  classNames: ['file-pager']
});
export default connect(stateToComputed)(MftPagination);
