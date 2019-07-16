import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { setSelectDirectoryForDetails, getSubDirectories } from 'investigate-hosts/actions/data-creators/downloads';

const stateToComputed = (state) => ({
  selectedDirectoryForDetails: state.endpoint.hostDownloads.mftDirectory.selectedDirectoryForDetails
});

const dispatchToActions = {
  setSelectDirectoryForDetails,
  getSubDirectories
};

const MFTDirectory = Component.extend({
  classNames: ['mft-directory'],
  classNameBindings: ['selectedDirectory'],

  @computed('data')
  isMainDrive({ parentDirectory }) {
    return parentDirectory === 0;
  },

  @computed('selectedDirectoryForDetails', 'data')
  selectedDirectory(selectedDirectoryForDetails, { recordNumber }) {
    return recordNumber === selectedDirectoryForDetails;
  },

  actions: {
    fetchSubdirectoriesAndFiles(data) {
      const { recordNumber, mftId } = data;
      const isDirectories = false;
      const pageSize = 100;
      this.send('setSelectDirectoryForDetails', recordNumber, 'drive');
      this.send('getSubDirectories', mftId, recordNumber, pageSize, isDirectories);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(MFTDirectory);