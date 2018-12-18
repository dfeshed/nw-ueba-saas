import Component from '@ember/component';
import { connect } from 'ember-redux';
import { serviceList, hostList } from 'investigate-files/reducers/file-list/selectors';
import { getAllServices, fetchAgentId } from 'investigate-files/actions/data-creators';
import { serviceId } from 'investigate-shared/selectors/investigate/selectors';

const stateToComputed = (state) => ({
  serviceList: serviceList(state),
  itemList: state.files.fileList.selectedFileList,
  items: hostList(state),
  serverId: state.endpointQuery.serverId,
  isHostListLoading: state.files.fileList.fetchMetaValueLoading,
  serviceId: serviceId(state)
});

const dispatchToActions = {
  getAllServices,
  fetchAgentId
};

const fileHosts = Component.extend({

  tagName: '',

  classNames: ['file-found-on-hosts'],

  itemList: [],

  showOnlyIcons: true,

  metaName: 'machine.machineName',

  init() {
    this._super(arguments);
  },

  actions: {

    pivotToInvestigate() {
      this.send('getAllServices');
      this.set('showServiceModal', true);
    },

    openHost(item) {
      this.send('fetchAgentId', item, ([data]) => {
        if (!this.get('isDestroyed') && !this.get('isDestroying')) {
          const serverId = this.get('serverId');
          window.open(`${window.location.origin}/investigate/hosts?machineId=${data.value.toUpperCase()}&tabName=OVERVIEW&sid=${serverId}`);
        }
      });
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(fileHosts);
