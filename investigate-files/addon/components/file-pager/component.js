import Component from 'ember-component';
import { connect } from 'ember-redux';
import { fileCount } from 'investigate-files/reducers/file-list/selectors';
import { isValidExpression } from 'investigate-files/reducers/filter/selectors';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  fileTotal: state.files.fileList.totalItems, // Total number of files in search result
  fileIndex: fileCount(state),
  isValidExpression: isValidExpression(state)
});

const Pager = Component.extend({
  tagName: 'section',
  classNames: ['file-pager'],

  @computed('fileTotal')
  displayTotal(fileTotal) {
    let total = fileTotal;
    // For performance reasons api returns 1000 as totalItems when filter is applied, even if result is more than 1000
    // Make sure we append '+' to indicate user more files are present
    if (this.get('isValidExpression') && fileTotal >= 1000) {
      total = `${fileTotal}+`;
    }
    return total;
  }
});
export default connect(stateToComputed, undefined)(Pager);