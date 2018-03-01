import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import {
  isFetchingSchedule,
  isEnabled,
  startDate,
  isError
} from 'hosts-scan-configure/reducers/hosts-scan/selectors';
import {
  updateScheduleProperty,
  saveScheduleConfig
} from 'hosts-scan-configure/actions/data-creators';
import computed from 'ember-computed-decorators';
import { isEmpty } from '@ember/utils';
import moment from 'moment';

const FLASH_MESSAGE_TYPES = {
  SUCCESS: {
    name: 'success',
    icon: 'check-circle-2'
  },
  ERROR: {
    name: 'error',
    icon: 'delete-1'
  }
};


const stateToComputed = (state) => ({
  isFetchingSchedule: isFetchingSchedule(state),
  isError: isError(state),
  enabled: isEnabled(state),
  startDate: startDate(state),
  config: state.hostsScan.config
});

const dispatchToActions = {
  updateScheduleProperty,
  saveScheduleConfig
};

const Form = Component.extend({
  layout,

  flashMessages: service(),

  classNames: 'schedule-form',

  isDirty: false,

  i18n: service(),

  errorMessage: 'hostsScanConfigure.error.generic',

  @computed('enabled', 'isDirty')
  isDisabled(enabled, isDirty) {
    return !(enabled || isDirty);
  },

  /**
   * Convenience method for showing a flash success/error message to the user on update or failure
   * @method showFlashMessage
   * @public
   * @param type
   * @param i18nKey
   * @param context
   */
  showFlashMessage(type, i18nKey, context) {
    const { i18n, flashMessages } = this.getProperties('i18n', 'flashMessages');
    flashMessages[type.name](i18n.t(i18nKey, context), { iconName: type.icon });
  },

  /**
   * Convenience method for showing a flash success/error message to the user on update or failure
   * @method showFlashMessage
   * @public
   * @param type
   * @param i18nKey
   * @param context
   */
  showErrorMessage(message) {
    const { flashMessages } = this.getProperties('flashMessages');
    const type = {
      name: 'error',
      icon: 'delete-1'
    };
    flashMessages[type.name](message, { iconName: type.icon });
  },

  actions: {
    toggleEnable() {
      this.toggleProperty('isDirty');
      this.send('updateScheduleProperty', 'enabled', !this.get('enabled'));
    },

    onDateChange(selectedDates, dateString) {
      if (!isEmpty(dateString)) {
        this.send('updateScheduleProperty', 'startDate', moment(dateString).format('MM/DD/YYYY'));
      }
    },

    saveConfig() {
      this.toggleProperty('isDirty');

      if (this.get('startDate') === 'today') {
        this.send('updateScheduleProperty', 'startDate', moment().format('MM/DD/YYYY'));
      }

      const callBackOptions = {
        onSuccess: () => {
          this.showFlashMessage(FLASH_MESSAGE_TYPES.SUCCESS, 'hostsScanConfigure.saveSuccess');
        },
        onFailure: () => {
          this.showErrorMessage(FLASH_MESSAGE_TYPES.ERROR, 'ERROR');
        }
      };
      this.send('saveScheduleConfig', this.get('config'), callBackOptions);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Form);
