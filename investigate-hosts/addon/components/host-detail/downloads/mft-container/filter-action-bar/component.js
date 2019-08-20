import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { toggleMftView, mftFilterVisible } from 'investigate-hosts/actions/data-creators/downloads';
import computed, { alias } from 'ember-computed-decorators';
import { connect } from 'ember-redux';

const stateToComputed = (state) => ({
  selectedMftFile: state.endpoint.hostDownloads.downloads.selectedMftFile,
  serverId: state.endpointQuery.serverId,
  focusedHost: state.endpoint.detailsInput.agentId,
  fileSource: state.endpoint.hostDownloads.mft.mftDirectory.fileSource,
  isOpenFilter: state.endpoint.hostDownloads.mft.mftDirectory.showFilter
});

const dispatchToActions = {
  toggleMftView,
  mftFilterVisible
};

const filterActionBar = Component.extend({
  tagName: 'box',
  classNames: ['filter-action-bar'],
  accessControl: service(),
  @alias('focusedHost')
  machineId: null,
  @computed('fileSource')
  isDisableFilter() {
    return !this.fileSource;
  },
  actions: {
    filterAction() {
      this.send('mftFilterVisible', true);
      this.openFilterPanel();
    }

  }
});
export default connect(stateToComputed, dispatchToActions)(filterActionBar);
