import classic from 'ember-classic-decorator';
import { classNames, classNameBindings } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setSelectDirectoryForDetails, getSubDirectories } from 'investigate-hosts/actions/data-creators/downloads';

const stateToComputed = (state) => ({
  selectedDirectoryForDetails: state.endpoint.hostDownloads.mft.mftDirectory.selectedDirectoryForDetails
});

const dispatchToActions = {
  setSelectDirectoryForDetails,
  getSubDirectories
};

@classic
@classNames('mft-directory')
@classNameBindings('selectedDirectory')
class MFTDirectory extends Component {
  @computed('data')
  get isMainDrive() {
    return this.data.parentDirectory === 0;
  }

  @computed('selectedDirectoryForDetails', 'data')
  get selectedDirectory() {
    return this.data.recordNumber === this.selectedDirectoryForDetails;
  }

  @action
  fetchSubdirectoriesAndFiles(data) {
    const { recordNumber, name, fullPathName } = data;

    this.send('setSelectDirectoryForDetails', {
      selectedDirectoryForDetails: recordNumber,
      fileSource: 'drive',
      pageSize: 100,
      isDirectories: false,
      inUse: true,
      fullPathName,
      name });
    this.send('getSubDirectories');
  }
}

export default connect(stateToComputed, dispatchToActions)(MFTDirectory);