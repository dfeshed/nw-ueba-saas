/* eslint-env node */
'use strict';

const mockPort = process.env.MOCK_PORT || 9999;
const mockServerUrl = `http://localhost:${mockPort}`;

module.exports = function(environment/* , appConfig */) {
  const ENV = {
    modulePrefix: 'investigate',
    mockServerUrl,
    mockPort,
    environment,
    APP: {
      // Here you can pass flags/options to your application instance
      // when it is created
      readyDelay: 0,
      uebaTimeout: 100
    },
    moment: {
      includeLocales: ['en', 'ja'],
      includeTimezone: 'subset'
    },
    i18n: {
      defaultLocale: 'en'
    }
  };

  if (environment === 'test') {
    ENV.APP.uebaTimeout = 1;
  }

  return ENV;
};
