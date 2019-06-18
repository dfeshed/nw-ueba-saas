import Component from '@ember/component';
import { listOfHostNames } from 'investigate-process-analysis/reducers/host-context/selectors';
import { fetchAgentId } from 'investigate-process-analysis/actions/creators/events-creators';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';

const stateToComputed = (state) => ({
  listOfHostNames: listOfHostNames(state),
  isLoading: state.processAnalysis.hostContext.loading,
  serverId: state.processAnalysis.processTree.selectedServerId
});

const dispatchToActions = {
  fetchAgentId
};


const HostListContainer = Component.extend({

  tagName: 'vbox',

  classNames: ['host-list-container'],

  pivot: service(),

  actions: {
    onHostNameClick(target, machineName) {
      if ('HOST_NAME' === target) {
        this.send('fetchAgentId', machineName, ([data]) => {
          if (!this.get('isDestroyed') && !this.get('isDestroying')) {
            const serverId = this.get('serverId');
            window.open(`${window.location.origin}/investigate/hosts/${data.value.toUpperCase()}?machineId=${data.value.toUpperCase()}&tabName=OVERVIEW&sid=${serverId}`);
          }
        });
      } else if ('PIVOT_ICON' === target) {
        this.get('pivot').pivotToInvestigate('machineIdentity.machineName', { machineIdentity: { machineName } });
      }
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(HostListContainer);
