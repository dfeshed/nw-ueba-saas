/* eslint-env node */
'use strict';

const mockPort = process.env.MOCK_PORT || 9999;
const mockServerUrl = `http://localhost:${mockPort}`;

module.exports = function(environment) {
  let ENV = {
    modulePrefix: 'health-wellness',
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
    // Testem prefers this...
    ENV.locationType = 'none';
    ENV.APP.rootElement = '#ember-testing';
    ENV.APP.autoboot = false;
  }

  return ENV;
};