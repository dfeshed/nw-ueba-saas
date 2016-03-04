/*
* @file i18n initializer
* initializer that injects the i18n class as a singleton throughout the app.
* @public
*/
export function initialize(application) {
  application.inject('model', 'i18n', 'service:i18n');
  application.inject('route', 'i18n', 'service:i18n');
  application.inject('controller', 'i18n', 'service:i18n');
  application.inject('component', 'i18n', 'service:i18n');
  application.inject('view', 'i18n', 'service:i18n');
}

export default {
  name: 'i18n',
  initialize
};
