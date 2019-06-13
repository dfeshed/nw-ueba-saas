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
import { failure } from 'investigate-shared/utils/flash-messages';

import {
  setConfig,
  resetForm,
  saveUIState,
  setSelectedServer
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
  listOfService: listOfServices(state),
  endpointServerList: state.packager.endpointServerList,
  selectedServerIP: state.packager.selectedServerIP
});

const dispatchToActions = {
  setConfig,
  resetForm,
  saveUIState,
  setSelectedServer
};

const formComponent = Component.extend({
  layout,

  columns,

  classNames: ['packager-form'],

  features: service(),

  minDate: 'today',

  errorClass: null,

  isError: false,

  className: 'power-select',

  selectedFrequency: '1 Hour',

  invalidTableItem: '-999999',

  autoUninstall: null,

  status: 'enabled',

  editedHost: '',

  @computed('status')
  enabled: (status) => status !== 'disabled',

  @computed('selectedServerForEdit.port', 'isUpdating')
  isDisabled(port, isUpdating) {
    return isEmpty(port) || isUpdating;
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

  @computed('selectedServerIP', 'configData.packageConfig')
  selectedServerForEdit(selectedServerIP, packageConfig) {
    const { host: selectedHost } = selectedServerIP;
    const { serviceId, server: host, port } = packageConfig;
    return (selectedServerIP.id === serviceId) ? { ...selectedServerIP, host, port } : { ...selectedServerIP, host: selectedHost, port: 443 };
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
      status: 'enabled',
      editedHost: ''
    });
  },

  _getTimezoneTime(selectedTime) {
    // Removing browser timezone information
    const timeWithoutZone = moment(selectedTime).parseZone(selectedTime).format('YYYY-MM-DDTHH:mm:ss');
    // Setting the timezone as UTC
    return moment(timeWithoutZone).parseZone().tz('UTC').format('YYYY-MM-DDTHH:mm:ss.sssZ');
  },

  _scrollTo(target) {
    document.querySelectorAll(target)[0].scrollIntoView();
  },

  _getCallbackFunction(configDataOld) {
    return {
      onSuccess: () => {
        this.resetErrorProperties();
      },
      onFailure: () => {
        this.send('saveUIState', configDataOld);
        failure('packager.errorMessages.packagerNotCreated');
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
      const packagerAutoUninstall = this.get('autoUninstall');
      let autoUninstall = null;
      const { port, hostIpClone, id: serviceId } = this.get('selectedServerForEdit');

      if (packagerAutoUninstall && packagerAutoUninstall.length) {
        autoUninstall = this._getTimezoneTime(packagerAutoUninstall[0]);
      }

      const driverServiceName = this.get('driverServiceName');
      const driverDisplayName = this.get('driverDisplayName');
      const driverDescription = this.get('driverDescription');
      const monitoringModeEnabled = true;
      const server = this.get('editedHost') || hostIpClone;

      const configDataBkp = this.get('configData');
      const configDataClone = { ...configDataBkp };
      configDataClone.packageConfig = {
        ...configDataClone.packageConfig,
        autoUninstall,
        driverServiceName,
        driverDisplayName,
        driverDescription,
        monitoringModeEnabled,
        server,
        serviceId,
        port
      };

      const error = validatePackageConfig(configDataClone.packageConfig);
      this.setProperties(error);
      if (!error) {
        this.send('saveUIState', configDataClone);
        this.send('setConfig', { ...configDataClone }, this._getCallbackFunction(configDataBkp), this.get('serverId'));
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

    setSelect(option) {
      this.send('setSelectedServer', option);
      this.set('editedHost', '');
    },

    reset() {
      this.resetErrorProperties();
      this.send('resetForm');
    },
    onForceOverwiteChange() {
      this.toggleProperty('configData.packageConfig.forceOverwrite');
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(formComponent);
