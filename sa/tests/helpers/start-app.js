import Ember from 'ember';
import Application from '../../app';
import config from '../../config/environment';
import './authenticate-session';
export default function startApp(attrs) {
  let application;

  let attributes = Ember.merge({}, config.APP);
  attributes = Ember.merge(attributes, attrs); // use defaults, but you can override;

  Ember.run(() => {
    application = Application.create(attributes);
    application.setupForTesting();
    application.injectTestHelpers();
    localStorage.setItem('rsa-i18n-default-locale', 'en');
    authenticateSession();
  });

  return application;
}
