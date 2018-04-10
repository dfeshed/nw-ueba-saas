/* eslint-env node */
'use strict';

const mockPort = process.env.MOCK_PORT || 9999;
const mockServerUrl = `http://localhost:${mockPort}`;

module.exports = function(environment/* , appConfig */) {
  const ENV = {
    modulePrefix: 'investigate-hosts',
    mockServerUrl,
    mockPort,
    environment,
    APP: {
      // Here you can pass flags/options to your application instance
      // when it is created
      readyDelay: 0 // 1500,
    },
    moment: {
      includeLocales: ['en'],
      includeTimezone: '2010-2020'
    },
    i18n: {
      defaultLocale: 'en-us',
      defaultFallback: true,
      includedLocales: ['en-us']
    }
  };

  if (environment === 'test') {
    ENV['ember-tether'] = {
      bodyElementId: 'ember-testing'
    };
  }
  return ENV;
};
