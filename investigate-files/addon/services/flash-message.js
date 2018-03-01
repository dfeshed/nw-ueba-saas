import Service, { inject as service } from '@ember/service';

export default Service.extend({

  fatalErrors: service(),

  flashMessages: service(),

  i18n: service(),

  /**
   * Convenience method for showing a flash success/error message to the user on update or failure
   * @method showFlashMessage
   * @public
   * @param i18nKey
   * @param context
   */
  showFlashMessage(i18nKey, context) {
    const { i18n, flashMessages } = this.getProperties('i18n', 'flashMessages');
    flashMessages.success(i18n.t(i18nKey, context), { iconName: 'check-circle-2' });
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
  }
});
