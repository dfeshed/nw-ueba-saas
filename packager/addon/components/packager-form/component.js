import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { isEmpty } from '@ember/utils';
import moment from 'moment';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import _ from 'lodash';
import { listOfServices,
  defaultDriverDescription,
  defaultDriverDisplayName,
  defaultDriverServiceName } from '../../reducers/selectors';
import { getConfiguration } from 'packager/actions/fetch/packager';
import {
  validatePackageConfig,
  validateLogConfigFields
} from './validation-utils';
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

  defaultDriverDescription: defaultDriverDescription(state),

  defaultDriverServiceName: defaultDriverServiceName(state),

  defaultDriverDisplayName: defaultDriverDisplayName(state),

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

  features: service(),

  minDate: 'today',

  errorClass: null,

  isError: false,

  protocolOptions: ['UDP', 'TCP', 'TLS'],

  className: 'power-select',

  isGenerateLogDisabled: true,

  isLogCollectionEnabled: false,

  primaryDestination: '',

  secondaryDestination: '',

  selectedFrequency: '1 Hour',

  invalidTableItem: '-999999',

  selectedProtocol: 'TCP',

  testLog: true,

  autoUninstall: null,

  isFullAgentEnabled: false,

  isMonitorModeEnabled: true,

  status: 'enabled',

  @computed('status')
  enabled: (status) => status !== 'disabled',

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

  @computed('isFullAgentEnabled', 'configData.packageConfig.driverDisplayName')
  driverDisplayName(isFullAgentEnabled, displayName) {
    if (isFullAgentEnabled) {
      if (displayName) {
        return displayName;
      }
      return this.get('defaultDriverDisplayName') || '';
    }
    return null;
  },

  @computed('isFullAgentEnabled', 'configData.packageConfig.driverServiceName')
  driverServiceName(isFullAgentEnabled, serviceName) {
    if (isFullAgentEnabled) {
      if (serviceName) {
        return serviceName;
      }
      return this.get('defaultDriverServiceName') || '';
    }
    return null;
  },

  @computed('isFullAgentEnabled', 'configData.packageConfig.driverDescription')
  driverDescription(isFullAgentEnabled, driverDescription) {
    if (isFullAgentEnabled) {
      if (driverDescription) {
        return driverDescription;
      }
      return this.get('defaultDriverDescription') || '';
    }
    return null;
  },

  resetErrorProperties() {
    this.setProperties({
      errorMessage: null,
      isError: false,
      errorClass: null,
      className: 'power-select',
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
      passwordInvalidMessage: null,
      isDriverDisplayNameError: false,
      isDriverServiceNameError: false
    });
  },

  resetDefaultProperties() {
    this.setProperties({
      testLog: true,
      selectedProtocol: 'TCP',
      status: 'enabled'
    });
  },

  _getTimezoneTime(selectedTime) {
    // Removing browser timezone information
    const timeWithoutZone = moment(selectedTime).parseZone(selectedTime).format('YYYY-MM-DDTHH:mm:ss');
    // Setting the timezone as UTC
    return moment(timeWithoutZone).parseZone().tz('UTC').format('YYYY-MM-DDTHH:mm:ss.sssZ');
  },

  _scrollTo(target) {
    $(target)[0].scrollIntoView();
  },

  _getCallbackFunction() {
    return {
      onFailure: (response) => {
        const error = validateLogConfigFields(this.get('configData.logCollectionConfig'), this.get('enabled'), response.meta);
        this.setProperties(error);
      },
      onSuccess: () => {
        this.resetErrorProperties();
      }
    };
  },

  _fetchLogConfigFromServer(formContent) {
    getConfiguration(formContent)
      .then((response) => {
        const flashMessage = this.get('flashMessages');
        const i18nMessages = this.get('i18n');
        const responseConfiguration = response.data;
        this.set('selectedProtocol', responseConfiguration.protocol);
        this.set('configData.logCollectionConfig', responseConfiguration);
        if (!responseConfiguration.enabled) {
          this.set('status', 'disabled');
        }
        if (responseConfiguration.hasErrors) {
          const warningMessage = `packager.errorMessages.${responseConfiguration.errorMessage}`;
          flashMessage.warning(`${i18nMessages.t('packager.upload.success')} ${i18nMessages.t(warningMessage)}`);
        } else {
          flashMessage.success(i18nMessages.t('packager.upload.success'));
        }
      }).catch(() => {
        this.get('flashMessages').error(this.get('i18n').t('packager.upload.failure'));
      });
  },

  actions: {

    generateAgent() {
      this.resetErrorProperties();
      const autoUninstall = this.get('autoUninstall');
      if (autoUninstall && autoUninstall.length) {
        const date = this._getTimezoneTime(autoUninstall[0]);
        this.set('configData.packageConfig.autoUninstall', date);
      } else {
        this.set('configData.packageConfig.autoUninstall', null);
      }
      if (!this.get('isFullAgentEnabled')) {
        this.set('configData.packageConfig.driverServiceName', undefined);
        this.set('configData.packageConfig.driverDisplayName', undefined);
        this.set('configData.packageConfig.driverDescription', undefined);
        this.set('configData.packageConfig.monitoringModeEnabled', undefined);
        this.set('configData.packageConfig.fullAgent', false);
      } else {
        this.set('configData.packageConfig.driverServiceName', this.get('driverServiceName'));
        this.set('configData.packageConfig.driverDisplayName', this.get('driverDisplayName'));
        this.set('configData.packageConfig.driverDescription', this.get('driverDescription'));
        this.set('configData.packageConfig.monitoringModeEnabled', this.get('isMonitorModeEnabled'));
        this.set('configData.packageConfig.fullAgent', this.get('isFullAgentEnabled'));
      }
      if (!this.get('isLogCollectionEnabled')) {
        // only package data need to be send when windows log collection is not enable
        const error = validatePackageConfig(this.get('configData.packageConfig'));
        this.setProperties(error);
        if (!error) {
          this.send('saveUIState', this.get('configData'));
          this.send('setConfig', { packageConfig: this.get('configData.packageConfig') }, 'PACKAGE_CONFIG', this._getCallbackFunction(), this.get('serverId'));
        } else {
          this._scrollTo('.server-input-group');
        }
      } else {
        let error;
        this.set('configData.logCollectionConfig.enabled', this.get('enabled'));
        this.set('configData.logCollectionConfig.protocol', this.get('selectedProtocol'));
        this.set('configData.logCollectionConfig.testLogOnLoad', this.get('testLog'));
        const packageConfigError = error = validatePackageConfig(this.get('configData.packageConfig'));
        if (!packageConfigError) {
          error = validateLogConfigFields(this.get('configData.logCollectionConfig'), this.get('enabled'));
        }
        this.setProperties(error);
        if (!error) {
          this.send('saveUIState', this.get('configData'));
          this.send('setConfig', this.get('configData'), false, this._getCallbackFunction(), this.get('serverId'));
        } else {
          if (packageConfigError) {
            this._scrollTo('.server-input-group');
          }
        }
      }
    },

    generateLogConfig() {
      this.resetErrorProperties();
      const error = validateLogConfigFields(this.get('configData.logCollectionConfig'), this.get('enabled'));
      this.setProperties(error);
      if (!error) {
        // only log config data need to be send on click of this button.
        this.send('saveUIState', this.get('configData'));
        this.set('configData.logCollectionConfig.testLogOnLoad', this.get('testLog'));
        this.set('configData.logCollectionConfig.enabled', this.get('enabled'));
        this.set('configData.logCollectionConfig.protocol', this.get('selectedProtocol'));
        this.send('setConfig', { logCollectionConfig: this.get('configData.logCollectionConfig') }, 'LOG_CONFIG', this._getCallbackFunction(), this.get('serverId'));
      } else {
        this._scrollTo('.windows-log-collection');
      }
    },

    enableLogCollection() {
      this.toggleProperty('isGenerateLogDisabled');
      this.toggleProperty('isLogCollectionEnabled');
      this.resetDefaultProperties();
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
    addRowFilter() {
      this.get('configData.logCollectionConfig.channels').pushObject({ channel: '', filter: 'Include', eventId: 'ALL' });
    },
    // pass the index of the row to delete the row in the channel filters
    deleteRow(index) {
      this.get('configData.logCollectionConfig.channels').removeAt(index);
    },
    reset() {
      this.resetErrorProperties();
      this.resetDefaultProperties();
      this.send('resetForm');
    },
    onForceOverwiteChange() {
      this.toggleProperty('configData.packageConfig.forceOverwrite');
    },
    enableFullAgent() {
      this.toggleProperty('isFullAgentEnabled');
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(formComponent);
