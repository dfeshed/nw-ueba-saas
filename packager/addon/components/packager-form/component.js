import Component from 'ember-component';
import layout from './template';
import computed, { alias } from 'ember-computed-decorators';
import { isEmpty } from 'ember-utils';
import moment from 'moment';
import { connect } from 'ember-redux';
import service from 'ember-service/inject';
import _ from 'lodash';
import { listOfServices } from '../../reducers/selectors';
import { getConfiguration } from 'packager/actions/fetch/packager';
import { validatePackageConfig, validateLogConfigFields } from './validation-utils';
import columns from './columns';
import $ from 'jquery';

import {
  setConfig,
  resetForm,
  saveUIState
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
  resetForm,
  saveUIState
};

const formComponent = Component.extend({
  layout,

  columns,

  classNames: ['packager-form'],

  flashMessages: service(),

  minDate: 'today',

  errorClass: null,

  isError: false,

  protocolOptions: ['UDP', 'TCP', 'TLS'],

  className: 'power-select',

  protocolClassName: 'power-select',

  isGenerateLogDisabled: true,

  isLogCollectionEnabled: false,

  primaryDestination: '',

  secondaryDestination: '',

  selectedFrequency: '1 Hour',
  invalidTableItem: '-999999',
  selectedProtocol: 'TCP',

  testLog: true,


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

  @computed()
  panelId() {
    return `winLogCollectionTooltip-${this.get('elementId')}`;
  },

  resetProperties() {
    this.setProperties({
      errorMessage: null,
      isError: false,
      errorClass: null,
      className: 'power-select',
      protocolClassName: 'power-select',
      isPortError: false,
      isConfigError: false,
      isServerError: false,
      isDisplayNameError: false,
      isServiceNameError: false,
      invalidPortMessage: null,
      invalidServerMessage: null,
      invalidServiceNameMessage: null,
      invalidDisplayNameMessage: null,
      invalidTableItem: null,
      isPasswordError: null,
      passwordEmptyMessage: null,
      testLog: true
    });
  },

  _validate() {
    let error = validatePackageConfig(this.get('configData.packageConfig'));
    if (!error) {
      error = validateLogConfigFields(this.get('configData.logCollectionConfig'));
    }
    return error;
  },

  _scrollTo(target) {
    $(target)[0].scrollIntoView();
  },

  _validateDestinationFields(primaryDestination, secondaryDestination) {
    const listOfDest = [];
    const flashMessage = this.get('flashMessages');
    const listOfService = this.get('listOfService');
    const i18nMessages = this.get('i18n');
    const isPrimaryDestinationAvailable = (listOfService.some((service) => primaryDestination === service.id));
    const isSecondaryDestinationAvailable = (listOfService.some((service) => secondaryDestination === service.id));
    if (!isPrimaryDestinationAvailable) {
      listOfDest.push(primaryDestination);
    }
    if (!isSecondaryDestinationAvailable) {
      listOfDest.push(secondaryDestination);
    }
    if (!isEmpty(listOfDest)) {
      const dest = i18nMessages.t('packager.upload.warning', { id: listOfDest.join(',') });
      flashMessage.warning(`${i18nMessages.t('packager.upload.success')} ${dest}`);
    } else {
      flashMessage.success(i18nMessages.t('packager.upload.success'));
    }
  },

  _getCallbackFunction() {
    return {
      onFailure: (response) => {
        const error = validateLogConfigFields(this.get('configData.logCollectionConfig'), response.meta);
        this.setProperties(error);
      }
    };
  },

  _fetchLogConfigFromServer(formContent) {
    getConfiguration(formContent)
      .then((response) => {
        const responseConfiguration = response.data;
        this.set('selectedProtocol', responseConfiguration.protocol);
        this.set('configData.logCollectionConfig', responseConfiguration);
        this._validateDestinationFields(responseConfiguration.primaryDestination, responseConfiguration.secondaryDestination);
      }).catch(({ meta: { reason } }) => {
        const i18nMessage = `packager.errorMessages.${reason}`;
        this.get('flashMessages').error(this.get('i18n').t(i18nMessage));
      });
  },

  actions: {

    generateAgent() {
      this.resetProperties();
      const { autoUninstall } = this.get('configData.packageConfig');
      if (autoUninstall && autoUninstall.length) {
        this.set('configData.packageConfig.autoUninstall', moment(autoUninstall[0]).toISOString());
      } else {
        this.set('configData.packageConfig.autoUninstall', null);
      }
      if (!this.get('isLogCollectionEnabled')) {
        // only package data need to be send when windows log collection is not enable
        const error = validatePackageConfig(this.get('configData.packageConfig'));
        this.setProperties(error);
        if (!error) {
          this.send('saveUIState', this.get('configData'));
          this.send('setConfig', { packageConfig: this.get('configData.packageConfig') }, 'PACKAGE_CONFIG', this._getCallbackFunction());
        } else {
          this._scrollTo('.server-input-group');
        }
      } else {
        this.set('configData.logCollectionConfig.enabled', true);
        this.set('configData.logCollectionConfig.protocol', this.get('selectedProtocol'));
        this.set('configData.logCollectionConfig.testLogOnLoad', this.get('testLog'));
        const error = this._validate();
        this.setProperties(error);
        if (!error) {
          this.send('saveUIState', this.get('configData'));
          this.send('setConfig', this.get('configData'), false, this._getCallbackFunction());
        } else {
          this._scrollTo('.server-input-group');
        }
      }
    },

    generateLogConfig() {
      const error = validateLogConfigFields(this.get('configData.logCollectionConfig'));
      this.setProperties(error);
      if (!error) {
        // only log config data need to be send on click of this button.
        this.send('saveUIState', this.get('configData'));
        this.set('configData.logCollectionConfig.testLogOnLoad', this.get('testLog'));
        this.set('configData.logCollectionConfig.enabled', true);
        this.set('configData.logCollectionConfig.protocol', this.get('selectedProtocol'));
        this.send('setConfig', { logCollectionConfig: this.get('configData.logCollectionConfig') }, 'LOG_CONFIG', this._getCallbackFunction());
      } else {
        this._scrollTo('.windows-log-collection');
      }
    },

    enableLogCollection() {
      this.toggleProperty('isGenerateLogDisabled');
      this.toggleProperty('isLogCollectionEnabled');
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
          const fileContent = e.target.result;
          const formContent = JSON.parse(fileContent.substring(fileContent.indexOf('{'), fileContent.lastIndexOf('}') + 1));
          formContent.enabled = this.get('isLogCollectionEnabled');
          this._fetchLogConfigFromServer(formContent);
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
        this.get('configData.logCollectionConfig.channels').pushObject({ channel: '', filter: 'Include', eventId: 'ALL' });
      }
    },
    // pass the index of the row to delete the row in the channel filters
    deleteRow(index) {
      this.get('configData.logCollectionConfig.channels').removeAt(index);
    },
    reset() {
      this.resetProperties();
      this.set('selectedProtocol', 'TCP');
      this.send('resetForm');
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(formComponent);
