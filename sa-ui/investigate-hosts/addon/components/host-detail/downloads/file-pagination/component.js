import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { fileCount, fileTotalLabel } from 'investigate-hosts/reducers/details/downloads/selectors';

const stateToComputed = (state) => ({
  fileTotal: fileTotalLabel(state),
  fileIndex: fileCount(state),
  selectedFileCount: state.endpoint.hostDownloads.downloads.selectedFileList.length
});

@classic
@tagName('section')
@classNames('file-pager')
class FilePagination extends Component {}

export default connect(stateToComputed)(FilePagination);
