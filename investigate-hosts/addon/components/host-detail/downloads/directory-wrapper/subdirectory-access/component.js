import Component from '@ember/component';
import computed from 'ember-computed-decorators';
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

const SubdirectoryAccess = Component.extend({
  tagName: '',
  close: true,
  isLoading: false,

  @computed('close', 'openDirectories', 'data')
  displaySubdirectory: {
    get() {
      const { openDirectories, data } = this.getProperties('openDirectories', 'data');
      let isClosed = this.get('close');
      let arrowDirection = 'arrow-down-12';

      if (openDirectories.includes(data.recordNumber)) {
        isClosed = false;
      } else {
        arrowDirection = isClosed ? 'arrow-right-12' : 'arrow-down-12';
      }

      return { arrowDirection, isClosed };
    },
    set(key, value) {
      return value;
    }
  },

  actions: {
    toggleSubdirectories() {
      const { ancestors, recordNumber, children } = this.get('data');
      const fullPathName = this.get('fullPathName');
      const name = this.get('directoryName');
      const openDirectories = this.get('openDirectories');
      // max number of folders is 65000 and fetch only directories is true for a fresh subdirectory fetch.
      let isDirectories = false;
      const inUse = true;

      if (!this.get('close') || openDirectories.includes(recordNumber)) {
        this.send('setSelectedParentDirectory', { selectedParentDirectory: { recordNumber, ancestors: [], close: true }, pageSize: 100, isDirectories, inUse, fullPathName, name });
        this.set('close', true);
      } else {
        if (!children) {
          this.set('isLoading', true);
          this.send('getSubDirectories');
          isDirectories = true;
        }
        this.send('setSelectedParentDirectory', { selectedParentDirectory: { recordNumber, ancestors, close: false }, pageSize: 65000, isDirectories, inUse, fullPathName, name });
        this.set('close', false);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(SubdirectoryAccess);