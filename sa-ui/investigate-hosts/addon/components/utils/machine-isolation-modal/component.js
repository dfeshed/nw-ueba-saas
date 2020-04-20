import classic from 'ember-classic-decorator';
import { computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import config from './config';

const dispatchToActions = {};


@classic
class MachineIsolationModal extends Component {
  config = config;

  @computed('config', 'selectedModal')
  get modalConfig() {
    const config = this.get('config');
    const selectedModal = this.get('selectedModal');
    return config[selectedModal];
  }

  set modalConfig(value) {
    return value;
  }
}

export default connect(undefined, dispatchToActions)(MachineIsolationModal);