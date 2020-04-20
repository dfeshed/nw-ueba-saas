import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import { alias } from '@ember/object/computed';
import Component from '@ember/component';
import { toggleMftView, mftFilterVisible } from 'investigate-hosts/actions/data-creators/downloads';
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

@classic
@tagName('box')
@classNames('filter-action-bar')
class filterActionBar extends Component {
  @service
  accessControl;

  @alias('focusedHost')
  machineId;

  @computed('fileSource')
  get isDisableFilter() {
    return !this.fileSource;
  }

  @action
  filterAction() {
    this.send('mftFilterVisible', true);
    this.openFilterPanel();
  }
}

export default connect(stateToComputed, dispatchToActions)(filterActionBar);
