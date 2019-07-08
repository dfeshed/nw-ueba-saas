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
      const { ancestors, recordNumber, children, mftId } = this.get('data');
      const isDirectories = true;
      const pageSize = 65000;
      if (this.get('close')) {
        if (!children) {
          this.set('isLoading', true);
          // max number of folders is 65000 and fetch only directories is true
          this.send('getSubDirectories', mftId, recordNumber, pageSize, isDirectories);
        }

        this.send('setSeletedParentDirectory', { recordNumber, ancestors, close: false });
      } else {
        this.send('setSeletedParentDirectory', { recordNumber, ancestors: [], close: true });
      }
      this.toggleProperty('close');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(SubdirectoryAccess);