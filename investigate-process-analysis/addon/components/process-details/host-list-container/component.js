import Component from '@ember/component';
import { listOfHostNames } from 'investigate-process-analysis/reducers/host-context/selectors';
import { fetchAgentId } from 'investigate-process-analysis/actions/creators/events-creators';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';

const stateToComputed = (state) => ({
  listOfHostNames: listOfHostNames(state)
});

const dispatchToActions = {
  fetchAgentId
};


const HostListContainer = Component.extend({

  classNames: ['host-list-container'],

  pivot: service(),

  actions: {
    onHostNameClick(target, item) {
      if ('HOST_NAME' === target) {
        this.send('fetchAgentId', item, ([data]) => {
          if (!this.get('isDestroyed') && !this.get('isDestroying')) {
            const serverId = this.get('serverId');
            window.open(`${window.location.origin}/investigate/hosts/${data.value.toUpperCase()}?machineId=${data.value.toUpperCase()}&tabName=OVERVIEW&sid=${serverId}`);
          }
        });
      } else if ('PIVOT_ICON' === target) {
        // TODO : will add it another PR
      }
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(HostListContainer);
