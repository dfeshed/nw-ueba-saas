import Component from '@ember/component';
import { isPresent } from '@ember/utils';
import { connect } from 'ember-redux';
import Notifications from 'component-lib/mixins/notifications';
import computed, { notEmpty } from 'ember-computed-decorators';
import {
  logParsers,
  availableDeviceTypes,
  deviceClasses
} from 'configure/reducers/content/log-parser-rules/selectors';

import parserRuleCreators from 'configure/actions/creators/content/log-parser-rule-creators';

const { addLogParser } = parserRuleCreators;

const stateToComputed = (state) => ({
  logParsers: logParsers(state),
  deviceTypes: availableDeviceTypes(state),
  deviceClasses: deviceClasses(state)
});

const dispatchToActions = {
  addLogParser
};

const VALID_PARSERNAME_REGEX = /^[a-z][a-z0-9_]{2,29}$/;

const AddLogParser = Component.extend(Notifications, {
  classNames: ['add-log-parser'],

  _selectedDeviceType: null,

  @computed('deviceTypes', '_selectedDeviceType')
  selectedDeviceType: {
    get(deviceTypes, selectedDeviceType) {
      return selectedDeviceType || deviceTypes[0];
    },
    set(value) {
      this.setProperties({
        logDeviceParserName: value.name,
        displayName: value.desc,
        deviceClass: value.category
      });
      this.set('_selectedDeviceType', value);
      return value;
    }
  },

  logDeviceParserName: null,

  deviceClass: null,

  displayName: null,

  @computed('logDeviceParserName')
  inValidParserNameWarning(name) {
    return !(!isPresent(name) || VALID_PARSERNAME_REGEX.test(name));
  },

  @computed('logParsers', 'logDeviceParserName')
  nameAlreadyExists(logParsers, name) {
    return isPresent(logParsers) && isPresent(name) && !!logParsers.findBy('name', name);
  },

  @computed('logParsers', 'displayName')
  displayNameAlreadyExists(logParsers, displayName) {
    return isPresent(logParsers) && isPresent(displayName) &&
      (logParsers.filter((parser) => (isPresent(parser.displayName) && parser.displayName.toLowerCase() === displayName.toLowerCase())).length > 0);
  },

  @notEmpty('selectedDeviceType.name') isExistingDeviceType: false,

  @computed('logDeviceParserName', 'displayName', 'deviceClass', 'nameAlreadyExists', 'displayNameAlreadyExists')
  isValid(name, displayName, deviceClass, nameAlreadyExists, displayNameAlreadyExists) {
    return isPresent(name) && isPresent(displayName) && isPresent(deviceClass) &&
      VALID_PARSERNAME_REGEX.test(name) && !nameAlreadyExists && !displayNameAlreadyExists;
  },

  actions: {
    handleAddParser() {
      const {
        logDeviceParserName,
        displayName,
        deviceClass,
        cloneFrom
      } = this.getProperties('logDeviceParserName', 'displayName', 'deviceClass', 'cloneFrom');

      this.send('addLogParser', {
        logDeviceParserName,
        displayName,
        deviceClass,
        cloneFrom: cloneFrom && cloneFrom.name || null
      }, {
        onSuccess: () => {
          this.sendAction('onLogParserAdded');
          this.send('success', 'configure.logsParser.addParser.addParserSuccessful');
        },
        onFailure: () => {
          this.send('failure', 'configure.logsParser.addParser.addParserFailed');
        }
      });
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(AddLogParser);