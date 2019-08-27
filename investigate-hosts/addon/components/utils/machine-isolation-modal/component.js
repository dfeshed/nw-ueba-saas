import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import config from './config';

const dispatchToActions = {};


const MachineIsolationModal = Component.extend({
  config,

  @computed('config', 'selectedModal')
  modalConfig: {
    get() {
      const config = this.get('config');
      const selectedModal = this.get('selectedModal');
      return config[selectedModal];
    },
    set(value) {
      return value;
    }
  }
});

export default connect(undefined, dispatchToActions)(MachineIsolationModal);