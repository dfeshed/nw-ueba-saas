import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { setSelectDirectoryForDetails, getSubDirectories } from 'investigate-hosts/actions/data-creators/downloads';

const stateToComputed = (state) => ({
  selectedDirectoryForDetails: state.endpoint.hostDownloads.mft.mftDirectory.selectedDirectoryForDetails
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
      const { recordNumber } = data;
      this.send('setSelectDirectoryForDetails', {
        selectedDirectoryForDetails: recordNumber,
        fileSource: 'drive',
        pageSize: 100,
        isDirectories: false,
        inUse: true });
      this.send('getSubDirectories');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(MFTDirectory);