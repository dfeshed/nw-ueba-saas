/* global authenticateSession */

import Ember from 'ember';
import Application from '../../app';
import config from '../../config/environment';
import './authenticate-session';
import registerPowerSelectHelpers from '../../tests/helpers/ember-power-select';

const {
  assign,
  run
} = Ember;

registerPowerSelectHelpers();

export default function startApp(attrs) {
  let application;

  // use defaults, but you can override
  const attributes = assign({}, config.APP, attrs);

  run(() => {
    application = Application.create(attributes);
    application.setupForTesting();
    application.injectTestHelpers();
    localStorage.setItem('rsa-i18n-default-locale', 'en');
    authenticateSession();
  });

  return application;
}
