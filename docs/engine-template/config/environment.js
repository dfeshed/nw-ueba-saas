/* eslint-env node */

module.exports = function(environment/* , appConfig */) {
  return {
    modulePrefix: 'changeme',
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