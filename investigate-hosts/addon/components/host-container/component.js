import Component from '@ember/component';
import { connect } from 'ember-redux';
import { hostListForScanning, hasMachineId } from 'investigate-hosts/reducers/hosts/selectors';
import { inject as service } from '@ember/service';

const stateToComputed = (state) => ({
  selectedHostList: hostListForScanning(state),
  hasMachineId: hasMachineId(state)
});

const Container = Component.extend({

  eventBus: service(),

  tagName: 'hbox',

  classNames: 'host-engine host-container',

  classNameBindings: ['hasMachineId'],

  init() {
    this._super(...arguments);
  },

  click(event) {
    // this trigger is required to open start/stop scan modal window
    this.get('eventBus').trigger('rsa-application-click', event.target);
  }

});

export default connect(stateToComputed)(Container);
