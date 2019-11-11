import Component from '@ember/component';
import { listOfHostNames } from 'investigate-process-analysis/reducers/host-context/selectors';
import { fetchAgentId } from 'investigate-process-analysis/actions/creators/events-creators';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import { processHostCount } from 'investigate-process-analysis/reducers/process-properties/selectors';

const stateToComputed = (state) => ({
  listOfHostNames: listOfHostNames(state),
  isLoading: state.processAnalysis.hostContext.loading,
  serverId: state.processAnalysis.processTree.selectedServerId,
  processHostCount: processHostCount(state)
});

const dispatchToActions = {
  fetchAgentId
};


const HostListContainer = Component.extend({

  tagName: 'vbox',

  classNames: ['host-list-container'],

  pivot: service(),

  actions: {
    onHostNameClick(target, item) {
      if ('HOST_NAME' === target) {
        const serverId = this.get('serverId');
        window.open(`${window.location.origin}/investigate/hosts/${item.agentId.toUpperCase()}/OVERVIEW?sid=${serverId}`);
      } else if ('PIVOT_ICON' === target) {
        this.get('pivot').pivotToInvestigate('machineIdentity.machineName', { machineIdentity: { machineName: item } });
      }
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(HostListContainer);
