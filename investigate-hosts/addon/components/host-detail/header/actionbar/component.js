import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import moment from 'moment';
import {
  isJsonExportCompleted,
  isSnapshotsAvailable,
  downloadLink,
  hostName
} from 'investigate-hosts/reducers/details/overview/selectors';

import {
  exportFileContext,
  setTransition,
  initializeAgentDetails
} from 'investigate-hosts/actions/data-creators/details';


const stateToComputed = (state) => ({
  hostName: hostName(state),
  scanTime: state.endpoint.detailsInput.scanTime,
  host: state.endpoint.overview.hostDetails,
  agentId: state.endpoint.detailsInput.agentId,
  snapShots: state.endpoint.detailsInput.snapShots,
  downloadLink: downloadLink(state),
  isExportDisabled: !isSnapshotsAvailable(state),
  isJsonExportCompleted: isJsonExportCompleted(state)
});

const dispatchToActions = {
  setTransition,
  initializeAgentDetails,
  exportFileContext
};

const ActionBar = Component.extend({

  tagName: 'hbox',

  classNames: 'actionbar controls flexi-fit',

  eventBus: service(),

  i18n: service(),

  flashMessage: service(),

  actions: {
    setSelect(option) {
      const oldTime = this.get('scanTime');
      this.send('initializeAgentDetails', { agentId: this.get('agentId'), scanTime: option });

      if (moment(oldTime).unix() > moment(option).unix()) {
        this.send('setTransition', 'toUp');
      } else {
        this.send('setTransition', 'toDown');
      }
    },
    openInAction() {
      const host = this.get('host');
      const { machine: { agentVersion } } = host;
      const url = `ecatui:///machines/${host.id}`;
      const i18n = this.get('i18n');
      if (agentVersion.startsWith('4.4')) {
        window.open(url);
      } else {
        this.get('flashMessage').showErrorMessage(i18n.t('investigateHosts.hosts.moreActions.notAnEcatAgent'));
      }
    },
    export() {
      const scanTime = this.get('scanTime');
      const agentId = this.get('agentId');
      this.send('exportFileContext', { agentId, scanTime, categories: ['AUTORUNS'] });
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(ActionBar);
