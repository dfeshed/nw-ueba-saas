import Component from 'ember-component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import injectService from 'ember-service/inject';
import moment from 'moment';
import { isJsonExportCompleted, isSnapshotsAvailable } from 'investigate-hosts/reducers/details/overview/selectors';
import { toggleInitiateScanModal } from 'investigate-hosts/actions/ui-state-creators';

import {
  exportFileContext,
  setTransition,
  initializeAgentDetails
} from 'investigate-hosts/actions/data-creators/details';


const stateToComputed = (state) => ({
  scanTime: state.endpoint.detailsInput.scanTime,
  host: state.endpoint.overview.hostDetails,
  agentId: state.endpoint.detailsInput.agentId,
  snapShots: state.endpoint.detailsInput.snapShots,
  downloadId: state.endpoint.overview.downloadId,
  isExportDisabled: !isSnapshotsAvailable(state),
  isJsonExportCompleted: isJsonExportCompleted(state)
});

const dispatchToActions = {
  setTransition,
  initializeAgentDetails,
  exportFileContext,
  toggleInitiateScanModal
};

const ActionBar = Component.extend({

  tagName: 'hbox',

  classNames: 'actionbar controls center flexi-fit',

  eventBus: injectService(),

  i18n: injectService(),

  flashMessage: injectService(),

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
        window.location = url;
      } else {
        this.get('flashMessage').showErrorMessage(i18n.t('investigateHosts.hosts.moreActions.notAnEcatAgent'));
      }
    },
    export() {
      const scanTime = this.get('scanTime');
      const agentId = this.get('agentId');
      this.send('exportFileContext', { agentId, scanTime, categories: ['AUTORUNS'] });
    }
  },

  @computed('downloadId')
  downloadLink(downloadId) {
    if (downloadId) {
      return `${location.origin}/endpoint/file/download/${downloadId}`;
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ActionBar);
