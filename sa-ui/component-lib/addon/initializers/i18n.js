/*
 * @file i18n initializer
 * initializer that injects the i18n class as a singleton throughout the app.
 * @public
 */
export function initialize(application) {
  application.inject('component', 'i18n', 'service:i18n');
  application.inject('controller', 'i18n', 'service:i18n');
  application.inject('helper', 'i18n', 'service:i18n');
  application.inject('route', 'i18n', 'service:i18n');

  /*
   * We have to inject explicitly into services, since Ember is not smart enough to ignore the i18n service,
   * and it tries to inject into itself, which throws errors.
   */
  application.inject('service:contextual-help', 'i18n', 'service:i18n');
  application.inject('service:date-format', 'i18n', 'service:i18n');
}

export default {
  name: 'i18n',
  initialize
};
