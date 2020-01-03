import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { fileCount, fileTotalLabel, mftSelectedFiles } from 'investigate-hosts/reducers/details/mft-directory/selectors';

const stateToComputed = (state) => ({
  fileTotal: fileTotalLabel(state),
  fileIndex: fileCount(state),
  selectedMftFiles: mftSelectedFiles(state)
});

@classic
@tagName('section')
@classNames('file-pager')
class MftPagination extends Component {}

export default connect(stateToComputed)(MftPagination);
