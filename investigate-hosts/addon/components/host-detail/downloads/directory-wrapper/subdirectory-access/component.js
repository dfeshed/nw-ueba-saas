import classic from 'ember-classic-decorator';
import { tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { setSelectedParentDirectory, getSubDirectories } from 'investigate-hosts/actions/data-creators/downloads';

const stateToComputed = (state) => ({
  openDirectories: state.endpoint.hostDownloads.mft.mftDirectory.openDirectories,
  fullPathName: state.endpoint.hostDownloads.mft.mftDirectory.fullPathName,
  directoryName: state.endpoint.hostDownloads.mft.mftDirectory.name
});

const dispatchToActions = {
  setSelectedParentDirectory,
  getSubDirectories
};

@classic
@tagName('')
class SubdirectoryAccess extends Component {
  close = true;
  isLoading = false;

  @computed('close', 'openDirectories', 'data')
  get displaySubdirectory() {

    let arrowDirection = 'arrow-down-12';

    if (this.openDirectories.includes(this.data.recordNumber)) {
      this.close = false;
      this.set('close', this.close);
    } else {
      arrowDirection = this.close ? 'arrow-right-12' : 'arrow-down-12';
    }

    return { arrowDirection, close: this.close };
  }

  @action
  toggleSubdirectories() {
    const { ancestors, recordNumber, children } = this.get('data');
    const fullPathName = this.get('fullPathName');
    const name = this.get('directoryName');
    // max number of folders is 65000 and fetch only directories is true for a fresh subdirectory fetch.
    let isDirectories = false;
    const inUse = true;
    if (this.get('close')) {
      if (!children) {
        this.set('isLoading', true);
        this.send('getSubDirectories');
        isDirectories = true;
      }
      this.send('setSelectedParentDirectory', { selectedParentDirectory: { recordNumber, ancestors, close: false }, pageSize: 65000, isDirectories, inUse, fullPathName, name });
    } else {
      this.send('setSelectedParentDirectory', { selectedParentDirectory: { recordNumber, ancestors: [], close: true }, pageSize: 100, isDirectories, inUse, fullPathName, name });
    }
    this.toggleProperty('close');
  }
}

export default connect(stateToComputed, dispatchToActions)(SubdirectoryAccess);