/* global authenticateSession */

import Ember from 'ember';
import Application from '../../app';
import config from '../../config/environment';
import './authenticate-session';

const {
  merge,
  run
} = Ember;

export default function startApp(attrs) {
  let application;

  let attributes = merge({}, config.APP);
  attributes = merge(attributes, attrs); // use defaults, but you can override;

  run(() => {
    application = Application.create(attributes);
    application.setupForTesting();
    application.injectTestHelpers();
    localStorage.setItem('rsa-i18n-default-locale', 'en');
    authenticateSession();
  });

  return application;
}
