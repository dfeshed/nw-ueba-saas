import Component from 'ember-component';
import layout from './template';
import computed, { alias } from 'ember-computed-decorators';
import { isEmpty } from 'ember-utils';
import moment from 'moment';
import { connect } from 'ember-redux';

import {
  setConfig,
  resetForm
} from '../../actions/data-creators';


const stateToComputed = ({ packager }) => ({
  // Already saved agent information
  configData: { ...packager.defaultPackagerConfig },

  // Flag to indicate config is currently updating or not
  isUpdating: packager.updating
});

const dispatchToActions = {
  setConfig,
  resetForm
};


const formComponent = Component.extend({
  layout,

  classNames: ['packager-form'],

  minDate: new Date(),

  @alias('configData.autoUninstall')
  autoUninstall: null,

  @alias('configData.forceOverwrite')
  forceOverwrite: false,

  @computed('configData.server', 'configData.port', 'isUpdating')
  isDisabled(server, port, isUpdating) {
    return isEmpty(server) || isEmpty(port) || isUpdating;
  },

  actions: {

    generateAgent() {
      const { autoUninstall } = this.get('configData');
      if (!isEmpty(autoUninstall[0])) {
        this.set('configData.autoUninstall', moment(autoUninstall[0]).toISOString());
      }
      this.send('setConfig', this.get('configData'));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(formComponent);