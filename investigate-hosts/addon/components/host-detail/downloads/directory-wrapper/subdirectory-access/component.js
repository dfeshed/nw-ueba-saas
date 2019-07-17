import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { setSeletedParentDirectory, getSubDirectories } from 'investigate-hosts/actions/data-creators/downloads';

const stateToComputed = (state) => ({
  openDirectories: state.endpoint.hostDownloads.mftDirectory.openDirectories
});

const dispatchToActions = {
  setSeletedParentDirectory,
  getSubDirectories
};

const SubdirectoryAccess = Component.extend({
  tagName: '',
  close: true,
  isLoading: false,

  @computed('close', 'openDirectories', 'data')
  displaySubdirectory(close, openDirectories, data) {

    let arrowDirection = 'arrow-down-12';

    if (openDirectories.includes(data.recordNumber)) {
      close = false;
      this.set('close', close);
    } else {
      arrowDirection = close ? 'arrow-right-12' : 'arrow-down-12';
    }

    return { arrowDirection, close };
  },
  actions: {
    toggleSubdirectories() {
      const { ancestors, recordNumber, children } = this.get('data');
      // max number of folders is 65000 and fetch only directories is true
      const isDirectories = true;
      const pageSize = 65000;
      const inUse = true;
      if (this.get('close')) {
        this.send('setSeletedParentDirectory', { selectedParentDirectory: { recordNumber, ancestors, close: false }, pageSize, isDirectories, inUse });
        if (!children) {
          this.set('isLoading', true);
          this.send('getSubDirectories');
        }

      } else {
        this.send('setSeletedParentDirectory', { selectedParentDirectory: { recordNumber, ancestors: [], close: true } });
      }
      this.toggleProperty('close');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(SubdirectoryAccess);