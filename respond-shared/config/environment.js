'use strict';

const mockPort = process.env.MOCK_PORT || 9999;
const mockServerUrl = `http://localhost:${mockPort}`;

module.exports = function(environment/* , appConfig */) {
  const ENV = {
    modulePrefix: 'respond-shared',
    mockServerUrl,
    mockPort,
    environment,
    APP: {
      readyDelay: 0
    },
    moment: {
      includeLocales: true,
      includeTimezone: 'all'
    },
    i18n: {
      defaultLocale: 'en-us',
      defaultFallback: true,
      includedLocales: ['en-us']
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
