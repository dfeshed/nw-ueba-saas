/* eslint-env node */
'use strict';

module.exports = function(/* environment, appConfig */) {
  return {
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
