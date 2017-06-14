import Mixin from 'ember-metal/mixin';
import service from 'ember-service/inject';

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
    }
  }
});