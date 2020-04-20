import classic from 'ember-classic-decorator';
import { classNames, layout as templateLayout } from '@ember-decorators/component';
import Component from '@ember/component';
import layout from './template';
import { computed, action } from '@ember/object';
import { alias } from '@ember/object/computed';
import { isEmpty } from '@ember/utils';
import { connect } from 'ember-redux';
import _ from 'lodash';
import { inject as service } from '@ember/service';

import { validateConfig } from 'investigate-shared/utils/validation-utils';
import { success, failure } from 'investigate-shared/utils/flash-messages';


import {
  saveRARConfig,
  resetRARConfig,
  saveUIState,
  testRARConfig,
  saveRarStatus
} from '../../../actions/data-creators';

const stateToComputed = ({ rar }) => ({
  // Default/Saved RAR config.
  configData: _.cloneDeep(rar.defaultRARConfig),
  isTestingConfig: rar.testConfigLoader,
  isEnabled: rar.isEnabled
});

const dispatchToActions = {
  saveRARConfig,
  resetRARConfig,
  saveUIState,
  testRARConfig,
  saveRarStatus
};

@classic
@templateLayout(layout)
@classNames('rar-config', 'rar-configuration')
class RARConfig extends Component {

  @service
  accessControl;

  @computed('configData.rarConfig.esh',
    'configData.rarConfig.httpsPort',
    'configData.rarConfig.httpsBeaconIntervalInSeconds',
    'configData.rarConfig.address')
  get isSaveDisabled() {
    const rarConfig = this.get('configData.rarConfig') || { rarConfig: {} };
    const { esh, httpsPort, httpsBeaconIntervalInSeconds, address } = rarConfig;
    return isEmpty(esh) || isEmpty(httpsPort) || isEmpty(httpsBeaconIntervalInSeconds) || isEmpty(address);
  }

  @computed('isEnabled')
  get isRarDisabled() {
    const isEnabled = this.get('isEnabled');
    return !isEnabled;
  }

  @alias('isEnabled')
  isRarEnabled = false;

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
  }

  _getCallbackFunction(successMsg, failureMsg) {
    return {
      onSuccess: () => {
        success(`endpointRAR.rarConfig.${successMsg}`);
        this.resetErrorProperties();
      },
      onFailure(message = failureMsg) {
        failure(`endpointRAR.rarConfig.${message}`);
      }
    };
  }

  @action
  toggleIsChecked() {
    const toggleRarStatus = !this.get('isRarEnabled');
    const successMessage = toggleRarStatus ? 'enableRar' : 'disableRar';
    if (this.get('accessControl.hasEndpointRarPermission')) {
      this.send('saveRarStatus', toggleRarStatus, this._getCallbackFunction(successMessage, 'failureMessage'));
    } else {
      failure('endpointRAR.rarConfig.permissionDeniedForEnable');
    }
  }

  @action
  generateAgent(isTestConfig = false) {
    this.resetErrorProperties();

    const error = validateConfig(this.get('configData.rarConfig'));
    this.setProperties(error);

    if (!error) {
      this.send('saveUIState', this.get('configData'));
      if (isTestConfig) {
        this.send('testRARConfig', { ...this.get('configData.rarConfig') }, this._getCallbackFunction('testConfigSuccess', 'testConfigFailure'));
      } else {
        if (this.get('accessControl.hasEndpointRarPermission')) {
          this.set('configData.rarConfig.enabled', true);
          this.send('saveRARConfig', { ...this.get('configData.rarConfig') }, this._getCallbackFunction('successMessage', 'failureMessage'));
        } else {
          failure('endpointRAR.rarConfig.permissionDenied');
        }
      }
    }
  }

  @action
  reset() {
    this.resetErrorProperties();
    this.send('resetRARConfig');
  }

}

export default connect(stateToComputed, dispatchToActions)(RARConfig);