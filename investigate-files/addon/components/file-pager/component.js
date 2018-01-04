import Component from 'ember-component';
import { connect } from 'ember-redux';
import { fileCount, fileCountForDisplay } from 'investigate-files/reducers/file-list/selectors';
import { isValidExpression } from 'investigate-files/reducers/filter/selectors';

const stateToComputed = (state) => ({
  fileTotal: fileCountForDisplay(state), // Total number of files in search result
  fileIndex: fileCount(state),
  isValidExpression: isValidExpression(state)
});

const Pager = Component.extend({
  tagName: 'section',
  classNames: ['file-pager']
});
export default connect(stateToComputed)(Pager);
