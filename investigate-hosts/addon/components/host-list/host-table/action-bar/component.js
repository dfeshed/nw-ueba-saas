import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import _ from 'lodash';
import {
  noHostsSelected,
  hostCountForDisplay,
  allAreEcatAgents } from 'investigate-hosts/reducers/hosts/selectors';
import { toggleDeleteHostsModal } from 'investigate-hosts/actions/ui-state-creators';

import { deleteHosts } from 'investigate-hosts/actions/data-creators/host';


const stateToComputed = (state) => ({
  totalItems: hostCountForDisplay(state),
  noHostsSelected: noHostsSelected(state),
  allAreEcatAgents: allAreEcatAgents(state),
  selectedHostList: state.endpoint.machines.selectedHostList
});

const dispatchToActions = {
  toggleDeleteHostsModal,
  deleteHosts
};

const ActionBar = Component.extend({

  tagName: 'section',

  classNames: 'host-table__toolbar',

  flashMessage: service(),

  i18n: service(),

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
