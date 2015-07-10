import Ember from 'ember';
import Application from '../../app';
//import Router from '../../router';
import config from '../../config/environment';
import initializeTestHelpers from 'simple-auth-testing/test-helpers';
import Test from 'simple-auth-testing/authenticators/test';

initializeTestHelpers();

export default function startApp(attrs) {
  var application;

  var attributes = Ember.merge({}, config.APP);
  attributes = Ember.merge(attributes, attrs); // use defaults, but you can override;

  Ember.run(function() {
    application = Application.create(attributes);
    application.setupForTesting();
    application.injectTestHelpers();
    authenticateSession();
    localStorage.setItem("rsa-i18n-default-locale", "en");
  });

  return application;
}
