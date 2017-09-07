import Component from 'ember-component';
import { connect } from 'ember-redux';

import layout from './template';
import { fileCount } from 'investigate-files/reducers/file-list/selectors';

const stateToComputed = ({ files }) => ({
  fileTotal: files.fileList.totalItems, // Total number of files in search result
  fileIndex: fileCount(files)
});

const Pager = Component.extend({
  layout,
  tagName: 'section',
  classNames: ['file-pager']
});
export default connect(stateToComputed, undefined)(Pager);