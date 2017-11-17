import Component from 'ember-component';
import layout from './template';
import computed, { alias } from 'ember-computed-decorators';
import { isEmpty } from 'ember-utils';
import moment from 'moment';
import { connect } from 'ember-redux';
import service from 'ember-service/inject';
import _ from 'lodash';

import {
  setConfig,
  resetForm
} from '../../actions/data-creators';


const stateToComputed = ({ packager }) => ({
  // Already saved agent information
  configData: _.cloneDeep(packager.defaultPackagerConfig),
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
  primaryDestination: '',
  secondaryDestination: '',
  selectedFrequency: '1 Hour',

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

  @computed('configData.listOfService')
  listOfService(list) {
    const services = [];
    for (let i = 0; i < list.length; i++) {
      const service = {};
      const key = Object.keys(list[i]);
      service.id = key.toString();
      service.value = Object.values(list[i][key]).toString().replace(/,/g, ' ');
      services.push(service);
    }
    return services;
  },

  @computed('listOfService', 'configData.logCollectionConfig.primaryDestination')
  listOfSecondaryService(list, id) {
    return list.filter((obj) => id !== obj.id);
  },

  @computed('listOfService', 'configData.logCollectionConfig.primaryDestination')
  selectedPrimaryDestination(list, id) {
    return list.find((obj) => obj.id === id);
  },

  @computed('listOfService', 'configData.logCollectionConfig.secondaryDestination')
  selectedSecondaryDestination(list, id) {
    return list.find((obj) => obj.id === id);
  },

  validateMandatoryFields() {
    if (isEmpty(this.get('configData.logCollectionConfig.configName'))) {
      this.setProperties({
        isError: true,
        errorMessage: this.get('i18n').t('packager.emptyName')
      });
      return true;
    }
    if (!isEmpty(this.get('configData.logCollectionConfig.primaryDestination')) && isEmpty(this.get('selectedPrimaryDestination'))) {
      this.setProperties({
        errorClass: 'is-error',
        className: 'rsa-form-label is-error power-select'
      });
      return true;
    }
    if (isEmpty(this.get('configData.logCollectionConfig.protocol'))) {
      this.setProperties({
        protocolErrorClass: 'is-error',
        protocolClassName: 'rsa-form-label is-error power-select'
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
      className: 'rsa-form-label power-select',
      protocolErrorClass: null,
      protocolClassName: 'rsa-form-label power-select'
    });
  },

  flashMessages: service(),

  actions: {

    generateAgent() {
      const { autoUninstall } = this.get('configData.packageConfig');
      if (autoUninstall && !isEmpty(autoUninstall[0])) {
        this.set('configData.packageConfig.autoUninstall', moment(autoUninstall[0]).toISOString());
      }
      if (!this.get('isLogCollectionEnabled')) {
        // only package data need to be send when windows log collection is not enable
        this.send('setConfig', { packageConfig: this.get('configData.packageConfig') }, 'PACKAGE_CONFIG');
      } else if (!this.validateMandatoryFields()) {
        this.resetProperties();
        this.send('setConfig', this.get('configData'), false);
      }
    },

    generateLogConfig() {
      const logConfig = this.get('configData.logCollectionConfig');
      if (logConfig.enableHeartbeat && isEmpty(logConfig.heartbeatFrequency)) {
        this.set('configData.logCollectionConfig.heartbeatFrequency', this.get('selectedFrequency'));
      }
      if (!this.validateMandatoryFields()) {
        this.resetProperties();
        // only log config data need to be send on click of this button.
        this.send('setConfig', { logCollectionConfig: this.get('configData.logCollectionConfig') }, 'LOG_CONFIG');
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

    setPrimaryDestination(destination) {
      this.set('configData.logCollectionConfig.primaryDestination', destination);
      this.set('configData.logCollectionConfig.secondaryDestination', '');
    },

    setSecondaryDestination(destination) {
      this.set('configData.logCollectionConfig.secondaryDestination', destination);
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
