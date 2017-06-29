/* eslint-env node */
'use strict';

const contextLookup = require('./context-lookup');

const mockPort = process.env.MOCK_PORT || 9999;
const mockServerUrl = `http://localhost:${mockPort}`;

module.exports = function(environment/* , appConfig */) {
  return {
    mockServerUrl,
    mockPort,
    modulePrefix: 'investigate',
    environment,
    contextLookup,
    APP: {
      // Here you can pass flags/options to your application instance
      // when it is created
      readyDelay: 0 // 1500,
    },
    moment: {
      includeLocales: ['en', 'ja'],
      includeTimezone: 'subset'
    }
  };
};