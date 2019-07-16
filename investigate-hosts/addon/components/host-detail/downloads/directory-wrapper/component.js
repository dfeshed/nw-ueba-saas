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
      const mftId = this.get('selectedMftFile');
      const isDirectories = false;
      const pageSize = 100;

      if (selectedAction === 'allFiles') {
        this.send('setSelectDirectoryForDetails', 0, 'allFiles');
        this.send('getSubDirectories', mftId, -1, pageSize, isDirectories);
      } else {
        this.send('setSelectDirectoryForDetails', 0, 'deletedFiles');
        const inUse = false;
        this.send('getSubDirectories', mftId, -1, pageSize, isDirectories, inUse);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DirectoryWrapper);