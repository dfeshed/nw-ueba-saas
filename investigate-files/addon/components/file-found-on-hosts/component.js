import Component from '@ember/component';
import { connect } from 'ember-redux';
import { serviceList, hostList, hostListCount } from 'investigate-files/reducers/file-list/selectors';
import { getAllServices, fetchAgentId } from 'investigate-files/actions/data-creators';
import { serviceId } from 'investigate-shared/selectors/investigate/selectors';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

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

const fileHosts = Component.extend({

  tagName: '',

  classNames: ['file-found-on-hosts'],

  pivot: service(),

  @computed('hostListCount')
  countLabelKey(count) {
    return 100 < count ? 'investigateFiles.message.listOfHostMessage' : '';
  },

  init() {
    this._super(arguments);
  },

  actions: {

    pivotToInvestigate(item) {
      this.get('pivot').pivotToInvestigate('machineIdentity.machineName', { machineIdentity: { machineName: item } });
    },

    openHost(item) {
      const serverId = this.get('serverId');
      window.open(`${window.location.origin}/investigate/hosts/${item.agentId.toUpperCase()}/OVERVIEW?sid=${serverId}`);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(fileHosts);
