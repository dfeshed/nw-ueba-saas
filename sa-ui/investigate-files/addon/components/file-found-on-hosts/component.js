import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { serviceList, hostList, hostListCount } from 'investigate-files/reducers/file-list/selectors';
import { getAllServices, fetchAgentId } from 'investigate-files/actions/data-creators';
import { serviceId } from 'investigate-shared/selectors/investigate/selectors';

const stateToComputed = (state) => ({
  serviceList: serviceList(state),
  itemList: state.files.fileList.selectedFileList,
  items: hostList(state),
  serverId: state.endpointQuery.serverId,
  isHostListLoading: state.files.fileList.fetchMetaValueLoading,
  serviceId: serviceId(state),
  hostListCount: hostListCount(state)
});

const dispatchToActions = {
  getAllServices,
  fetchAgentId
};

@classic
@tagName('')
@classNames('file-found-on-hosts')
class fileHosts extends Component {
  @service
  pivot;

  @computed('hostListCount')
  get countLabelKey() {
    return 100 < this.hostListCount ? 'investigateFiles.message.listOfHostMessage' : '';
  }

  init() {
    super.init(...arguments);
  }

  @action
  pivotToInvestigate(item) {
    this.get('pivot').pivotToInvestigate('machineIdentity.machineName', { machineIdentity: { machineName: item } });
  }

  @action
  openHost(item) {
    const serverId = this.get('serverId');
    window.open(`${window.location.origin}/investigate/hosts/${item.agentId.toUpperCase()}/OVERVIEW?sid=${serverId}`);
  }
}

export default connect(stateToComputed, dispatchToActions)(fileHosts);
