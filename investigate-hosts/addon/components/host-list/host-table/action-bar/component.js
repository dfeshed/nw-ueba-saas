import Component from 'ember-component';
import { connect } from 'ember-redux';
import injectService from 'ember-service/inject';
import { noHostsSelected } from 'investigate-hosts/reducers/hosts/selectors';
import computed from 'ember-computed-decorators';
import { toggleInitiateScanModal, toggleCancelScanModal, toggleDeleteHostsModal } from 'investigate-hosts/actions/ui-state-creators';

const MAX_HOST = 100;

const stateToComputed = (state) => ({
  selectedHostList: state.endpoint.machines.selectedHostList,
  isDisabled: noHostsSelected(state)
});

const dispatchToActions = {
  toggleInitiateScanModal,
  toggleCancelScanModal,
  toggleDeleteHostsModal
};

const ActionBar = Component.extend({

  tagName: 'section',

  classNames: 'host-table__toolbar',

  flashMessage: injectService(),

  panelId: 'initScan',

  @computed('selectedHostList')
  tooManyHostsSelected(selectedHostList) {
    return selectedHostList.length > MAX_HOST;
  },

  @computed('tooManyHostsSelected')
  warningClass(tooManyHostsSelected) {
    return tooManyHostsSelected ? 'danger' : 'standard';
  },

  actions: {

    openInitiateScanModal() {
      if (this.get('tooManyHostsSelected')) {
        return;
      }
      this.send('toggleInitiateScanModal');
    },
    openCancelScanModal() {
      if (this.get('tooManyHostsSelected')) {
        return;
      }
      this.send('toggleCancelScanModal');
    },

    /**
     * Opens the selected machines in the thick client
     * @param keyword
     * @param machines
     * @public
     */
    openThickClient() {
      const selectedHostList = this.get('selectedHostList');

      let url = 'ecatui:///machines/';

      if (selectedHostList.length <= 0) {
        this.get('flashMessage').showErrorMessage('investigateHosts.hosts.moreActions.openInErrorMessage');
        return;
      }
      url += selectedHostList.join(':');
      window.location = url;
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(ActionBar);
