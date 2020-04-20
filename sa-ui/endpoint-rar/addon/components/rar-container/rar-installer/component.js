import classic from 'ember-classic-decorator';
import { classNames, layout as templateLayout } from '@ember-decorators/component';
import layout from './template';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { validateConfig } from 'investigate-shared/utils/validation-utils';
import { getRARDownloadID } from '../../../actions/data-creators';
import { failure } from 'investigate-shared/utils/flash-messages';
import { inject as service } from '@ember/service';
import { action } from '@ember/object';

const stateToComputed = ({ rar }) => ({
  isLoading: rar.loading
});

const dispatchToActions = {
  getRARDownloadID
};

@classic
@templateLayout(layout)
@classNames('rar-installer', 'rar-configuration')
class RARInstall extends Component {

  @service
  accessControl;

  rarInstallerPassword = '';
  isPasswordError = false;

  _getCallbackFunction() {
    const self = this;
    return {
      onSuccess() {
        self.set('isPasswordError', false);
      },
      onFailure(message) {
        failure(`endpointRAR.rarConfig.${message}`);
      }
    };
  }

  @action
  generateRARInstaller() {
    const password = this.get('rarInstallerPassword');
    const error = validateConfig({ password });

    this.setProperties(error);
    if (!error) {
      if (this.get('accessControl.hasEndpointRarPermission')) {
        this.send('getRARDownloadID', { packagePassword: password }, this._getCallbackFunction());
      } else {
        failure('endpointRAR.rarConfig.permissionDeniedForDownload');
      }
    }
  }

  @action
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

export default connect(stateToComputed, dispatchToActions)(RARInstall);