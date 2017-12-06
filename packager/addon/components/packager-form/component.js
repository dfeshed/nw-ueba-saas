import Component from 'ember-component';
import layout from './template';
import computed, { alias } from 'ember-computed-decorators';
import { isEmpty } from 'ember-utils';
import moment from 'moment';
import { connect } from 'ember-redux';
import service from 'ember-service/inject';
import _ from 'lodash';
import { listOfServices } from '../../reducers/selectors';
import columns from './columns';

import {
  setConfig,
  resetForm
} from '../../actions/data-creators';


const stateToComputed = (state) => ({
  // Already saved agent information
  configData: _.cloneDeep(state.packager.defaultPackagerConfig),
  // Flag to indicate config is currently updating or not
  isUpdating: state.packager.updating,
  // fetching decoder and concentrator
  listOfService: listOfServices(state)
});

const dispatchToActions = {
  setConfig,
  resetForm
};

const VALID_EVENT_PATTERN = /^[0-9-]+$/;
const INVALID_CONFIG_NAME_PATTERN = /[ !@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/g;

const formComponent = Component.extend({
  layout,

  classNames: ['packager-form'],

  minDate: new Date(),
  errorClass: null,
  isError: false,
  protocolOptions: ['UDP', 'TCP', 'TLS'],
  className: 'rsa-form-label power-select',
  protocolClassName: 'rsa-form-label power-select',
  isGenerateLogDisabled: true,
  isLogCollectionEnabled: false,
  primaryDestination: '',
  secondaryDestination: '',
  selectedFrequency: '1 Hour',
  invalidTableItem: '-999999',
  columns,

  @alias('configData.packageConfig.autoUninstall')
  autoUninstall: null,

  @alias('configData.packageConfig.forceOverwrite')
  forceOverwrite: false,

  @computed('configData.packageConfig.server', 'configData.packageConfig.port', 'isUpdating')
  isDisabled(server, port, isUpdating) {
    return isEmpty(server) || isEmpty(port) || isUpdating;
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
    const configName = this.get('configData.logCollectionConfig.configName');
    const channels = this.get('configData.logCollectionConfig.channels');
    let channelValidation = true;
    if (isEmpty(configName)) {
      this.setProperties({
        isError: true,
        errorMessage: this.get('i18n').t('packager.emptyName')
      });
      return true;
    }
    if (INVALID_CONFIG_NAME_PATTERN.test(configName)) {
      this.setProperties({
        isError: true,
        errorMessage: this.get('i18n').t('packager.specialCharacter')
      });
      return true;
    }
    if (isEmpty(this.get('configData.logCollectionConfig.primaryDestination'))) {
      this.setProperties({
        errorClass: 'is-error',
        className: 'rsa-form-label is-error power-select'
      });
      return true;
    }
    channels.every((obj) => {
      const { eventId, filter, channel } = obj;
      if (isEmpty(channel) || isEmpty(filter) || isEmpty(eventId)) {
        this.set('invalidTableItem', '');
        channelValidation = false;
        return false;
      }

      const arrayOfEvents = eventId.split(',');
      arrayOfEvents.every((event) => {
        if (!VALID_EVENT_PATTERN.test(event.trim())) {
          this.set('invalidTableItem', eventId);
          channelValidation = false;
          return false;
        } else {
          return true;
        }
      });

      return channelValidation;
    });

    if (!channelValidation) {
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
      protocolClassName: 'rsa-form-label power-select',
      selectedProtocol: null
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
        this.resetProperties();
      } else if (!this.validateMandatoryFields()) {
        this.send('setConfig', this.get('configData'));
        this.resetProperties();
      }
    },

    generateLogConfig() {
      if (!this.validateMandatoryFields()) {
        // only log config data need to be send on click of this button.
        this.send('setConfig', { logCollectionConfig: this.get('configData.logCollectionConfig') }, 'LOG_CONFIG');
        this.resetProperties();
      }
    },

    enableLogCollection() {
      this.toggleProperty('isGenerateLogDisabled');
      this.toggleProperty('isLogCollectionEnabled');
      this.set('configData.logCollectionConfig.enabled', this.get('isLogCollectionEnabled'));
      this.resetProperties();
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
          const listOfDest = [];
          fileContent = e.target.result;
          formContent = JSON.parse(fileContent.substring(0, fileContent.lastIndexOf('}') + 1));
          this.set('configData.logCollectionConfig', formContent);
          this.set('selectedProtocol', formContent.protocol);

          const primaryExists = this.get('listOfService').some((l) =>
          formContent.primaryDestination === l.id);

          if (!isEmpty(formContent.primaryDestination) && !primaryExists) {
            listOfDest.push(formContent.primaryDestination);
            this.set('primaryDestination', '');
          } else {
            this.set('primaryDestination', formContent.primaryDestination);
          }

          const secondaryExists = this.get('listOfService').some((l) =>
          formContent.secondaryDestination === l.id);

          if (!isEmpty(formContent.secondaryDestination) && !secondaryExists) {
            listOfDest.push(formContent.secondaryDestination);
            this.set('secondaryDestination', '');
          } else {
            this.set('secondaryDestination', formContent.secondaryDestination);
          }

          if (!isEmpty(listOfDest)) {
            const dest = this.get('i18n').t('packager.upload.warning', { id: listOfDest.join(',') });
            this.get('flashMessages').warning(`${this.get('i18n').t('packager.upload.success')} ${dest}`);
          } else {
            this.get('flashMessages').success(this.get('i18n').t('packager.upload.success'));
          }

        } catch (err) {
          this.get('flashMessages').warning(this.get('i18n').t('packager.upload.failure'));
        } finally {
          ev.target.value = null;
        }
      };
      reader.readAsText(ev.target.files[0]);
    },

    // adding an empty row to the channel filters table
    addRowFilter(item) {
      if (item.target.classList.contains('rsa-icon')) {
        this.get('configData.logCollectionConfig.channels').pushObject({ channel: '', filter: 'Include', eventId: '' });
      }
    },
    // pass the index of the row to delete the row in the channel filters
    deleteRow(index) {
      this.get('configData.logCollectionConfig.channels').removeAt(index);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(formComponent);
