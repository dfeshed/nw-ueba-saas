import Service from 'ember-service';
import service from 'ember-service/inject';

export default Service.extend({

  flashMessages: service(),

  i18n: service(),

  /**
   * Convenience method for showing a flash success message to the user
   * @method showFlashMessage
   * @public
   * @param i18nKey
   */
  showFlashMessage(i18nKey) {
    const { i18n, flashMessages } = this.getProperties('i18n', 'flashMessages');
    flashMessages.success(i18n.t(i18nKey), { iconName: 'check-circle-2' });
  },

  /**
   * Convenience method for showing a flash error message to the user
   * @method showErrorMessage
   * @public
   * @param message
   */
  showErrorMessage(message) {
    const { flashMessages } = this.getProperties('flashMessages');
    const type = {
      name: 'error',
      icon: 'delete-1'
    };
    flashMessages[type.name](message, { iconName: type.icon });
  }
});
