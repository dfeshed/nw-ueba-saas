import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { isEmpty } from '@ember/utils';
import { connect } from 'ember-redux';
import _ from 'lodash';

import { validateConfig } from 'investigate-shared/utils/validation-utils';
import { failure } from 'investigate-shared/utils/flash-messages';


import {
  saveRARConfig,
  resetRARConfig,
  saveUIState
} from '../../../actions/data-creators';

const stateToComputed = (state) => ({
  // Default/Saved RAR config.
  configData: _.cloneDeep(state.rar.defaultRARConfig)
});

const dispatchToActions = {
  saveRARConfig,
  resetRARConfig,
  saveUIState
};

const RARConfig = Component.extend({
  layout,

  classNames: ['rar-config', 'rar-configuration'],

  @computed('configData.rarConfig.esh',
    'configData.rarConfig.httpsPort',
    'configData.rarConfig.httpsBeaconIntervalInSeconds',
    'configData.rarConfig.address')
  isDisabled(esh, httpsPort, httpsBeaconIntervalInSeconds, address) {
    return isEmpty(esh) || isEmpty(httpsPort) || isEmpty(httpsBeaconIntervalInSeconds) || isEmpty(address);
  },

  resetErrorProperties() {
    this.setProperties({
      isHostError: false,
      isPortError: false,
      isBeaconError: false,
      isServerError: false,
      invalidPortMessage: null,
      invalidServerMessage: null,
      invalidHostNameMessage: null,
      invalidBeaconIntervalMessage: null
    });
  },

  _getCallbackFunction() {
    return {
      onSuccess: () => {
        this.resetErrorProperties();
      },
      onFailure(message) {
        failure(message, null, false);
      }
    };
  },

  actions: {

    generateAgent() {
      this.resetErrorProperties();

      const error = validateConfig(this.get('configData.rarConfig'));
      this.setProperties(error);

      if (!error) {
        this.send('saveUIState', this.get('configData'));
        this.send('saveRARConfig', { ...this.get('configData.rarConfig') }, this._getCallbackFunction());
      }
    },

    reset() {
      this.resetErrorProperties();
      this.send('resetRARConfig');
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(RARConfig);