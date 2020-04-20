/* eslint-env node */
'use strict';

module.exports = function(environment/* , appConfig */) {
  const ENV = {
    modulePrefix: 'test-helpers',
    environment,
    APP: {
    }
  };

  if (environment === 'test') {
    // Testem prefers this...
    ENV.locationType = 'none';
    ENV.APP.rootElement = '#ember-testing';
    ENV.APP.autoboot = false;
  }

  return ENV;
};
