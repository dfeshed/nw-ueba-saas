/* eslint-env node */
'use strict';

const mockPort = process.env.MOCK_PORT || 9999;
const mockServerUrl = `http://localhost:${mockPort}`;

module.exports = function(environment/* , appConfig */) {
  return {
    modulePrefix: 'admin',
    mockServerUrl,
    mockPort,
    environment,
    APP: {
      // Here you can pass flags/options to your application instance
      // when it is created
      readyDelay: 0 // 1500,
    },
    moment: {
      includeLocales: ['en', 'ja'],
      includeTimezone: 'subset'
    },
    i18n: {
      defaultLocale: 'en'
    }
  };
};