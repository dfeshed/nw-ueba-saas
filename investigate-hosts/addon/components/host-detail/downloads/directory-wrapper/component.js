import classic from 'ember-classic-decorator';
import { action } from '@ember/object';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getSubDirectories, setSelectDirectoryForDetails } from 'investigate-hosts/actions/data-creators/downloads';

const stateToComputed = (state) => ({
  mftDirectory: state.endpoint.hostDownloads.mft.mftDirectory.subDirectories,
  fileSource: state.endpoint.hostDownloads.mft.mftDirectory.fileSource,
  selectedMftFile: state.endpoint.hostDownloads.downloads.selectedMftFile
});

const dispatchToActions = {
  getSubDirectories,
  setSelectDirectoryForDetails
};

@classic
@tagName('section')
@classNames('directory-wrapper')
class DirectoryWrapper extends Component {
  @action
  fetchFiles(selectedAction) {
    const isDirectories = false;
    const pageSize = 100;
    const selectedDirectoryForDetails = -1;

    if (selectedAction === 'allFiles') {
      this.send('setSelectDirectoryForDetails', { selectedDirectoryForDetails, fileSource: 'allFiles', pageSize, isDirectories, inUse: true });
    } else {
      this.send('setSelectDirectoryForDetails', { selectedDirectoryForDetails, fileSource: 'deletedFiles', pageSize, isDirectories, inUse: false });
    }
    this.send('getSubDirectories');
  }
}

export default connect(stateToComputed, dispatchToActions)(DirectoryWrapper);