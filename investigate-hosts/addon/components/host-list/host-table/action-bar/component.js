import Component from 'ember-component';
import { connect } from 'ember-redux';
import injectService from 'ember-service/inject';
import _ from 'lodash';
import {
  noHostsSelected,
  tooManyHostsSelected,
  warningClass,
  allAreEcatAgents } from 'investigate-hosts/reducers/hosts/selectors';
import {
  toggleInitiateScanModal,
  toggleCancelScanModal,
  toggleDeleteHostsModal } from 'investigate-hosts/actions/ui-state-creators';

const stateToComputed = (state) => ({
  totalItems: state.endpoint.machines.totalItems,
  noHostsSelected: noHostsSelected(state),
  tooManyHostsSelected: tooManyHostsSelected(state),
  warningClass: warningClass(state),
  allAreEcatAgents: allAreEcatAgents(state),
  selectedHostList: state.endpoint.machines.selectedHostList
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

  i18n: injectService(),

  panelId: 'initScan',

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
      const selectedHostList = this.get('selectedHostList'); // [{id, version}]
      const selectedHostIds = _.map(selectedHostList, 'id');
      const i18n = this.get('i18n');
      let url = 'ecatui:///machines/';

      if (selectedHostList.length <= 0) {
        this.get('flashMessage').showErrorMessage(i18n.t('investigateHosts.hosts.moreActions.openInErrorMessage'));
        return;
      } else if (selectedHostList.some((host) => !host.version.startsWith('4.4'))) {
        this.get('flashMessage').showErrorMessage(i18n.t('investigateHosts.hosts.moreActions.notAnEcatAgent'));
        return;
      }
      url += selectedHostIds.join(':');
      window.location = url;
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(ActionBar);
