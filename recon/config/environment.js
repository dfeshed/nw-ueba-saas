/* eslint-env node */
'use strict';

module.exports = function(environment/* , appConfig */) {
  const ENV = {
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
