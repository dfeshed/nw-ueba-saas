/* eslint-env node */
'use strict';

const mockPort = process.env.MOCK_PORT || 9999;
const mockServerUrl = `http://localhost:${mockPort}`;
const useMockServer = !process.env.NOMOCK;

module.exports = function(/* environment, appConfig */) {
  return {
    useMockServer,
    mockServerUrl,
    mockPort,
    moment: {
      includeLocales: true,
      includeTimezone: 'all'
    },
    i18n: {
      defaultLocale: 'en-us',
      defaultFallback: true,
      includedLocales: ['en-us']
    }
  };
};
