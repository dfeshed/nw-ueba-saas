import Component from 'ember-component';
import { connect } from 'ember-redux';
import { areSomeScanning, hostListForScanning, hasMachineId } from 'investigate-hosts/reducers/hosts/selectors';
import service from 'ember-service/inject';
import { initializeHostsPreferences } from 'investigate-hosts/actions/data-creators/host';

const stateToComputed = (state) => ({
  selectedHostList: hostListForScanning(state),
  hasMachineId: hasMachineId(state),
  showInitiateScanModal: state.endpoint.visuals.showInitiateScanModal,
  showCancelScanModal: state.endpoint.visuals.showCancelScanModal,
  areSomeScanning: areSomeScanning(state),
  showDeleteHostsModal: state.endpoint.visuals.showDeleteHostsModal
});

const dispatchToActions = {
  initializeHostsPreferences
};

const Container = Component.extend({

  eventBus: service(),

  tagName: 'hbox',

  classNames: 'host-engine host-container',

  classNameBindings: ['hasMachineId'],

  init() {
    this._super(...arguments);
    this.send('initializeHostsPreferences');
  },

  click(event) {
    // this trigger is required to open start/stop scan modal window
    this.get('eventBus').trigger('rsa-application-click', event.target);
  }

});

export default connect(stateToComputed, dispatchToActions)(Container);
