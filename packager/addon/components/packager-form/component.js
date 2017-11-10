import Component from 'ember-component';
import layout from './template';
import computed, { alias } from 'ember-computed-decorators';
import { isEmpty } from 'ember-utils';
import moment from 'moment';
import { connect } from 'ember-redux';
import service from 'ember-service/inject';

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
  errorClass: null,
  isError: false,
  protocolOptions: ['UDP', 'TCP', 'TLS'],
  heartbeatFrequency: ['1 Hour', '6 Hours', '12 Hours', '24 Hours'],
  className: 'rsa-form-label power-select',
  isGenerateLogDisabled: true,
  isLogCollectionEnabled: false,

  @alias('configData.packageConfig.autoUninstall')
  autoUninstall: null,

  @alias('configData.packageConfig.forceOverwrite')
  forceOverwrite: false,

  @alias('configData.logCollectionConfig.enableHeartbeat')
  enableHeartFrequency: false,

  @computed('configData.packageConfig.server', 'configData.packageConfig.port', 'isUpdating')
  isDisabled(server, port, isUpdating) {
    return isEmpty(server) || isEmpty(port) || isUpdating;
  },

  validateMandatoryFields() {
    if (isEmpty(this.get('configData.logCollectionConfig.configName'))) {
      this.setProperties({
        isError: true,
        errorMessage: this.get('i18n').t('packager.emptyName')
      });
      return true;
    }
    if (!isEmpty(this.get('configData.logCollectionConfig.primaryDestination')) && isEmpty(this.get('selectedPrimary'))) {
      this.setProperties({
        errorClass: 'is-error',
        className: 'rsa-form-label is-error power-select'
      });
      return true;
    }
    return false;
  },

  resetProperties() {
    this.setProperties({
      errorMessage: null,
      isError: false,
      errorClass: null,
      className: 'rsa-form-label power-select'
    });
  },

  flashMessages: service(),

  actions: {

    generateAgent() {
      const { autoUninstall } = this.get('configData');
      if (!isEmpty(autoUninstall[0])) {
        this.set('configData.packageConfig.autoUninstall', moment(autoUninstall[0]).toISOString());
      }
      if (!this.get('isLogCollectionEnabled')) {
        // only package data need to be send when windows log collection is not enable
        this.send('setConfig', { packageConfig: this.get('configData.packageConfig') });
      } else if (!this.validateMandatoryFields()) {
        this.resetProperties();
        this.send('setConfig', this.get('configData'), false);
      }
    },

    generateLogConfig() {
      if (!this.validateMandatoryFields()) {
        this.resetProperties();
        // only log config data need to be send on click of this button.
        this.send('setConfig', { logCollectionConfig: this.get('configData.logCollectionConfig') });
      }
    },

    enableLogCollection() {
      this.toggleProperty('isGenerateLogDisabled');
      this.toggleProperty('isLogCollectionEnabled');
    },

    toggleProperty(property) {
      this.toggleProperty(property);
    },

    setSelect(property, selected, option) {
      this.set(selected, option);
      this.set(`configData.logCollectionConfig.${property}`, option);
    },

    uploadConfig(ev) {
      const reader = new FileReader();
      reader.onload = (e) => {
        try {
          let fileContent = '';
          let formContent = {};
          fileContent = e.target.result;
          formContent = JSON.parse(fileContent.substring(0, fileContent.indexOf('}') + 1));
          this.set('configData.logCollectionConfig', formContent);
          this.get('flashMessages').success(this.get('i18n').t('packager.upload.success'));
        } catch (err) {
          this.get('flashMessages').warning(this.get('i18n').t('packager.upload.failure'));
        } finally {
          ev.target.value = null;
        }
      };
      reader.readAsText(ev.target.files[0]);
    }

  }

});

export default connect(stateToComputed, dispatchToActions)(formComponent);
