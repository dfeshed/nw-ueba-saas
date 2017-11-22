import Component from 'ember-component';
import layout from './template';
import { connect } from 'ember-redux';
import service from 'ember-service/inject';
import { isFetchingSchedule, isEnabled } from 'hosts-scan-configure/reducers/schedule/selectors';
import { updateScheduleProperty, saveScheduleConfig } from 'hosts-scan-configure/actions/data-creators';
import computed from 'ember-computed-decorators';

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
  enabled: isEnabled(state),
  config: state.schedule.config
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

    saveConfig() {
      this.toggleProperty('isDirty');
      const callBackOptions = {
        onSuccess: () => {
          this.showFlashMessage(FLASH_MESSAGE_TYPES.SUCCESS, 'endpoint.common.saveSuccess');
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
