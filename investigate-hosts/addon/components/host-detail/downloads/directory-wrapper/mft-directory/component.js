import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { setSelectDirectoryForDetails } from 'investigate-hosts/actions/data-creators/downloads';

const stateToComputed = (state) => ({
  selectedDirectoryForDetails: state.endpoint.hostDownloads.mftDirectory.selectedDirectoryForDetails
});

const dispatchToActions = {
  setSelectDirectoryForDetails
};

const MFTDirectory = Component.extend({
  classNames: ['mft-directory'],
  classNameBindings: ['selectedDirectory'],

  @computed('selectedDirectoryForDetails', 'data')
  selectedDirectory(selectedDirectoryForDetails, { recordNumber }) {
    return recordNumber === selectedDirectoryForDetails;
  },

  actions: {
    fetchSubdirectoriesAndFiles(data) {
      // placeholder, needs to also make a backend call
      const { recordNumber } = data;
      this.send('setSelectDirectoryForDetails', recordNumber);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(MFTDirectory);