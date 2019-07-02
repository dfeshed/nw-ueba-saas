import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { toggleMftView } from 'investigate-hosts/actions/data-creators/downloads';
import { alias } from 'ember-computed-decorators';
import { connect } from 'ember-redux';

const stateToComputed = (state) => ({
  selectedMftFile: state.endpoint.hostDownloads.downloads.selectedMftFile,
  serverId: state.endpointQuery.serverId,
  focusedHost: state.endpoint.detailsInput.agentId,
  isMFTView: state.endpoint.hostDownloads.downloads.isShowMFTView
});

const dispatchToActions = {
  toggleMftView
};

const filterActionBar = Component.extend({
  tagName: 'box',
  classNames: ['filter-action-bar'],
  accessControl: service(),
  @alias('focusedHost')
  machineId: null

});
export default connect(stateToComputed, dispatchToActions)(filterActionBar);