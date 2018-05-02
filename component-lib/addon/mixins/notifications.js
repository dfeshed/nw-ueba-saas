import Mixin from '@ember/object/mixin';
import { inject as service } from '@ember/service';

export const FLASH_MESSAGE_TYPES = {
  SUCCESS: {
    name: 'success',
    icon: 'check-circle-2'
  },
  INFO: {
    name: 'info',
    icon: 'information-circle'
  },
  WARNING: {
    name: 'warning',
    icon: 'report-problem-circle'
  },
  ERROR: {
    name: 'error',
    icon: 'delete-1'
  }
};

/**
 * @class Notifications Mixin
 * Equips the consuming object with actions that allow for the display of a flash notification message
 * @public
 */
export default Mixin.create({
  flashMessages: service(),
  actions: {
    showFlashMessage(type, i18nKey, context) {
      const { i18n, flashMessages } = this.getProperties('i18n', 'flashMessages');
      flashMessages[type.name](i18n.t(i18nKey, context), { iconName: type.icon });
    },
    success(i18nKey, context) {
      this.send('showFlashMessage', FLASH_MESSAGE_TYPES.SUCCESS, i18nKey, context);
    },
    failure(i18nKey, context) {
      this.send('showFlashMessage', FLASH_MESSAGE_TYPES.ERROR, i18nKey, context);
    }
  }
});
