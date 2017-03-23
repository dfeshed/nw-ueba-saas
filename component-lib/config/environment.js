/* eslint-env node */
'use strict';

module.exports = function(/* environment, appConfig */) {
  return {
    dateFormatDefault: 'MM/dd/yyyy',
    timeFormatDefault: 'HR24',
    timezoneDefault: 'UTC',
    i18n: {
      defaultLocale: 'en',
      includedLocales: ['en', 'ja']
    }
  };
};
