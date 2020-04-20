/* eslint-env node */
'use strict';

module.exports = function(environment) {
  let ENV = {
    moment: {
      includeLocales: true,
      includeTimezone: 'all'
    },
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
