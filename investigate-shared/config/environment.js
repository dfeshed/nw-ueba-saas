/* eslint-env node */
'use strict';

const mockPort = process.env.MOCK_PORT || 9999;
const mockServerUrl = `http://localhost:${mockPort}`;

module.exports = function(environment/* , appConfig */) {
  const ENV = {
    flashMessageDefaults: {
      timeout: 5000,
      iconSize: 'larger',
      iconStyle: 'lined',
      type: 'info',
      types: ['info', 'success', 'warning', 'error']
    },
    modulePrefix: 'investigate-shared',
    mockServerUrl,
    mockPort,
    environment,
    moment: {
      includeLocales: true,
      includeTimezone: 'all'
    },
    APP: {
    }
  };

  if (environment === 'test') {
    ENV['ember-tether'] = {
      bodyElementId: 'ember-testing'
    };

    // Testem prefers this...
    ENV.locationType = 'none';
    ENV.APP.rootElement = '#ember-testing';
    ENV.APP.autoboot = false;
  }

  return ENV;
};
