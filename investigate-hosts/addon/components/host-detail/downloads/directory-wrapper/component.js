import Component from '@ember/component';
import { connect } from 'ember-redux';
import { getSubDirectories, setSelectDirectoryForDetails } from 'investigate-hosts/actions/data-creators/downloads';

const stateToComputed = (state) => ({
  mftDirectory: state.endpoint.hostDownloads.mftDirectory.subDirectories,
  fileSource: state.endpoint.hostDownloads.mftDirectory.fileSource,
  selectedMftFile: state.endpoint.hostDownloads.downloads.selectedMftFile
});

const dispatchToActions = {
  getSubDirectories,
  setSelectDirectoryForDetails
};

const DirectoryWrapper = Component.extend({
  tagName: 'section',
  classNames: ['directory-wrapper'],

  actions: {
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
});

export default connect(stateToComputed, dispatchToActions)(DirectoryWrapper);