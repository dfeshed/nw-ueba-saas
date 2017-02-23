import Ember from 'ember';

const {
  Mixin,
  inject: {
    service
  },
  String: {
    htmlSafe
  },
  isNone
} = Ember;

export default Mixin.create({

  fatalErrors: service(),
  flashMessages: service(),
  i18n: service(),

  /**
   * @description Displays a flash notification used when error is detected loading models
   * @param {string} modelName - the i18n key to display as the model name in the translated message
   * @param {boolean} [isWarning=true] - when true (default) a warning-type flash is used.
   * Otherwise an error-type message is displayed.
   * @private
   */
  displayFlashErrorLoadingModel(modelName, isWarning = true) {
    const i18nKey = 'responded.errors.unableToLoadModel';
    const options = { model: this.get('i18n').t(`responded.models.${modelName}`) };
    if (isWarning) {
      this.displayWarningFlashMessage(i18nKey, options);
    } else {
      this.displayErrorFlashMessage(i18nKey, options);
    }
  },

  /**
   * @description Displays a flash notification with an attribute name and the action performed.
   * eg: - Assignee was updated.
   *     - Note was deleted.
   * @param {Object} [options] - Optional argument to display the field-name and the action.
   *  `actionName`: the i18n key of the action . Default is 'incident.edit.actions.updateRecord'
   *  `attributeName`: the i18n key of the field. If not provided a generic message is used instead
   *  `enableNotification`: Default to true. When false the notification wont be displayed.
   * @private
   */
  displayEditFieldSuccessMessage(options) {
    const {
      actionName = 'incident.edit.actions.updateRecord',
      enableNotification = true,
      attributeName
    } = options;

    if (enableNotification) {
      let message, i18nOptions;
      if (isNone(attributeName)) {
        message = 'incident.edit.update.singleSuccessfulMessage';
      } else {
        message = 'incident.edit.attributeActionSuccessfulMessage';
        i18nOptions = {
          attribute: this.get('i18n').t(attributeName),
          action: this.get('i18n').t(actionName)
        };
      }
      this.displaySuccessFlashMessage(message, i18nOptions);
    }
  },

  /**
   * @description Displays a flash success notification
   * @private
   */
  displaySuccessFlashMessage(i18nKey, i18nOptions = {}) {
    const message = this.get('i18n').t(i18nKey, i18nOptions);
    this.get('flashMessages').success(message, { iconName: 'check-circle-2' });
  },

  /**
   * @description Displays a flash warning notification
   * @private
   */
  displayWarningFlashMessage(i18nKey, i18nOptions = {}) {
    const message = this.get('i18n').t(i18nKey, i18nOptions);
    this.get('flashMessages').warning(message, { iconName: 'report-problem-circle' });
  },

  /**
   * @description Displays a flash error notification
   * @private
   */
  displayErrorFlashMessage(i18nKey, i18nOptions = {}) {
    const message = this.get('i18n').t(i18nKey, i18nOptions);
    this.get('flashMessages').error(message, { iconName: 'delete-1' });
  },

  /**
   * @description Displays a fatal error with the Unexpected-Error message
   * @private
   */
  displayFatalUnexpectedError() {
    this.get('fatalErrors').logError(htmlSafe(this.get('i18n').t('responded.errors.unexpected')));
  },

  /**
   * @description Displays a fatal error with the Timeout-Error message
   * @private
   */
  displayFatalTimeoutError() {
    this.get('fatalErrors').logError(htmlSafe(this.get('i18n').t('responded.errors.timeout')));
  }
});