import Component from 'ember-component';
import { connect } from 'ember-redux';
import injectService from 'ember-service/inject';
import _ from 'lodash';
import {
  noHostsSelected,
  hostCountForDisplay,
  allAreEcatAgents } from 'investigate-hosts/reducers/hosts/selectors';
import {
  toggleCancelScanModal,
  toggleDeleteHostsModal } from 'investigate-hosts/actions/ui-state-creators';

import { deleteHosts } from 'investigate-hosts/actions/data-creators/host';


const stateToComputed = (state) => ({
  totalItems: hostCountForDisplay(state),
  noHostsSelected: noHostsSelected(state),
  allAreEcatAgents: allAreEcatAgents(state),
  selectedHostList: state.endpoint.machines.selectedHostList
});

const dispatchToActions = {
  toggleCancelScanModal,
  toggleDeleteHostsModal,
  deleteHosts
};

const ActionBar = Component.extend({

  tagName: 'section',

  classNames: 'host-table__toolbar',

  flashMessage: injectService(),

  i18n: injectService(),

  actions: {
    handleDeleteHosts() {
      const callBackOptions = {
        onSuccess: () => {
          this.get('flashMessage').showFlashMessage('investigateHosts.hosts.deleteHosts.success');
        },
        onFailure: ({ meta: message }) => this.get('flashMessage').showErrorMessage(message.message)
      };
      this.send('deleteHosts', callBackOptions);
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
