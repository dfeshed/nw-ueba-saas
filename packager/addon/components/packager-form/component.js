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

import { validatePackageConfig } from './validation-utils';
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

  primaryDestination: '',

  secondaryDestination: '',

  selectedFrequency: '1 Hour',

  invalidTableItem: '-999999',

  selectedProtocol: 'TCP',

  testLog: true,

  autoUninstall: null,

  status: 'enabled',

  @computed('status')
  enabled: (status) => status !== 'disabled',

  @computed('configData.packageConfig.server', 'configData.packageConfig.port', 'isUpdating')
  isDisabled(server, port, isUpdating) {
    return isEmpty(server) || isEmpty(port) || isUpdating;
  },

  @computed('configData.packageConfig.driverDisplayName')
  driverDisplayName(displayName) {
    return displayName || this.get('defaultDriverDisplayName') || '';
  },

  @computed('configData.packageConfig.driverServiceName')
  driverServiceName(serviceName) {
    return serviceName || this.get('defaultDriverServiceName') || '';
  },

  @computed('configData.packageConfig.driverDescription')
  driverDescription(driverDescription) {
    return driverDescription || this.get('defaultDriverDescription') || '';
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
      onSuccess: () => {
        this.resetErrorProperties();
      }
    };
  },

  _agentConfigExpand() {
    if (document.querySelector('.agentConfiguration.is-collapsed')) {
      document.querySelector('.agentConfiguration > h3').click();
    }
  },

  actions: {

    validate(value) {
      const val = value.trim();
      if (isEmpty(val)) {
        this.set('isPasswordError', true);
        this.set('passwordInvalidMessage', 'packager.errorMessages.passwordEmptyMessage');
      } else {
        this.set('isPasswordError', false);
      }
    },

    generateAgent() {
      this.resetErrorProperties();
      const autoUninstall = this.get('autoUninstall');
      if (autoUninstall && autoUninstall.length) {
        const date = this._getTimezoneTime(autoUninstall[0]);
        this.set('configData.packageConfig.autoUninstall', date);
      } else {
        this.set('configData.packageConfig.autoUninstall', null);
      }

      this.set('configData.packageConfig.driverServiceName', this.get('driverServiceName'));
      this.set('configData.packageConfig.driverDisplayName', this.get('driverDisplayName'));
      this.set('configData.packageConfig.driverDescription', this.get('driverDescription'));
      this.set('configData.packageConfig.monitoringModeEnabled', true);

      const error = validatePackageConfig(this.get('configData.packageConfig'));
      this.setProperties(error);
      if (!error) {
        this.send('saveUIState', this.get('configData'));
        this.send('setConfig', { ...this.get('configData.packageConfig') }, this._getCallbackFunction(), this.get('serverId'));
      } else {
        if (error.isAccordion) {
          this._agentConfigExpand();
        }
        this._scrollTo('.server-input-group');
      }
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
    }

  }

});

export default connect(stateToComputed, dispatchToActions)(formComponent);
