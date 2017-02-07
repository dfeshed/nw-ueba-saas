/* eslint-disable */

var mockPort = process.env.MOCK_PORT || 9999;
var mockServerUrl = "http://localhost:" + mockPort;
var useMockServer = !process.env.NOMOCK;

module.exports = function(environment/* , appConfig */) {
  return {
    useMockServer,
    mockServerUrl,
    mockPort,
    modulePrefix: 'live-content',
    environment,
    APP: {
      // Here you can pass flags/options to your application instance
      // when it is created
      readyDelay: 0 // 1500,
    },
    moment: {
      includeLocales: ['en', 'ja'],
      includeTimezone: '2010-2020'
    },
    'i18n': {
      defaultLocale: 'en-us',
      includedLocales: ['en-us', 'ja']
    }
  };
};