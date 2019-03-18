import layout from './template';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { validateConfig } from 'investigate-shared/utils/validation-utils';
import { getRARDownloadID } from '../../../actions/data-creators';
import { failure } from 'investigate-shared/utils/flash-messages';

const stateToComputed = ({ rar }) => ({
  isLoading: rar.loading
});

const dispatchToActions = {
  getRARDownloadID
};


const RARInstall = Component.extend({
  layout,
  tagName: 'box',
  classNames: ['rar-installer', 'rar-configuration'],
  rarInsallerPassword: '',
  isPasswordError: false,

  _getCallbackFunction() {
    const self = this;
    return {
      onSuccess() {
        self.set('isPasswordError', false);
      },
      onFailure(message) {
        failure(message, null, false);
      }
    };
  },
  actions: {
    generateRARInstaller() {
      const password = this.get('rarInsallerPassword');
      const error = validateConfig({ password });

      this.setProperties(error);
      if (!error) {
        this.send('getRARDownloadID', { packagePassword: password }, this._getCallbackFunction());
      }
    },
    validate(value) {
      const password = value.trim();
      const validatePassword = validateConfig({ password });
      if (validatePassword) {
        this.setProperties({ ...validatePassword });
      } else {
        this.set('isPasswordError', false);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(RARInstall);