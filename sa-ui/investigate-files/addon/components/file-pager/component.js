import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { fileCount, fileTotalLabel } from 'investigate-files/reducers/file-list/selectors';

const stateToComputed = (state) => ({
  fileTotal: fileTotalLabel(state), // Total number of files in search result
  fileIndex: fileCount(state),
  selectedFileCount: state.files.fileList.selectedFileList.length
});

@classic
@tagName('section')
@classNames('file-pager')
class Pager extends Component {}

export default connect(stateToComputed)(Pager);
