/* eslint-disable */

var mockPort = process.env.MOCK_PORT || 9999;
var mockServerUrl = "http://localhost:" + mockPort;

module.exports = function(environment/* , appConfig */) {
  return {
    modulePrefix: 'respond',
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
      includeTimezone: '2010-2020'
    }
  };
};